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
        if (size == heap.length) {
            Object[] newHeap = new Object[heap.length * 2];

            for (int i = 0; i < heap.length; i++) {
                newHeap[i] = heap[i];
            }

            heap = newHeap;
        }

        heap[size] = item;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (isEmpty()) {
            return null;
        }

        T min = (T) heap[0];

        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            siftDown(0);
        }

        return min;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            return null;
        }

        return (T) heap[0];
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;

            T childValue = (T) heap[index];
            T parentValue = (T) heap[parent];

            if (comparator.compare(childValue, parentValue) >= 0) {
                break;
            }

            Object temp = heap[index];
            heap[index] = heap[parent];
            heap[parent] = temp;

            index = parent;
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        while (true) {
            int left = (index * 2) + 1;
            int right = (index * 2) + 2;
            int smallest = index;

            if (left < size &&
                    comparator.compare((T) heap[left], (T) heap[smallest]) < 0) {
                smallest = left;
            }

            if (right < size &&
                    comparator.compare((T) heap[right], (T) heap[smallest]) < 0) {
                smallest = right;
            }

            if (smallest == index) {
                break;
            }

            Object temp = heap[index];
            heap[index] = heap[smallest];
            heap[smallest] = temp;

            index = smallest;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }

        size = 0;
    }

    public int size() {
        return size;
    }
}
