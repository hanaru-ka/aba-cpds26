import java.util.ArrayList;
import java.util.List;

/** Team-written searching (no Arrays.binarySearch). */
public class Searcher {

    /** Binary search by name on a list sorted by name. Returns the index, or -1. */
    public static int binarySearchByName(List<Product> sortedList, String name) {
        int low = 0;
        int high = sortedList.size() - 1;

        name = name.toLowerCase();

        while (low <= high) {
            int mid = (low + high) / 2;
            String productName = sortedList.get(mid).getName().toLowerCase();

            if (productName.equals(name) || productName.startsWith(name)) {
                return mid;
            } else if (productName.compareTo(name) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    /** Linear search for partial name or category matches. */
    public static List<Product> linearSearchByKeyword(List<Product> list, String keyword) {
        List<Product> results = new ArrayList<>();
        keyword = keyword.toLowerCase();

        for (Product product : list) {
            String name = product.getName().toLowerCase();
            String category = product.getCategory().toLowerCase();

            if (name.contains(keyword) || category.contains(keyword)) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * Linear search for multi-word queries. The query is split into keywords, and a
     * product matches only if EVERY keyword appears in its ID, name, category, or
     * special offer (case-insensitive, partial words allowed).
     */
    public static List<Product> linearSearchByKeywords(List<Product> list, String query) {
        List<Product> results = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String[] keywords = query.toLowerCase().trim().split("\\s+");

        for (Product product : list) {
            String offer = product.getSpecialOffer();
            String searchable = (product.getProductId() + " "
                    + product.getName() + " "
                    + product.getCategory() + " "
                    + (offer == null ? "" : offer)).toLowerCase();

            boolean allFound = true;
            for (String keyword : keywords) {
                if (!searchable.contains(keyword)) {
                    allFound = false;
                    break;
                }
            }

            if (allFound) {
                results.add(product);
            }
        }

        return results;
    }
}
