import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reads and writes accounts.csv, products.csv, and orders.csv (in a "data" folder) and receipts. */
public class CsvStorage {
    private static final String DATA_DIR = "data";
    private static final String RECEIPTS_DIR = "receipts";
    private static final String ACCOUNTS_FILE = DATA_DIR + "/accounts.csv";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.csv";
    private static final String ORDERS_FILE = DATA_DIR + "/orders.csv";

    private static final String ACCOUNTS_HEADER = "username,salt,passwordHash,role";
    private static final String PRODUCTS_HEADER = "productId,name,category,price,stock,discount,specialOffer";
    private static final String ORDERS_HEADER = "orderId,username,date,paymentMode,items,total";

    // ---------- Accounts ----------

    public Map<String, User> loadAccounts() {
        Map<String, User> accounts = new LinkedHashMap<>();
        for (String[] r : readRows(ACCOUNTS_FILE, 4)) {
            User user = r[3].equalsIgnoreCase("ADMIN")
                    ? new Admin(r[0], r[1], r[2])
                    : new Customer(r[0], r[1], r[2]);
            accounts.put(r[0], user);
        }
        return accounts;
    }

    public void saveAccounts(Map<String, User> accounts) {
        List<String> lines = new ArrayList<>();
        lines.add(ACCOUNTS_HEADER);
        for (User u : accounts.values()) {
            lines.add(csv(u.getUsername()) + "," + csv(u.getSalt()) + ","
                    + csv(u.getPasswordHash()) + "," + csv(u.getRole()));
        }
        writeAll(ACCOUNTS_FILE, lines);
    }

    // ---------- Products ----------

    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        for (String[] r : readRows(PRODUCTS_FILE, 6)) {
            try {
                products.add(new Product(r[0], r[1], r[2],
                        Double.parseDouble(r[3]), Integer.parseInt(r[4]),
                        Double.parseDouble(r[5]), r.length > 6 ? r[6] : ""));
            } catch (NumberFormatException e) {
                System.err.println("Skipping bad product row: " + String.join(",", r));
            }
        }
        return products;
    }

    public void saveProducts(List<Product> products) {
        List<String> lines = new ArrayList<>();
        lines.add(PRODUCTS_HEADER);
        for (Product p : products) {
            lines.add(csv(p.getProductId()) + "," + csv(p.getName()) + "," + csv(p.getCategory()) + ","
                    + p.getPrice() + "," + p.getStock() + "," + p.getDiscount() + ","
                    + csv(p.getSpecialOffer()));
        }
        writeAll(PRODUCTS_FILE, lines);
    }

    // ---------- Orders ----------

    /**
     * Loads past orders. The product list is needed to rebuild each order's items from
     * their product IDs; items whose product no longer exists are skipped.
     */
    public List<Order> loadOrders(List<Product> products) {
        Map<String, Product> byId = new HashMap<>();
        for (Product p : products) byId.put(p.getProductId(), p);

        List<Order> orders = new ArrayList<>();
        for (String[] r : readRows(ORDERS_FILE, 6)) {
            try {
                List<CartItem> items = new ArrayList<>();
                if (!r[4].isEmpty()) {
                    for (String entry : r[4].split(";")) {
                        String[] parts = entry.split(":");
                        Product p = byId.get(parts[0]);
                        if (p != null && parts.length == 2) {
                            items.add(new CartItem(p, Integer.parseInt(parts[1])));
                        }
                    }
                }
                orders.add(new Order(r[0], r[1], r[2], r[3], items, Double.parseDouble(r[5])));
            } catch (NumberFormatException e) {
                System.err.println("Skipping bad order row: " + String.join(",", r));
            }
        }
        return orders;
    }

    /** Appends one order to orders.csv. */
    public void saveOrder(Order order) {
        StringBuilder items = new StringBuilder();
        for (CartItem item : order.getItems()) {
            if (items.length() > 0) items.append(';');
            items.append(item.getProduct().getProductId()).append(':').append(item.getQuantity());
        }
        String row = csv(order.getOrderId()) + "," + csv(order.getUsername()) + ","
                + csv(order.getDate()) + "," + csv(order.getPaymentMode()) + ","
                + items + "," + String.format("%.2f", order.getTotal());
        try {
            Path path = Paths.get(ORDERS_FILE);
            Files.createDirectories(Paths.get(DATA_DIR));
            List<String> out = new ArrayList<>();
            if (!Files.exists(path)) out.add(ORDERS_HEADER);
            out.add(row);
            Files.write(path, out, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not save order: " + e.getMessage());
        }
    }

    /** Writes the receipt text to receipts/receipt_<orderId>.txt. */
    public void saveReceipt(Order order) {
        String text = order.generateReceipt();
        if (text == null || text.trim().isEmpty()) text = basicReceipt(order);
        try {
            Files.createDirectories(Paths.get(RECEIPTS_DIR));
            Files.write(Paths.get(RECEIPTS_DIR, "receipt_" + order.getOrderId() + ".txt"),
                    text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Could not save receipt: " + e.getMessage());
        }
    }

    private String basicReceipt(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("ABA-Mart Receipt\n");
        sb.append("Order ID: ").append(order.getOrderId()).append('\n');
        sb.append("Customer: ").append(order.getUsername()).append('\n');
        sb.append("Date: ").append(order.getDate()).append('\n');
        sb.append("Payment: ").append(order.getPaymentMode()).append("\n\n");
        for (CartItem item : order.getItems()) {
            sb.append(String.format("%-25s x%-3d %10.2f%n", item.getProduct().getName(),
                    item.getQuantity(), item.getSubtotal()));
        }
        sb.append(String.format("%nTotal: %.2f%n", order.getTotal()));
        return sb.toString();
    }

    // ---------- Helpers ----------

    /** Reads a CSV file, skipping the header and blank lines. Missing file gives an empty list. */
    private List<String[]> readRows(String file, int minColumns) {
        List<String[]> rows = new ArrayList<>();
        Path path = Paths.get(file);
        if (!Files.exists(path)) return rows;
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {          // line 0 is the header
                if (lines.get(i).trim().isEmpty()) continue;
                String[] fields = parseLine(lines.get(i));
                if (fields.length >= minColumns) rows.add(fields);
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file + ": " + e.getMessage());
        }
        return rows;
    }

    private void writeAll(String file, List<String> lines) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.write(Paths.get(file), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not write " + file + ": " + e.getMessage());
        }
    }

    /** Splits one CSV line, handling quoted fields that contain commas or quotes. */
    private static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQuotes = false;
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }

    /** Quotes a value if it contains a comma, quote, or newline. */
    private static String csv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
