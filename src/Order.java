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
        StringBuilder sb = new StringBuilder();
        sb.append("ABA-Mart Receipt\n");
        sb.append("Order ID: ").append(orderId).append('\n');
        sb.append("Customer: ").append(username).append('\n');
        sb.append("Date: ").append(date).append('\n');
        sb.append("Payment: ").append(paymentMode).append("\n\n");
        for (CartItem item : items) {
            sb.append(String.format("%-25s x%-3d %10.2f%n", item.getProduct().getName(),
                    item.getQuantity(), item.getSubtotal()));
        }
        sb.append(String.format("%nTotal: %.2f%n", total));
        return sb.toString();
    }

    public String getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public String getDate() { return date; }
    public String getPaymentMode() { return paymentMode; }
    public List<CartItem> getItems() { return items; }
    public double getTotal() { return total; }
}
