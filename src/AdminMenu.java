/** Admin main menu. */
public class AdminMenu {
    private final ProductCatalog catalog;
    private final RestockMonitor monitor;

    public AdminMenu(ProductCatalog catalog, RestockMonitor monitor) {
        this.catalog = catalog;
        this.monitor = monitor;
    }

    public void run() {
        // TODO: show restock alerts first, then menu loop until logout
    }

    private void showRestockAlerts() { /* TODO: monitor.getMostUrgent(...) */ }
    private void checkStockBySection() { /* TODO: list stock for one category */ }
    private void addProduct() { /* TODO: catalog.addProduct */ }
    private void editProduct() { /* TODO: name, price, stock, discount, special offer */ }
    private void removeProduct() { /* TODO: catalog.removeProduct */ }
}
