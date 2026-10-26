/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: OrderLine represents one clothing item and the quantity of it
 *              on a customer order.
 */

public class OrderLine {
    private ClothingItem item;
    private int quantity;

    public OrderLine(ClothingItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
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

    public double getLineTotal() {
        return item.getFinalPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s x%d @ $%.2f = $%.2f",
                item.getName(), quantity, item.getFinalPrice(), getLineTotal());
    }
}
