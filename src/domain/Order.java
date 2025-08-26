package domain;

import java.util.ArrayList;
import java.util.List;

public class Order {
    public final int orderId;
    public final String customerName;
    public final String shippingAddress;
    public final List<OrderItem> items = new ArrayList<>();
    public String status = "RECEIVED";

    public Order(int orderId, String customerName, String shippingAddress) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
    }

    @Override public String toString() { return "Order#" + orderId + " [" + status + "]"; }
}
