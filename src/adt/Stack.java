package adt;

import java.util.ArrayList;
import java.util.List;

public class Stack<T> {
    private final List<T> data = new ArrayList<>();

    public void push(T item) { data.add(item); }

    public T pop() {
        if (data.isEmpty()) throw new IllegalStateException("pop from empty stack");
        return data.remove(data.size() - 1);
    }

    public T peek() {
        if (data.isEmpty()) throw new IllegalStateException("peek from empty stack");
        return data.get(data.size() - 1);
    }

    public boolean isEmpty() { return data.isEmpty(); }
    public int size() { return data.size(); }

    @Override public String toString() { return "Stack" + data.toString(); }
}
