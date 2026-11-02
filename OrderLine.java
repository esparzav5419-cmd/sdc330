/*
 * Name: valentin esparza
 * Date: 11/01/2026
 * Assignment: SDC330 Course Project - Database Implementation
 * Description: OrderLine represents one clothing item and the quantity of it
 *              on a customer order. It remembers the unit price at the time
 *              of the order so later price changes do not alter old orders.
 */

public class OrderLine {
    private ClothingItem item;
    private int quantity;
    private double unitPrice;

    // New order line: uses the item's current final price
    public OrderLine(ClothingItem item, int quantity) {
        this(item, quantity, item.getFinalPrice());
    }

    // Order line loaded from the database with its saved price
    public OrderLine(ClothingItem item, int quantity, double unitPrice) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public ClothingItem getItem() {
        return item;
    }

    public void setItem(ClothingItem item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getLineTotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s x%d @ $%.2f = $%.2f",
                item.getName(), quantity, unitPrice, getLineTotal());
    }
}
