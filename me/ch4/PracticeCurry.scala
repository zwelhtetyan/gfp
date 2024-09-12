def greaterThan(n: Int): List[Int] => List[Int] = (list) => list.filter(num => num > n)


// Separate function
def largerThan(number: Int): Int => Boolean = (v) => v > number

def divisibleBy(value: Int): (num: Int) => Boolean = num => num % value == 0

def shorterThan(n: Int): String => Boolean = (value) => value.length() < 4

def numOfS(w: String) = w.length() - w.replaceAll("s", "").length()
def containsS(n: Int)(word: String): Boolean = numOfS(word) > n