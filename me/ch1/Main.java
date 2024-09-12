package me.ch1;

import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    List<String> items = new ArrayList<>();
    items.add("Book");

    ShoppingCart cart = new ShoppingCart();
    System.out.println(cart.getDiscountPercentage(items));

  }
}
