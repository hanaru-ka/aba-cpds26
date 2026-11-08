import java.util.ArrayList;
import java.util.List;

/** A customer's past orders, kept in a stack (most recent first). */
public class OrderHistory {
    private final MyStack<Order> orders = new MyStack<>();

    public void addOrder(Order order) {
        // TODO: orders.push(order)
    }

    /** Returns orders most recent first. */
    public List<Order> getAllOrders() {
        // TODO: read the stack from top to bottom
        return new ArrayList<>();
    }

    public Order findOrder(String orderId) {
        // TODO: linear search by order ID
        return null;
    }
}
