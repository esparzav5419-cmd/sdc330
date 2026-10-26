/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: Shirt extends ClothingItem and adds the sleeve style.
 *              Shirts have a 5% discount.
 */

public class Shirt extends ClothingItem {
    private String sleeveStyle;

    public Shirt(int itemId, String name, String size, String color,
            double basePrice, int quantityInStock, String sleeveStyle) {
        super(itemId, name, size, color, basePrice, quantityInStock);
        this.sleeveStyle = sleeveStyle;
    }

    public String getSleeveStyle() {
        return sleeveStyle;
    }

    public void setSleeveStyle(String sleeveStyle) {
        this.sleeveStyle = sleeveStyle;
    }

    @Override
    public double getDiscountRate() {
        return 0.05;
    }

    @Override
    public String getCategory() {
        return "Shirt";
    }

    @Override
    public String toString() {
        return super.toString() + " | Sleeves: " + sleeveStyle;
    }
}