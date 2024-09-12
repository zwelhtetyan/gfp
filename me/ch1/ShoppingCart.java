package me.ch1;

import java.util.List;

class ShoppingCart {

  public int getDiscountPercentage(List<String> items) {
    if (items.contains("Book")) {
      return 5;
    } else {
      return 0;
    }
  }
}