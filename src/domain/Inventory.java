package domain;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<String, Integer> stock = new HashMap<>(); // isbn -> qty

    public void setStock(String isbn, int qty) { stock.put(isbn, qty); }

    public int getStock(String isbn) {               // <-- thêm hàm này để đọc tồn kho
        return stock.getOrDefault(isbn, 0);
    }

    public boolean isAvailable(OrderItem item) {
        return stock.getOrDefault(item.book.isbn, 0) >= item.qty;
    }

    public boolean reserve(OrderItem item) {
        if (isAvailable(item)) {
            stock.put(item.book.isbn, stock.get(item.book.isbn) - item.qty);
            return true;
        }
        return false;
    }
}
