import java.util.Comparator;
import java.util.List;

/** Team-written sorting (no Collections.sort). */
public class Sorter {

    /** Stable merge sort, O(n log n). Returns a new sorted list. */
    public static List<Product> mergeSort(List<Product> list, Comparator<Product> cmp) {
        // TODO: split, sort halves, merge
        return list;
    }

    /** Inserts into a list already sorted by name: binary search for the position. */
    public static void binaryInsert(List<Product> sortedList, Product product) {
        // TODO: find index with binary search, then add at that index
    }
}
