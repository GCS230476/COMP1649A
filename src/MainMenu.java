import domain.*;
import algo.Sorting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MainMenu {
    private static final Scanner SC = new Scanner(System.in);

    // In-memory temporary storage
    private static final List<Book> catalog = new ArrayList<>();
    private static final List<Order> pendingOrders = new ArrayList<>();   // orders waiting to be processed
    private static final List<Order> processedOrders = new ArrayList<>(); // processed orders

    // Business layer
    private static final Inventory inventory = new Inventory();
    private static final OrderProcessor processor = new OrderProcessor(inventory);

    private static int nextOrderId = 100;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== BOOKSTORE MENU ===");
            System.out.println("1) Add Book to Catalog");
            System.out.println("2) View Catalog");
            System.out.println("3) Create Order (manual input, save pending)");
            System.out.println("4) View Pending Orders");
            System.out.println("5) Process All Pending Orders (choose sorting)");
            System.out.println("6) Search Processed Orders by ID");
            System.out.println("7) Statistics");
            System.out.println("8) Export Results (console + CSV)");
            System.out.println("9) Exit");
            int c = readInt("Choose: ", 1, 9);

            switch (c) {
                case 1 -> addBook();
                case 2 -> viewCatalog();
                case 3 -> createOrder();
                case 4 -> viewPending();
                case 5 -> processAllPending();
                case 6 -> searchProcessed();
                case 7 -> stats();
                case 8 -> exportData();
                case 9 -> { System.out.println("Goodbye!"); return; }
            }
        }
    }

    // ========== 1) Add book ==========
    private static void addBook() {
        System.out.println("\n-- Add Book --");
        String title = readNonEmpty("Title: ");
        String author = readNonEmpty("Author: ");
        String isbn;
        while (true) {
            isbn = readNonEmpty("ISBN: ").trim();
            if (findBookByIsbn(isbn) != null) {
                System.out.println("ISBN already exists in catalog. Try again!");
            } else break;
        }
        int qty = readInt("Initial stock (>=0): ", 0, Integer.MAX_VALUE);

        Book b = new Book(title, author, isbn);
        catalog.add(b);
        inventory.setStock(isbn, qty);
        System.out.println("Added: " + b + " | stock=" + inventory.getStock(isbn));
    }

    // ========== 2) View catalog ==========
    private static void viewCatalog() {
    System.out.println("\n-- Catalog --");
    if (catalog.isEmpty()) { System.out.println("(Empty)"); return; }

    // Example: sort by title using quick sort before printing
    List<Book> copy = new ArrayList<>(catalog);
    java.util.Comparator<Book> byTitle = java.util.Comparator.comparing(b -> b.title);
    // Use your Sorting util:
    algo.Sorting.quickSort(copy, byTitle);

    for (int i = 0; i < copy.size(); i++) {
        Book b = copy.get(i);
        System.out.printf("%d) %s by %s (ISBN %s) | stock=%d%n",
                i + 1, b.title, b.author, b.isbn, inventory.getStock(b.isbn));
    }
}
    // ========== 3) Create order (pending) ==========
    private static void createOrder() {
        if (catalog.isEmpty()) {
            System.out.println("No books in catalog. Please add books first (menu 1).");
            return;
        }
        System.out.println("\n-- Create Order --");
        String name = readNonEmpty("Customer name: ");
        String addr = readNonEmpty("Shipping address: ");
        Order o = new Order(nextOrderId++, name, addr);

        while (true) {
            String isbn = readString("Enter ISBN (Enter to finish): ").trim();
            if (isbn.isEmpty()) break;
            Book b = findBookByIsbn(isbn);
            if (b == null) {
                System.out.println("ISBN not found in catalog.");
                continue;
            }
            int qty = readInt("Quantity (>=1): ", 1, Integer.MAX_VALUE);
            o.items.add(new OrderItem(b, qty));
            System.out.println("Added: " + b.title + " x" + qty);
        }

        if (o.items.isEmpty()) {
            System.out.println("Empty order. Discarded.");
            nextOrderId--; // roll back ID if unused
            return;
        }
        pendingOrders.add(o);
        System.out.println("Saved pending order #" + o.orderId + ".");
    }

    // ========== 4) View pending ==========
    private static void viewPending() {
        System.out.println("\n-- Pending Orders --");
        if (pendingOrders.isEmpty()) { System.out.println("(None)"); return; }
        for (Order o : pendingOrders) {
            System.out.printf("Order#%d | %s | items=%d%n", o.orderId, o.customerName, o.items.size());
        }
    }

    // ========== 5) Process all pending ==========
    private static void processAllPending() {
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders to process.");
            return;
        }
        System.out.println("\n-- Process Orders --");
        String field = chooseField(); // title/author/isbn
        String algo  = chooseAlgo();  // insertion/selection/merge/quick

        // Submit all pending orders to the processor
        for (Order o : pendingOrders) {
            processor.submit(o);
        }
        pendingOrders.clear();

        // Process until queue is empty
        while (true) {
            Order out = processor.processNext(field, algo);
            if (out == null) break;
            processedOrders.add(out);
            System.out.println(renderOrder(out));
        }
        System.out.println(">> Finished processing all pending orders.");
    }

    // ========== 6) Search processed by ID ==========
    private static void searchProcessed() {
        if (processedOrders.isEmpty()) {
            System.out.println("No processed orders yet.");
            return;
        }
        int id = readInt("Enter Order ID: ", 0, Integer.MAX_VALUE);
        Order found = processor.findOrderBinary(id);
        System.out.println(found != null ? "Found: " + renderOrder(found) : "Not found.");
    }

    // ========== 7) Statistics ==========
    private static void stats() {
        System.out.println("\n-- Statistics --");
        int totalStock = catalog.stream().mapToInt(b -> inventory.getStock(b.isbn)).sum();
        long confirmed = processedOrders.stream().filter(o -> "CONFIRMED".equals(o.status)).count();
        long backorder = processedOrders.stream().filter(o -> "BACKORDER".equals(o.status)).count();

        System.out.println("Books in catalog: " + catalog.size());
        System.out.println("Total stock units: " + totalStock);
        System.out.println("Pending orders: " + pendingOrders.size());
        System.out.println("Processed orders: " + processedOrders.size());
        System.out.println("  - Confirmed: " + confirmed);
        System.out.println("  - Backorder: " + backorder);
    }

    // ========== 8) Export results ==========
    private static void exportData() {
        System.out.println("\n-- Export --");
        // Print to console
        if (processedOrders.isEmpty()) {
            System.out.println("(No processed orders to export)");
        } else {
            System.out.println(">> Processed Orders:");
            for (Order o : processedOrders) {
                System.out.println(renderOrder(o));
            }
        }

        // Write CSV (temporary file)
        String file = "orders_export.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("orderId,status,customer,items\n");
            for (Order o : processedOrders) {
                bw.write(o.orderId + "," + o.status + "," + escape(o.customerName) + "," + escape(itemsSummary(o)) + "\n");
            }
            System.out.println(">> Wrote file: " + file);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // ======= helpers =======
    private static Book findBookByIsbn(String isbn) {
        for (Book b : catalog) if (b.isbn.equals(isbn)) return b;
        return null;
    }

    private static String chooseField() {
        System.out.println("Choose item sort field:");
        System.out.println("1) title   2) author   3) isbn");
        int c = readInt("Choose: ", 1, 3);
        return (c == 2) ? "author" : (c == 3 ? "isbn" : "title");
    }

    private static String chooseAlgo() {
        System.out.println("Choose sorting algorithm:");
        System.out.println("1) insertion   2) selection   3) merge   4) quick");
        int c = readInt("Choose: ", 1, 4);
        return switch (c) {
            case 1 -> "insertion";
            case 2 -> "selection";
            case 3 -> "merge";
            default -> "quick";
        };
    }

    private static String renderOrder(Order o) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order#").append(o.orderId).append(" [").append(o.status).append("] -> ");
        for (int i = 0; i < o.items.size(); i++) {
            if (i > 0) sb.append(", ");
            OrderItem it = o.items.get(i);
            sb.append(it.book.title).append("(").append(it.qty).append(")");
        }
        return sb.toString();
    }

    private static String itemsSummary(Order o) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < o.items.size(); i++) {
            if (i > 0) sb.append("; ");
            OrderItem it = o.items.get(i);
            sb.append(it.book.title).append(" x").append(it.qty);
        }
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return SC.nextLine();
    }
    private static String readNonEmpty(String prompt) {
        while (true) {
            String s = readString(prompt).trim();
            if (!s.isEmpty()) return s;
            System.out.println("Must not be empty. Try again!");
        }
    }
    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.printf("Enter a number in range [%d..%d].%n", min, max);
                } else {
                    return v;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
}
