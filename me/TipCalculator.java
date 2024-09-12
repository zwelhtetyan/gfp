package me;

import java.util.List;

public class TipCalculator {

  public int getTipPercentage(List<String> names) {

    if (names.size() > 5) {
      return 20;
    } else if (names.size() > 0) {
      return 10;
    } else {
      return 0;
    }
  }
}