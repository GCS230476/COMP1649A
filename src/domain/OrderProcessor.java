package domain;

import adt.QueueViaTwoStacks;
import adt.Stack;
import algo.Sorting;
import algo.Searching;
import java.util.*;

public class OrderProcessor {
    private final QueueViaTwoStacks<Order> queue = new QueueViaTwoStacks<>();
    private final Stack<Order> history = new Stack<>();
    private final Map<Integer, Order> index = new HashMap<>();
    private final Inventory inventory;

    public OrderProcessor(Inventory inventory) { this.inventory = inventory; }

    public void submit(Order order) {
        queue.enqueue(order);
        index.put(order.orderId, order);
    }

    public Order processNext(String sortField, String algoName) {
        if (queue.isEmpty()) return null;
        Order o = queue.dequeue();

        boolean allAvailable = true;
        for (OrderItem it : o.items) {
            if (!inventory.isAvailable(it)) { allAvailable = false; break; }
        }

        if (allAvailable) {
            for (OrderItem it : o.items) inventory.reserve(it);
            Comparator<OrderItem> cmp = comparatorBy(sortField);
            switch (algoName.toLowerCase()) {
                case "insertion": Sorting.insertionSort(o.items, cmp); break;
                case "selection": Sorting.selectionSort(o.items, cmp); break;
                case "merge": {
                    List<OrderItem> sorted = Sorting.mergeSort(o.items, cmp);
                    o.items.clear(); o.items.addAll(sorted); break;
                }
                default: Sorting.quickSort(o.items, cmp);
            }
            o.status = "CONFIRMED";
        } else { o.status = "BACKORDER"; }

        history.push(o);
        return o;
    }

    private Comparator<OrderItem> comparatorBy(String field) {
        switch (field.toLowerCase()) {
            case "author": return Comparator.comparing((OrderItem it) -> it.book.author);
            case "isbn":   return Comparator.comparing((OrderItem it) -> it.book.isbn);
            default:       return Comparator.comparing((OrderItem it) -> it.book.title);
        }
    }

    public Order findOrderLinear(int id) {
        List<Order> orders = new ArrayList<>(index.values());
        orders.sort(Comparator.comparingInt(o -> o.orderId));
        int i = Searching.linearSearch(orders, new Order(id,"",""), Comparator.comparingInt(o -> o.orderId));
        return (i >= 0) ? orders.get(i) : null;
    }

    public Order findOrderBinary(int id) {
        List<Order> orders = new ArrayList<>(index.values());
        orders.sort(Comparator.comparingInt(o -> o.orderId));
        int i = Searching.binarySearch(orders, new Order(id,"",""), Comparator.comparingInt(o -> o.orderId));
        return (i >= 0) ? orders.get(i) : null;
    }
}
