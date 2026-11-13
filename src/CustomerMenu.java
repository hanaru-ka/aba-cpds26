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

    private void browseByCategory() { /* TODO: show products with "running low" labels */ }
    private void searchProducts() { /* TODO: search by name or category */ }
    private void sortProducts() { /* TODO: sort by name, price, or stock */ }
    private void manageCart() { /* TODO: add, edit quantity, remove */ }
    private void checkout() { /* TODO: select items, pay, deduct stock, save receipt */ }
    private void viewOrderHistory() { /* TODO: show past orders */ }
    private void reorder() { /* TODO: re-buy one product or whole cart, skip out-of-stock */ }
}
