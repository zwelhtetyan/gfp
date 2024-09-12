package me.ch4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.naming.InitialContext;

public class RankedWords {

  public static void main(String[] args) {
    List<String> words = Arrays.asList("ada", "haskell", "scala", "java", "rust");

    Function<String, Integer> wordScoreFunc = (w) -> score(w);
    Function<String, Integer> wordScoreWithBonusFunc = (w) -> scoreWithBonus(w);

    System.out.println(rankedWords(wordScoreWithBonusFunc, words));
  }

  static int score(String word) {
    return word.replaceAll("a", "").length();
  }

  static int scoreWithBonus(String word) {
    int base = score(word);
    if (word.contains("c"))
      return base + 5;
    else
      return base;
  }

  static List<String> rankedWords(Function<String, Integer> scoreFn, List<String> words) {
    Comparator<String> cmp = (w1, w2) -> Integer.compare(scoreFn.apply(w2), scoreFn.apply(w1));

    return words.stream()
        .sorted(cmp)
        .collect(Collectors.toList());
  }

  static List<String> highScoringWords(Function<String, Integer> fn, List<String> words) {
    List<String> r = new ArrayList<>();

    for (String word : words) {
      if (fn.apply(word) > 1) {
        r.add(word);
      }
    }

    return r;
  }

}
