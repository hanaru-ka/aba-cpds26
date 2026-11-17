import java.util.ArrayList;
import java.util.List;

/** A customer's past orders, kept in a stack (most recent first). */
public class OrderHistory {
    private final MyStack<Order> orders = new MyStack<>();

    public void addOrder(Order order) {
        orders.push(order);
    }

    /** Returns orders most recent first. */
    public List<Order> getAllOrders() {
        List<Order> result = new ArrayList<>();
        MyStack<Order> holding = new MyStack<>();

        while (!orders.isEmpty()) {
            Order order = orders.pop();
            result.add(order);
            holding.push(order);
        }
        while (!holding.isEmpty()) {
            orders.push(holding.pop());
        }
        return result;
    }

    public Order findOrder(String orderId) {
        for (Order order : getAllOrders()) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
}
