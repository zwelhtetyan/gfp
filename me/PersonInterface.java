package me;

import java.util.List;

public interface PersonInterface {

  void add(String name);

  void remove(String name);

  List<String> getPersons();
}