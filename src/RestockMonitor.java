import java.util.ArrayList;
import java.util.List;

/** Min-heap of products by stock level; feeds restock alerts and low-stock labels. */
public class RestockMonitor {
    public static final int LOW_STOCK_THRESHOLD = 10; // fixed threshold, adjust as needed

    private final MinHeap<Product> heap =
            new MinHeap<>((a, b) -> Integer.compare(a.getStock(), b.getStock()));

    public RestockMonitor(List<Product> products) {
        rebuild(products);
    }

    public void rebuild(List<Product> products) {
        heap.clear();

        for (Product product : products) {
            heap.insert(product);
        }
    }

    /** Returns up to n products that most urgently need restocking. */
    public List<Product> getMostUrgent(int n) {
        List<Product> urgentProducts = new ArrayList<>();
        List<Product> temporary = new ArrayList<>();

        for (int i = 0; i < n && !heap.isEmpty(); i++) {
            Product product = heap.extractMin();

            urgentProducts.add(product);
            temporary.add(product);
        }

        for (Product product : temporary) {
            heap.insert(product);
        }

        return urgentProducts;
    }

    public boolean isLowStock(Product product) {
        return product != null &&
                product.isLowStock(LOW_STOCK_THRESHOLD);
    }
}
