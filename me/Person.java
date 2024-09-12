package me;

import java.util.ArrayList;
import java.util.List;

public class Person implements PersonInterface {
  List<String> persons = new ArrayList<>();

  @Override
  public void add(String name) {
    persons.add(name);
  }

  @Override
  public void remove(String name) {
    persons.remove(name);
  }

  @Override
  public List<String> getPersons() {
    return new ArrayList<>(persons);
  }
}
