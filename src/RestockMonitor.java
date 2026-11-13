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
        // TODO: insert products into the heap
    }

    /** Returns up to n products that most urgently need restocking. */
    public List<Product> getMostUrgent(int n) {
        // TODO: extract n lowest-stock products, then reinsert them
        return new ArrayList<>();
    }

    public boolean isLowStock(Product product) {
        // TODO: use LOW_STOCK_THRESHOLD
        return false;
    }
}
