import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Sorted product list plus lookup maps, with search and sort methods. */
public class ProductCatalog {
    private final List<Product> products = new ArrayList<>();            // sorted by name
    private final Map<String, List<Product>> byCategory = new HashMap<>();
    private final Map<String, Product> byId = new HashMap<>();

    public ProductCatalog(List<Product> loaded) {
        // TODO: add each product using addProduct so the list stays sorted
    }

    /** Binary insertion keeps the list sorted by name. */
    public void addProduct(Product product) {
        // TODO: Sorter.binaryInsert, update byCategory and byId
    }

    public boolean removeProduct(String productId) {
        // TODO: remove from list and both maps
        return false;
    }

    public Product getProduct(String productId) {
        // TODO: byId lookup
        return null;
    }

    public List<Product> getByCategory(String category) {
        // TODO: byCategory lookup
        return new ArrayList<>();
    }

    /** Binary search on the sorted list (exact or prefix name). */
    public Product searchByName(String name) {
        // TODO: Searcher.binarySearchByName
        return null;
    }

    /** Linear search for partial name or category matches. */
    public List<Product> searchByKeyword(String keyword) {
        // TODO: Searcher.linearSearchByKeyword
        return new ArrayList<>();
    }

    /** Sort by "name", "price", or "stock" using merge sort. */
    public List<Product> sortBy(String field) {
        // TODO: Sorter.mergeSort with the matching comparator
        return new ArrayList<>();
    }

    public List<Product> getAllProducts() { return products; }
}
