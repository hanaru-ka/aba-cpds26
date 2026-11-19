
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
        System.out.println("=== RESTOCK ALERTS ===");

        List<Product> products =
                monitor.getMostUrgent(catalog.getAllProducts().size());

        boolean found = false;

        System.out.printf("%-10s %-24s %12s %8s  %s%n",
                "Product ID", "Product", "Price", "Stock", "Status");
        System.out.println("-".repeat(75));

        for (Product product : products) {
            if (product.isOutOfStock() || monitor.isLowStock(product)) {

                String status = product.isOutOfStock()
                        ? "Out of Stock"
                        : "Running Low";

                System.out.printf("%-10s %-24s %12s %8d  %s%n",
                        product.getProductId(),
                        product.getName(),
                        String.format("PHP %.2f", product.getFinalPrice()),
                        product.getStock(),
                        status);

                found = true;
            }
        }

        if (!found) {
            System.out.println("No products currently need restocking.");
        }
    }

    // ---------- Check Stock by Section ----------

    private void checkStockBySection() {
        ConsoleUtil.clear();
        System.out.println("=== STOCK BY SECTION ===");

        List<String> categories = new ArrayList<>();

        for (Product product : catalog.getAllProducts()) {
            if (!categories.contains(product.getCategory())) {
                categories.add(product.getCategory());
            }
        }

        if (categories.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }

        int choice = readInt("Choose a section: ", 1, categories.size());

        String selectedCategory = categories.get(choice - 1);
        List<Product> products = catalog.getByCategory(selectedCategory);

        System.out.println("\n=== " + selectedCategory.toUpperCase() + " ===");

        printProductList(products);
    }

    // ---------- Add Product ----------

    private void addProduct() {
        ConsoleUtil.clear();

        System.out.println("=== ADD PRODUCT ===");
        System.out.println("\nCURRENT PRODUCT CATALOG");

        // Display all existing products before adding a new one.
        printProductList(catalog.getAllProducts());

        System.out.println("\n=== NEW PRODUCT DETAILS ===");

        String id = readRequired("Product ID: ").toUpperCase();

        if (catalog.getProduct(id) != null) {
            System.out.println("A product with that ID already exists.");
            return;
        }

        String name = readRequired("Product Name: ");
        String category = readRequired("Category: ");
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

        System.out.println("\nProduct added successfully!");

        System.out.println("\nUPDATED PRODUCT CATALOG");
        printProductList(catalog.getAllProducts());
    }

    // ---------- Edit Product ----------

    private void editProduct() {
        ConsoleUtil.clear();

        System.out.println("=== EDIT PRODUCT ===");
        printProductList(catalog.getAllProducts());

        String id = ask("\nEnter Product ID to edit: ");

        if (id == null || id.isEmpty()) {
            return;
        }

        Product product = catalog.getProduct(id.toUpperCase());

        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("\nCurrent Product Details:");
        displayProduct(product);

        System.out.println("\nPress Enter to keep the current value.");

        String name = ask("New Name [" + product.getName() + "]: ");
        String category = ask(
                "New Category [" + product.getCategory() + "]: "
        );

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
        catalog.removeProduct(id.toUpperCase());

        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }

        if (category != null && !category.isEmpty()) {
            product.setCategory(category);
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
    }

    // ---------- Remove Product ----------

    private void removeProduct() {
        ConsoleUtil.clear();

        System.out.println("=== REMOVE PRODUCT ===");
        printProductList(catalog.getAllProducts());

        String id = ask("\nEnter Product ID to remove: ");

        if (id == null || id.isEmpty()) {
            return;
        }

        Product product = catalog.getProduct(id.toUpperCase());

        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("\nProduct to be removed:");
        displayProduct(product);

        String confirmation = ask(
                "\nAre you sure you want to remove this product? (Y/N): "
        );

        if (confirmation != null && confirmation.equalsIgnoreCase("Y")) {
            catalog.removeProduct(id.toUpperCase());
            monitor.rebuild(catalog.getAllProducts());

            System.out.println("Product removed successfully!");
        } else {
            System.out.println("Product removal cancelled.");
        }
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
