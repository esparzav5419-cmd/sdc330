/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: Order demonstrates composition. An Order is composed of
 *              OrderLine objects that it creates and owns.
 */

import java.util.ArrayList;

public class Order {
    private int orderId;
    private String customerName;
    private String orderDate;
    private final ArrayList<OrderLine> lines;

    public Order(int orderId, String customerName, String orderDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.lines = new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public ArrayList<OrderLine> getLines() {
        return lines;
    }

    // Adds an existing OrderLine
    public void addLine(OrderLine line) {
        lines.add(line);
    }

    // Overloaded: builds the OrderLine from an item and quantity
    public void addLine(ClothingItem item, int quantity) {
        lines.add(new OrderLine(item, quantity));
    }

    public double getTotal() {
        double total = 0;
        for (OrderLine line : lines) {
            total += line.getLineTotal();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order #%d | Customer: %s | Date: %s",
                orderId, customerName, orderDate));
        for (OrderLine line : lines) {
            sb.append(System.lineSeparator()).append("  ").append(line);
        }
        sb.append(System.lineSeparator())
          .append(String.format("  Order Total: $%.2f", getTotal()));
        return sb.toString();
    }
}