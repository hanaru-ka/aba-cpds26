import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Shopping cart (productId -> CartItem), keeps insertion order. */
public class Cart {
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public void addItem(Product product, int quantity) {
        // TODO: add or increase quantity; check stock
    }

    public boolean updateQuantity(String productId, int quantity) {
        // TODO: edit quantity
        return false;
    }

    public boolean removeItem(String productId) {
        // TODO: remove item
        return false;
    }

    public double getTotal() {
        // TODO: sum of subtotals
        return 0;
    }

    public void clear() { items.clear(); }
    public boolean isEmpty() { return items.isEmpty(); }
    public Collection<CartItem> getItems() { return items.values(); }
}
