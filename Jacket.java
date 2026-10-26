/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: Jacket extends ClothingItem and adds whether it is waterproof
 *              and the season it is made for. Jackets have a 20% discount.
 */

public class Jacket extends ClothingItem {
    private boolean waterproof;
    private String season;

    public Jacket(int itemId, String name, String size, String color,
            double basePrice, int quantityInStock, boolean waterproof,
            String season) {
        super(itemId, name, size, color, basePrice, quantityInStock);
        this.waterproof = waterproof;
        this.season = season;
    }

    public boolean getWaterproof() {
        return waterproof;
    }

    public void setWaterproof(boolean waterproof) {
        this.waterproof = waterproof;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    @Override
    public double getDiscountRate() {
        return 0.20;
    }

    @Override
    public String getCategory() {
        return "Jacket";
    }

    @Override
    public String toString() {
        return super.toString() + " | Waterproof: " + (waterproof ? "Yes" : "No")
                + " | Season: " + season;
    }
}
