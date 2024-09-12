package me.ch4;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class PracticeRankedWords {
  public static void main(String[] args) {
    List<String> words = Arrays.asList("ada", "haskell", "scala", "java", "rust");

    System.out.println(rankedWords((w) -> score(w), words));
    System.out.println(rankedWords((w) -> score(w) + bonus(w), words));
    System.out.println(rankedWords((w) -> score(w) + bonus(w) - penalty(w), words));
  }

  static int score(String word) {
    return word.replaceAll("a", "").length();
  }

  static int bonus(String word) {
    return word.contains("c") ? 5 : 0;
  }

  static int penalty(String word) {
    return word.contains("s") ? 7 : 0;
  }

  static List<String> rankedWords(Function<String, Integer> scoreFn, List<String> word) {

    Comparator<String> compare = (w1, w2) -> Integer.compare(scoreFn.apply(w2), scoreFn.apply(w1));

    return word.stream().sorted(compare).collect(Collectors.toList());
  }
}
