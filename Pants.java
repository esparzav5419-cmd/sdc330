/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: Pants extends ClothingItem and adds the inseam length.
 *              Pants have a 10% discount.
 */

public class Pants extends ClothingItem {
    private int inseam;

    public Pants(int itemId, String name, String size, String color,
            double basePrice, int quantityInStock, int inseam) {
        super(itemId, name, size, color, basePrice, quantityInStock);
        this.inseam = inseam;
    }

    public int getInseam() {
        return inseam;
    }

    public void setInseam(int inseam) {
        this.inseam = inseam;
    }

    @Override
    public double getDiscountRate() {
        return 0.10;
    }

    @Override
    public String getCategory() {
        return "Pants";
    }

    @Override
    public String toString() {
        return super.toString() + " | Inseam: " + inseam + " in";
    }
}
