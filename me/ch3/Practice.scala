def firstTwo(list: List[String]): List[String] = {
  list.slice(0, 2)
}

def lastTwo(list: List[String]): List[String] = {
  list.slice(list.size - 2, list.size)
}

def movedFirstTwoToTheEnd(list: List[String]): List[String] = {
  val firstTwo = list.slice(0, 2)
  val rest = list.slice(2, list.size)
  rest.appendedAll(firstTwo)
}

def insertedBeforeLast(list: List[String], newStr: String): List[String] = {
  list.slice(0, list.size - 1).appended(newStr).appended(list(list.size - 1))
}
