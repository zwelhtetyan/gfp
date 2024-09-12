package me;

import java.util.List;

/**
 * ShoppingCart
 */
public class ShoppingCart {
  public int getDiscountPercentage(List<String> items) {
    if (items.contains("Book")) {
      return 5;
    } else {
      return 0;
    }
  }
}
