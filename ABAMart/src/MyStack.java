/** Array-based stack (LIFO), written by the team. */
public class MyStack<T> {
    private Object[] data = new Object[10];
    private int size = 0;

    public void push(T item) {
        // TODO: grow the array when full, then add on top
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        // TODO: remove and return the top item
        return null;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        // TODO: return the top item without removing it
        return null;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
}
