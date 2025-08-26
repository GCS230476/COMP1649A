package adt;

public class QueueViaTwoStacks<T> {
    private final Stack<T> in = new Stack<>();
    private final Stack<T> out = new Stack<>();

    private void shift() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) out.push(in.pop());
        }
    }

    public void enqueue(T item) { in.push(item); }

    public T dequeue() {
        shift();
        if (out.isEmpty()) throw new IllegalStateException("dequeue from empty queue");
        return out.pop();
    }

    public T peek() {
        shift();
        if (out.isEmpty()) throw new IllegalStateException("peek from empty queue");
        return out.peek();
    }

    public boolean isEmpty() { return in.isEmpty() && out.isEmpty(); }
    public int size() { return in.size() + out.size(); }

    @Override public String toString() { return "Queue(in=" + in + ", out=" + out + ")"; }
}
