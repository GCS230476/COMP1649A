package domain;

public class OrderItem {
    public final Book book;
    public final int qty;

    public OrderItem(Book book, int qty) {
        this.book = book;
        this.qty = qty;
    }

    @Override public String toString() { return book.title + "(" + qty + ")"; }
}
