import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

/** Sorted product list plus lookup maps, with search and sort methods. */
public class ProductCatalog {
    private final List<Product> products = new ArrayList<>();            // sorted by name
    private final Map<String, List<Product>> byCategory = new HashMap<>();
    private final Map<String, Product> byId = new HashMap<>();

    public ProductCatalog(List<Product> loaded) {
        for (Product product : loaded) {
            addProduct(product);
        }
    }

    /** Binary insertion keeps the list sorted by name. */
    public void addProduct(Product product) {
        if (product == null || byId.containsKey(product.getProductId())) {
            return;
        }

        Sorter.binaryInsert(products, product);

        byId.put(product.getProductId(), product);

        String category = product.getCategory().toLowerCase();

        if (!byCategory.containsKey(category)) {
            byCategory.put(category, new ArrayList<>());
        }

        byCategory.get(category).add(product);
    }

    public boolean removeProduct(String productId) {
        Product product = byId.remove(productId);

        if (product == null) {
            return false;
        }

        products.remove(product);

        String category = product.getCategory().toLowerCase();
        List<Product> categoryProducts = byCategory.get(category);

        if (categoryProducts != null) {
            categoryProducts.remove(product);

            if (categoryProducts.isEmpty()) {
                byCategory.remove(category);
            }
        }

        return true;
    }

    public Product getProduct(String productId) {
        return byId.get(productId);
    }

    public List<Product> getByCategory(String category) {
        List<Product> result = byCategory.get(category.toLowerCase());

        if (result == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(result);
    }

    /** Binary search on the sorted list (exact or prefix name). */
    public Product searchByName(String name) {
        int index = Searcher.binarySearchByName(products, name);

        if (index == -1) {
            return null;
        }

        return products.get(index);
    }

    /** Linear search for partial name or category matches. */
    public List<Product> searchByKeyword(String keyword) {
        return Searcher.linearSearchByKeyword(products, keyword);
    }

    /** Sort by "name", "price", or "stock" using merge sort. */
    public List<Product> sortBy(String field) {
        Comparator<Product> comparator;

        if (field.equalsIgnoreCase("name")) {
            comparator = Comparator.comparing(
                    p -> p.getName().toLowerCase()
            );
        } else if (field.equalsIgnoreCase("price")) {
            comparator = Comparator.comparingDouble(Product::getPrice);
        } else if (field.equalsIgnoreCase("stock")) {
            comparator = Comparator.comparingInt(Product::getStock);
        } else {
            return new ArrayList<>(products);
        }

        return Sorter.mergeSort(products, comparator);
    }

    public List<Product> getAllProducts() {
        return products;
    }
}
