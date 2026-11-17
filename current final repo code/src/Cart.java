import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Shopping cart (productId -> CartItem), keeps insertion order. */
public class Cart {
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public void addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) return;
        CartItem existing = items.get(product.getProductId());
        if (existing == null) {
            items.put(product.getProductId(), new CartItem(product, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
    }

    public boolean updateQuantity(String productId, int quantity) {
        CartItem item = items.get(productId);
        if (item == null || quantity <= 0) return false;
        item.setQuantity(quantity);
        return true;
    }

    public boolean removeItem(String productId) {
        return items.remove(productId) != null;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clear() { items.clear(); }
    public boolean isEmpty() { return items.isEmpty(); }
    public Collection<CartItem> getItems() { return items.values(); }
}
