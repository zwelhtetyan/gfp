import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StreamExample {
  public static void main(String[] args) {
    Stream<Integer> numbers = Stream.of(1, 2, 3);
    List<Integer> result = oddNumbers(numbers).collect(Collectors.toList());
    // System.out.println(result);

    Stream<Integer> infiniteNumbers = Stream.iterate(0, i -> i + 1);
    Stream<Integer> infiniteOddNumbers = oddNumbers(infiniteNumbers).limit(3);
    List<Integer> infiniteOddNumbersList = infiniteOddNumbers.collect(Collectors.toList());

    System.out.println(infiniteOddNumbersList);

  }

  static Stream<Integer> oddNumbers(Stream<Integer> numbers) {
    return numbers.filter(n -> n % 2 != 0);
  }
}
