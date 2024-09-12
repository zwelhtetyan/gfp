// def highestList(numbers: List[Int]): (n: Int) => List[Int] = {
//   (greaterThan: Int) => numbers.filter(n => n > greaterThan)
// }

// val listGreaterThan = highestList(List(5, 1, 2, 4, 0))

// def createDivisibleList(numbers: List[Int]): (n: Int) => List[Int] = {
//   (v: Int) => numbers.filter(n => n % v == 0)
// }

// val listDivisibleBy = createDivisibleList(List(5, 1, 2, 4, 15))

def numberOfS(w: String): Int = w.length() - w.replaceAll("s", "").length()
def containsS(n: Int): String => Boolean = w => numberOfS(w) > n

List("rust", "ada").filter(containsS(2))