
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Team-written sorting (no Collections.sort). */
public class Sorter {

    /** Stable merge sort, O(n log n). Returns a new sorted list. */
    public static List<Product> mergeSort(List<Product> list, Comparator<Product> cmp) {
        if (list.size() <= 1) {
            return new ArrayList<>(list);
        }

        int mid = list.size() / 2;

        List<Product> left = new ArrayList<>(list.subList(0, mid));
        List<Product> right = new ArrayList<>(list.subList(mid, list.size()));

        left = mergeSort(left, cmp);
        right = mergeSort(right, cmp);

        return merge(left, right, cmp);
    }

    private static List<Product> merge(List<Product> left,
                                       List<Product> right,
                                       Comparator<Product> cmp) {
        List<Product> result = new ArrayList<>();

        int i = 0;
        int j = 0;

        while (i < left.size() && j < right.size()) {
            if (cmp.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }

        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }

        return result;
    }

    /** Inserts into a list already sorted by name: binary search for the position. */
    public static void binaryInsert(List<Product> sortedList, Product product) {
        int low = 0;
        int high = sortedList.size();

        while (low < high) {
            int mid = (low + high) / 2;

            if (sortedList.get(mid).getName()
                    .compareToIgnoreCase(product.getName()) <= 0) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        sortedList.add(low, product);
    }
}
