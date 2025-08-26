package domain;

public class Book {
    public final String title;
    public final String author;
    public final String isbn;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    @Override public String toString() { return title + " by " + author + " (" + isbn + ")"; }
}
