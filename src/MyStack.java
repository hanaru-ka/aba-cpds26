import java.util.Arrays;


/** Array-based stack (LIFO), written by the team. */
public class MyStack<T> {
    private Object[] data = new Object[10];
    private int size = 0;

    public void push(T item) {
        // TODO: grow the array when full, then add on top

        if (size == data.length) {
            data = Arrays.copyOf(data, size * 2);
        }
        data[size] = item;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        // TODO: remove and return the top item

        if (isEmpty()) {
            return null;
        }
        size--;
        T item = (T) data[size];
        data[size] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        // TODO: return the top item without removing it

        if (isEmpty()) {
            return null;
        }
        return (T) data[size - 1];
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
}
