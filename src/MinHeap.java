import java.util.Comparator;

/** Array-based min-heap, written by the team. */
public class MinHeap<T> {
    private Object[] heap = new Object[16];
    private int size = 0;
    private final Comparator<T> comparator;

    public MinHeap(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void insert(T item) {
        // TODO: add at the end, then siftUp
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        // TODO: remove root, move last to root, siftDown
        return null;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        // TODO: return the root without removing it
        return null;
    }

    private void siftUp(int index) { /* TODO */ }
    private void siftDown(int index) { /* TODO */ }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
}
