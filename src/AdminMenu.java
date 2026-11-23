import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Comparator;

/** Admin main menu. */
public class AdminMenu {
    private final ProductCatalog catalog;
    private final RestockMonitor monitor;
    private final Scanner in;

    public AdminMenu(ProductCatalog catalog, RestockMonitor monitor, Scanner in) {
        this.catalog = catalog;
        this.monitor = monitor;
        this.in = in;
    }

    public void run() {
        while (true) {
            ConsoleUtil.clear();

            System.out.println("=== ADMIN MENU ===");
            System.out.println("1. View Restock Alerts");
            System.out.println("2. Check Stock by Section");
            System.out.println("3. Add Product");
            System.out.println("4. Edit Product");
            System.out.println("5. Remove Product");
            System.out.println("0. Logout");

            String choice = ask("Choose an option: ");

            if (choice == null || choice.equals("0")) {
                System.out.println("Logging out...");
                return;
            }

            if (choice.equals("1")) {
                showRestockAlerts();
                ConsoleUtil.pause(in);
            } else if (choice.equals("2")) {
                checkStockBySection();
                ConsoleUtil.pause(in);
            } else if (choice.equals("3")) {
                addProduct();
                ConsoleUtil.pause(in);
            } else if (choice.equals("4")) {
                editProduct();
                ConsoleUtil.pause(in);
            } else if (choice.equals("5")) {
                removeProduct();
                ConsoleUtil.pause(in);
            } else {
                System.out.println("Invalid option. Please try again.");
                ConsoleUtil.pause(in);
            }
        }
    }

    // ---------- View Restock Alerts ----------

    private void showRestockAlerts() {
        ConsoleUtil.clear();
        System.out.println("=== RESTOCK ALERTS BY CATEGORY ===");

        List<String> categories = getCategories();
        boolean found = false;

        if (categories.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        for (String category : categories) {
            List<Product> products = catalog.getByCategory(category);
            List<Product> alertProducts = new ArrayList<>();

            for (Product product : products) {
                if (product.isOutOfStock() || monitor.isLowStock(product)) {
                    alertProducts.add(product);
                }
            }

            if (alertProducts.isEmpty()) {
                continue;
            }

            found = true;
            System.out.println("\n=== " + category.toUpperCase() + " ===");
            printProductList(alertProducts);
        }

        if (!found) {
            System.out.println("No products currently need restocking.");
        }
    }

    // ---------- Check Stock by Section ----------

    private void checkStockBySection() {
        ConsoleUtil.clear();
        System.out.println("=== STOCK BY CATEGORY ===");

        List<String> categories = getCategories();

        if (categories.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }

        int choice = readInt("Choose a category: ", 1, categories.size());

        String selectedCategory = categories.get(choice - 1);
        List<Product> products = catalog.getByCategory(selectedCategory);

        System.out.println("\n=== " + selectedCategory.toUpperCase() + " ===");
        printProductList(products);
    }

    // ---------- Category Helper ----------

    private List<String> getCategories() {
        List<String> categories = new ArrayList<>();

        for (Product product : catalog.getAllProducts()) {
            boolean exists = false;

            for (String category : categories) {
                if (category.equalsIgnoreCase(product.getCategory())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                categories.add(product.getCategory());
            }
        }

        categories.sort(String.CASE_INSENSITIVE_ORDER);
        return categories;
    }

    // ---------- Add Product ----------

    private void addProduct() {
        ConsoleUtil.clear();

        System.out.println("=== ADD PRODUCT ===");
        String category = chooseCategoryForNewProduct();

        if (category == null) {
            System.out.println("No category selected. Product addition cancelled.");
            return;
        }

        System.out.println("\n=== PRODUCTS IN " + category.toUpperCase() + " ===");
        printProductList(catalog.getByCategory(category));

        System.out.println("\n=== NEW PRODUCT DETAILS ===");

        String id = readRequired("Product ID: ").toUpperCase();

        if (catalog.getProduct(id) != null) {
            System.out.println("A product with that ID already exists.");
            return;
        }

        String name = readRequired("Product Name: ");
        double price = readDouble("Price: ", 0);
        int stock = readInt("Stock: ", 0, Integer.MAX_VALUE);
        double discount = readDouble(
                "Discount percentage (0-100): ", 0, 100
        ) / 100.0;

        String offer = ask("Special Offer (enter None if there is no offer): ");

        if (offer == null || offer.isEmpty()) {
            offer = "None";
        }

        Product product = new Product(
                id, name, category, price, stock, discount, offer
        );

        catalog.addProduct(product);
        monitor.rebuild(catalog.getAllProducts());

        System.out.println("\nProduct added successfully to " + category + "!");
        System.out.println("\nUPDATED " + category.toUpperCase() + " CATEGORY");
        printProductList(catalog.getByCategory(category));
    }

    // ---------- Edit Product ----------

    private void editProduct() {
        ConsoleUtil.clear();

        System.out.println("=== EDIT PRODUCT BY CATEGORY ===");
        String category = chooseExistingCategory();

        if (category == null) {
            return;
        }

        List<Product> products = catalog.getByCategory(category);
        System.out.println("\n=== " + category.toUpperCase() + " ===");
        printProductList(products);

        Product product = chooseProductFromCategory(products);
        if (product == null) {
            return;
        }

        System.out.println("\nCurrent Product Details:");
        displayProduct(product);

        System.out.println("\nPress Enter to keep the current value.");

        String name = ask("New Name [" + product.getName() + "]: ");
        String newCategory = chooseCategoryForEdit(product.getCategory());

        double price = readDoubleOrKeep(
                "New Price [" + product.getPrice() + "]: ",
                product.getPrice(), 0
        );

        int stock = readIntOrKeep(
                "New Stock [" + product.getStock() + "]: ",
                product.getStock(), 0
        );

        double discount = readDoubleOrKeep(
                "New Discount Percentage [" +
                        (product.getDiscount() * 100) + "]: ",
                product.getDiscount() * 100, 0, 100
        ) / 100.0;

        String offer = ask(
                "New Special Offer [" + product.getSpecialOffer() + "]: "
        );

        // Remove the old catalog entry before changing its category or name.
        catalog.removeProduct(product.getProductId());

        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }

        if (newCategory != null && !newCategory.isEmpty()) {
            product.setCategory(newCategory);
        }

        product.setPrice(price);
        product.setStock(stock);
        product.setDiscount(discount);

        if (offer != null && !offer.isEmpty()) {
            product.setSpecialOffer(offer);
        }

        catalog.addProduct(product);
        monitor.rebuild(catalog.getAllProducts());

        System.out.println("\nProduct updated successfully!");
        System.out.println("\nUPDATED CATEGORY: " + product.getCategory().toUpperCase());
        printProductList(catalog.getByCategory(product.getCategory()));
    }

    // ---------- Remove Product ----------

    private void removeProduct() {
        ConsoleUtil.clear();

        System.out.println("=== REMOVE PRODUCT BY CATEGORY ===");
        String category = chooseExistingCategory();

        if (category == null) {
            return;
        }

        List<Product> products = catalog.getByCategory(category);
        System.out.println("\n=== " + category.toUpperCase() + " ===");
        printProductList(products);

        Product product = chooseProductFromCategory(products);
        if (product == null) {
            return;
        }

        System.out.println("\nProduct to be removed:");
        displayProduct(product);

        String confirmation = ask(
                "\nAre you sure you want to remove this product? (Y/N): "
        );

        if (confirmation != null && confirmation.equalsIgnoreCase("Y")) {
            catalog.removeProduct(product.getProductId());
            monitor.rebuild(catalog.getAllProducts());

            System.out.println("Product removed successfully from " + category + "!");
        } else {
            System.out.println("Product removal cancelled.");
        }
    }

    // ---------- Category and Product Selection ----------

    private String chooseCategoryForNewProduct() {
        List<String> categories = getCategories();

        System.out.println("\n=== SELECT PRODUCT CATEGORY ===");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.println((categories.size() + 1) + ". Add New Category");
        System.out.println("0. Cancel");

        int choice = readInt("Choose a category: ", 0, categories.size() + 1);

        if (choice == 0) {
            return null;
        }
        if (choice == categories.size() + 1) {
            return readRequired("New Category Name: ");
        }
        return categories.get(choice - 1);
    }

    private String chooseExistingCategory() {
        List<String> categories = getCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return null;
        }

        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.println("0. Cancel");

        int choice = readInt("Choose a category: ", 0, categories.size());
        if (choice == 0) {
            return null;
        }
        return categories.get(choice - 1);
    }

    private String chooseCategoryForEdit(String currentCategory) {
        List<String> categories = getCategories();

        System.out.println("\nCurrent category: " + currentCategory);
        System.out.println("Choose a new category, or press Enter to keep the current one:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.println((categories.size() + 1) + ". Add New Category");

        String input = ask("Category choice: ");
        if (input == null || input.isEmpty()) {
            return currentCategory;
        }

        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= categories.size()) {
                return categories.get(choice - 1);
            }
            if (choice == categories.size() + 1) {
                return readRequired("New Category Name: ");
            }
        } catch (NumberFormatException e) {
            // Keep current category if the input is invalid.
        }

        System.out.println("Invalid category choice. Keeping the current category.");
        return currentCategory;
    }

    private Product chooseProductFromCategory(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products in this category.");
            return null;
        }

        String id = ask("\nEnter Product ID (or press Enter to cancel): ");
        if (id == null || id.isEmpty()) {
            return null;
        }

        for (Product product : products) {
            if (product.getProductId().equalsIgnoreCase(id)) {
                return product;
            }
        }

        System.out.println("Product not found in the selected category.");
        return null;
    }

    // ---------- Product Display ----------

    private void printProductList(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        List<Product> sortedProducts = Sorter.mergeSort(
                products,
                Comparator.comparing(Product::getProductId)
        );

        System.out.printf("%-10s %-24s %12s %8s  %s%n",
                "Product ID", "Product", "Price", "Stock", "Status");
        System.out.println("-".repeat(75));

        for (Product product : sortedProducts) {
            String status;

            if (product.isOutOfStock()) {
                status = "Out of Stock";
            } else if (monitor.isLowStock(product)) {
                status = "Running Low";
            } else {
                status = "Available";
            }

            System.out.printf("%-10s %-24s %12s %8d  %s%n",
                    product.getProductId(),
                    product.getName(),
                    String.format("PHP %.2f", product.getFinalPrice()),
                    product.getStock(),
                    status);
        }
    }

    private void displayProduct(Product product) {
        System.out.println("Product ID: " + product.getProductId());
        System.out.println("Name: " + product.getName());
        System.out.println("Category: " + product.getCategory());
        System.out.println("Price: PHP " +
                String.format("%.2f", product.getPrice()));
        System.out.println("Stock: " + product.getStock());
        System.out.println("Discount: " +
                (product.getDiscount() * 100) + "%");
        System.out.println("Special Offer: " + product.getSpecialOffer());
    }

    // ---------- Input Helpers ----------

    private String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    private String readRequired(String prompt) {
        while (true) {
            String value = ask(prompt);

            if (value == null) {
                return "";
            }

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(ask(prompt));

                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Ask again.
            }

            System.out.println("Please enter a valid number.");
        }
    }

    private int readIntOrKeep(String prompt, int current, int min) {
        while (true) {
            String input = ask(prompt);

            if (input == null || input.isEmpty()) {
                return current;
            }

            try {
                int value = Integer.parseInt(input);

                if (value >= min) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Ask again.
            }

            System.out.println("Please enter a valid number.");
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            try {
                double value = Double.parseDouble(ask(prompt));

                if (Double.isFinite(value) && value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Ask again.
            }

            System.out.println("Please enter a valid amount.");
        }
    }

    private double readDouble(String prompt, double min) {
        return readDouble(prompt, min, Double.MAX_VALUE);
    }

    private double readDoubleOrKeep(
            String prompt, double current, double min) {

        while (true) {
            String input = ask(prompt);

            if (input == null || input.isEmpty()) {
                return current;
            }

            try {
                double value = Double.parseDouble(input);

                if (Double.isFinite(value) && value >= min) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Ask again.
            }

            System.out.println("Please enter a valid amount.");
        }
    }

    private double readDoubleOrKeep(
            String prompt, double current, double min, double max) {

        while (true) {
            String input = ask(prompt);

            if (input == null || input.isEmpty()) {
                return current;
            }

            try {
                double value = Double.parseDouble(input);

                if (Double.isFinite(value) && value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Ask again.
            }

            System.out.println("Please enter a valid amount.");
        }
    }
}
