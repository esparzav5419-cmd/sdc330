/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: Discountable is an interface that defines what it means for a
 *              product to be discountable. Each implementing class decides
 *              how its discount is calculated.
 */

public interface Discountable {
    double getDiscountRate();

    double getFinalPrice();
}
