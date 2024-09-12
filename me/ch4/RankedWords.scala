def score(word: String) = word.replaceAll("a", "").length()
def bonus(word: String) = if (word.contains("c")) 5 else 0
def penalty(word: String) = if (word.contains("s")) 7 else 0

def rankedWords(wordScore: (String) => Int, word: List[String]): List[String] = {
  word.sortBy(wordScore).reverse
}

def rankedWordsAndReturnScores(wordScore: (String) => Int, word: List[String]): List[Int] = {
  word.map(wordScore)
}

def filterFn(w: String): Boolean = w.length() > 0
def highScoringWords(score: (String) => Int, filterFn: (String) => Boolean, word: List[String]) = {
  word.sortBy(score).reverse.filter(filterFn)
}

def closerLike(score: (String) => Int, word: List[String]) = {
  (higherThan: Int) => word.filter(n => score(n) > higherThan)
}

val wordScoreHigherThan: (Int) => List[String] = closerLike()


def highScoringWords(scoreFn: String => Int): Int => List[String] => List[String] = {
  (greaterThan) => (word) => word.filter(w => scoreFn(w) > greaterThan)
}

// built in support for currying type parameter
def highScoringWords(wordScore: String => Int)(higherThan: Int)(words: List[String]): List[String] = {
  words.filter(word => wordScore(word) > higherThan)
}

def cumulativeScore(wordScore: String => Int, word: List[String]) = {
 word.foldLeft(0)((total, word) => total + wordScore(word)) 
}