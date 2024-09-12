package me;

public class Main {
  public static void main(String[] args) {
    Person person = new Person();

    person.add("Zwel");
    person.add("Book");

    ShoppingCart cart = new ShoppingCart();

    int result = cart.getDiscountPercentage(person.getPersons());

    System.out.println(result);

  }
}
