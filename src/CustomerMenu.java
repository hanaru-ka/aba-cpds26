
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/** Customer main menu. */
public class CustomerMenu {
    private final User user;
    private final ProductCatalog catalog;
    private final RestockMonitor monitor;
    private final Scanner in;
    private final Cart cart = new Cart();
    private final OrderHistory history = new OrderHistory();

    public CustomerMenu(User user, ProductCatalog catalog,
                        RestockMonitor monitor, Scanner in) {
        this.user = user;
        this.catalog = catalog;
        this.monitor = monitor;
        this.in = in;
    }

    public void run() {
        while (true) {
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("Welcome, " + user.getUsername() + "!");
            System.out.println("1. Browse Products by Category");
            System.out.println("2. Logout");

            System.out.print("Choose an option: ");

            if (!in.hasNextLine()) {
                return;
            }

            String choice = in.nextLine().trim();

            if (choice.equals("1")) {
                browseByCategory();
            } else if (choice.equals("2")) {
                System.out.println("Logging out...");
                break;
            } else {
                System.out.println("Invalid option. Please try again.");
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

        System.out.print("\nChoose a category: ");
        String choice = in.nextLine().trim();

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
            System.out.println("\nProduct: " + product.getName());
            System.out.println("Price: PHP " + product.getFinalPrice());
            System.out.println("Stock: " + product.getStock());

            if (product.isOutOfStock()) {
                System.out.println("Status: Out of Stock");
            } else if (monitor.isLowStock(product)) {
                System.out.println("Status: Running Low");
            } else {
                System.out.println("Status: Available");
            }
        }
    }

    // Other customer functions will be implemented separately.
    private void searchProducts() { /* TODO: search by name or category */ }
    private void sortProducts() { /* TODO: sort by name, price, or stock */ }
    private void manageCart() { /* TODO: add, edit quantity, remove */ }
    private void checkout() { /* TODO: select items, pay, deduct stock, save receipt */ }
    private void viewOrderHistory() { /* TODO: show past orders */ }
    private void reorder() { /* TODO: re-buy one product or whole cart, skip out-of-stock */ }
}
