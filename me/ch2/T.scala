object Tip {
  def getTipPercentage(names: List[String]): Int = { 
    if (names.size > 5) {
      20
    } else if (names.size > 0) {
      10
    } else {
      0
    }
  }
}

val names = List.empty
Tip.getTipPercentage(names)

val small = List("Alice", "Bob", "Charlie")
Tip.getTipPercentage(small)

val larger = List("Alice", "Bob", "Charlie", "Daniel", "Emily", "Frank");
Tip.getTipPercentage(larger)


def getDiscountPercentage(items: List[String]): Int = {
  if (items.contains("Book")) 5
  else 0
}