import java.util.ArrayList;
import java.util.List;

/** Team-written searching (no Arrays.binarySearch). */
public class Searcher {

    /** Binary search by name on a list sorted by name. Returns the index, or -1. */
    public static int binarySearchByName(List<Product> sortedList, String name) {
        // TODO: O(log n) search
        return -1;
    }

    /** Linear search for partial name or category matches. */
    public static List<Product> linearSearchByKeyword(List<Product> list, String keyword) {
        // TODO: O(n) scan
        return new ArrayList<>();
    }

    public static List<Product> filterByCategory(List<Product> list, String category) {
        // TODO: O(n) scan
        return new ArrayList<>();
    }
}
