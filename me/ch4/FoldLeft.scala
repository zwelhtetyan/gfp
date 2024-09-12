// Return a sum of all integers in the given list.
// input: List(5, 1, 2, 4, 100) output: 112

// Return the total length of all the words in the given list.
// input: List("scala", "rust", "ada") output: 12

// Return the number of the letter 's' found in all the words in the given list.
// input: List("scala", "haskell", "rust", "ada") output: 3

// Return the maximum of all integers in the given list.
// input: List(5, 1, 2, 4, 15) output: 15

def sum(a: Int, b: Int): Int = a + b
def sumAll(list: List[Int]): Int = list.foldLeft(0)(sum)

def len(w: String): Int = w.length()
def wordLength(list: List[String]): Int = list.foldLeft(0)((total, curr) => total + len(curr))

def numOfS(w: String): Int = w.length() - w.replaceAll("s", "").length()
def sumS(list: List[String]): Int = list.foldLeft(0)((t, c) => t + numOfS(c))

def max(a: Int, b: Int): Int = if (a > b) a else b
def findMax(list: List[Int]): Int = list.foldLeft(Int.MinValue)(max)