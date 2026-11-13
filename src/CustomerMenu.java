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

    public CustomerMenu(User user, ProductCatalog catalog, RestockMonitor monitor) {
        this.user = user;
        this.catalog = catalog;
        this.monitor = monitor;
    }

    public void run() {
        // TODO: menu loop until logout
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

        Scanner in = new Scanner(System.in);

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
            } else if (product.isLowStock(RestockMonitor.LOW_STOCK_THRESHOLD)) {
                System.out.println("Status: Running Low");
            } else {
                System.out.println("Status: Available");
            }
        }
    }

    //to do toh
    private void searchProducts() { /* TODO: search by name or category */ }
    private void sortProducts() { /* TODO: sort by name, price, or stock */ }
    private void manageCart() { /* TODO: add, edit quantity, remove */ }
    private void checkout() { /* TODO: select items, pay, deduct stock, save receipt */ }
    private void viewOrderHistory() { /* TODO: show past orders */ }
    private void reorder() { /* TODO: re-buy one product or whole cart, skip out-of-stock */ }
}
