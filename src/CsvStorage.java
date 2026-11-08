import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Reads and writes accounts.csv, products.csv, and orders.csv. */
public class CsvStorage {
    private static final String ACCOUNTS_FILE = "accounts.csv";
    private static final String PRODUCTS_FILE = "products.csv";
    private static final String ORDERS_FILE = "orders.csv";

    public Map<String, User> loadAccounts() {
        // TODO: read username, salt, passwordHash, role
        return new HashMap<>();
    }

    public void saveAccounts(Map<String, User> accounts) {
        // TODO: write accounts.csv
    }

    public List<Product> loadProducts() {
        // TODO: read productId, name, category, price, stock, discount, specialOffer
        return new ArrayList<>();
    }

    public void saveProducts(List<Product> products) {
        // TODO: write products.csv
    }

    public List<Order> loadOrders() {
        // TODO: read orderId, username, date, paymentMode, items, total
        return new ArrayList<>();
    }

    public void saveOrder(Order order) {
        // TODO: append to orders.csv
    }

    public void saveReceipt(Order order) {
        // TODO: write receipt text to the receipts folder or file
    }
}
