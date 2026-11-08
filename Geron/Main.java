import java.util.List;

/** Entry point: loads CSV data, then shows the login screen. */
public class Main {
    public static void main(String[] args) {
        CsvStorage storage = new CsvStorage();
        AuthManager auth = new AuthManager(storage.loadAccounts());
        ProductCatalog catalog = new ProductCatalog(storage.loadProducts());
        RestockMonitor monitor = new RestockMonitor(catalog.getAllProducts());

        // TODO: loop at login screen: Register / Login / Exit
        // TODO: after login, open CustomerMenu or AdminMenu based on role
        // TODO: save all data to CSV on exit
    }
}
