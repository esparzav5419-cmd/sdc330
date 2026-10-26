/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: ClothingItem is the abstract super class for all clothing
 *              sold by Esparza LLC. It holds the common properties and
 *              implements the Discountable interface.
 */

public abstract class ClothingItem implements Discountable {
    private int itemId;
    private String name;
    private String size;
    private String color;
    private double basePrice;
    private int quantityInStock;

    public ClothingItem(int itemId, String name, String size, String color,
            double basePrice, int quantityInStock) {
        this.itemId = itemId;
        this.name = name;
        this.size = size;
        this.color = color;
        this.basePrice = basePrice;
        this.quantityInStock = quantityInStock;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    // Each subclass supplies its own discount rate
    @Override
    public abstract double getDiscountRate();

    // Each subclass names its own category
    public abstract String getCategory();

    @Override
    public double getFinalPrice() {
        return basePrice * (1 - getDiscountRate());
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s (%s) | Size: %s | Color: %s | Price: $%.2f"
                + " | Final Price: $%.2f | In Stock: %d",
                itemId, name, getCategory(), size, color, basePrice,
                getFinalPrice(), quantityInStock);
    }
}