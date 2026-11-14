import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/** Customer main menu. */
public class CustomerMenu {
    private final User user;
    private final ProductCatalog catalog;
    private final RestockMonitor monitor;
    private final Cart cart = new Cart();
    private final OrderHistory history = new OrderHistory();
    private final Scanner in;

    public CustomerMenu(User user, ProductCatalog catalog, RestockMonitor monitor, Scanner in) {
        this.user = user;
        this.catalog = catalog;
        this.monitor = monitor;
        this.in = in;
    }

    public void run() {
        while (true) {
            ConsoleUtil.clear();
            System.out.println("=== Customer Menu (" + user.getUsername() + ") ===");
            System.out.println("1. Browse by category");
            System.out.println("2. My cart");
            System.out.println("0. Logout");
            String choice = ask("Choose an option: ");
            if (choice == null || choice.equals("0")) return;
            if (choice.equals("1")) {
                browseByCategory();
                ConsoleUtil.pause(in);
            }
            else if (choice.equals("2")) {
                manageCart();
                ConsoleUtil.pause(in);
            }

            else {
                System.out.println("Invalid option. Please enter 1 or 0.");
                ConsoleUtil.pause(in);
            }
        }
    }

    private void browseByCategory() {
        List<Product> products = catalog.getAllProducts();
        List<String> categories = new ArrayList<>();

        for (Product product : products) {
            String category = product.getCategory();

            if (!categories.contains(category)) {
                categories.add(category);
            }
        }

        if (categories.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("\n=== PRODUCT CATEGORIES ===");

        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        String choice = ask("\nChoose a category: ");
        if (choice == null) return;

        int categoryNumber;

        try {
            categoryNumber = Integer.parseInt(choice);
        } catch (NumberFormatException e) {
            System.out.println("Invalid category selection.");
            return;
        }

        if (categoryNumber < 1 || categoryNumber > categories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }

        String selectedCategory = categories.get(categoryNumber - 1);

        List<Product> categoryProducts =
                catalog.getByCategory(selectedCategory);

        System.out.println("\n=== " + selectedCategory.toUpperCase() + " ===");

        for (Product product : categoryProducts) {
            System.out.println("\nProduct ID: " + product.getProductId());
            System.out.println("Product: " + product.getName());
            System.out.println("Price: PHP " + product.getFinalPrice());
            System.out.println("Stock: " + product.getStock());

            if (product.isOutOfStock()) {
                System.out.println("Status: Out of Stock");
            } else if (product.isLowStock(RestockMonitor.LOW_STOCK_THRESHOLD)) {
                System.out.println("Status: Running Low");
            } else {
                System.out.println("Status: Available");
            }
        }
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    private void manageCart() {
        while (true) {
            System.out.println("\n=== MY CART ===");
            printCart();

            System.out.println("\n1. Add item");
            System.out.println("2. Edit quantity");
            System.out.println("3. Remove item");
            System.out.println("0. Back");

            String choice = ask("Choose an option: ");
            if (choice == null || choice.equals("0")) return;

            if (choice.equals("1")) {
                addItemToCart();
            } else if (choice.equals("2")) {
                editCartQuantity();
            } else if (choice.equals("3")) {
                removeCartItem();
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private void printCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            System.out.println("\nProduct ID: " + product.getProductId());
            System.out.println("Product: " + product.getName());
            System.out.println("Quantity: " + item.getQuantity());
            System.out.printf("Subtotal: PHP %.2f%n", item.getSubtotal());
        }
        System.out.printf("%nTotal: PHP %.2f%n", cart.getTotal());
    }

    private void addItemToCart() {
        String id = ask("Product ID: ");
        if (id == null || id.isEmpty()) return;

        Product product = catalog.getProduct(id.trim().toUpperCase());
        if (product == null) {
            System.out.println("No product with that ID.");
            return;
        }
        if (product.isOutOfStock()) {
            System.out.println(product.getName() + " is out of stock.");
            return;
        }

        String qtyText = ask("Quantity: ");
        int quantity = parsePositiveInt(qtyText);
        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        int alreadyInCart = 0;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }
        if (alreadyInCart + quantity > product.getStock()) {
            System.out.println("Not enough stock. Available: " + product.getStock()
                    + ", already in cart: " + alreadyInCart + ".");
            return;
        }

        cart.addItem(product, quantity);
        System.out.println("Added " + quantity + " x " + product.getName() + " to your cart.");
    }

    private void editCartQuantity() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        String id = ask("Product ID to edit: ");
        if (id == null || id.isEmpty()) return;

        String qtyText = ask("New quantity: ");
        int quantity = parsePositiveInt(qtyText);
        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        Product product = catalog.getProduct(id.trim().toUpperCase());
        if (product != null && quantity > product.getStock()) {
            System.out.println("Not enough stock. Available: " + product.getStock() + ".");
            return;
        }

        if (cart.updateQuantity(id.trim().toUpperCase(), quantity)) {
            System.out.println("Quantity updated.");
        } else {
            System.out.println("That product is not in your cart.");
        }
    }

    private void removeCartItem() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        String id = ask("Product ID to remove: ");
        if (id == null || id.isEmpty()) return;

        if (cart.removeItem(id.trim().toUpperCase())) {
            System.out.println("Removed from your cart.");
        } else {
            System.out.println("That product is not in your cart.");
        }
    }

    private int parsePositiveInt(String text) {
        if (text == null) return 0;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }




    //to do toh
    private void searchProducts() { /* TODO: search by name or category */ }
    private void sortProducts() { /* TODO: sort by name, price, or stock */ }

    private void checkout() { /* TODO: select items, pay, deduct stock, save receipt */ }
    private void viewOrderHistory() { /* TODO: show past orders */ }
    private void reorder() { /* TODO: re-buy one product or whole cart, skip out-of-stock */ }
}
