import java.util.List;

/** A completed purchase. */
public class Order {
    private final String orderId;
    private final String username;
    private final String date;
    private final String paymentMode; // CARD, EWALLET, CASH
    private final List<CartItem> items;
    private final double total;

    public Order(String orderId, String username, String date,
                 String paymentMode, List<CartItem> items, double total) {
        this.orderId = orderId;
        this.username = username;
        this.date = date;
        this.paymentMode = paymentMode;
        this.items = items;
        this.total = total;
    }

    public String generateReceipt() {
        // TODO: build the receipt text
        return "";
    }

    public String getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public String getDate() { return date; }
    public String getPaymentMode() { return paymentMode; }
    public List<CartItem> getItems() { return items; }
    public double getTotal() { return total; }
}
