case class Book(title: String, authors: List[String])
val books = List(
  Book("FP in Scala", List("Chiusano", "Bjarnason")),
  Book("The Hobbit", List("Tolkien"))
)

case class Movie(title: String)
def bookAdaptations(author: String): List[Movie] =
  if (author == "Tolkien")
    List(
      Movie("An Unexpected Journey"),
      Movie("The Desolation of Smaug")
    )
  else List.empty


// Our task is to return a feed of movie recommendations based on books
def recommendationFeed(books: List[Book]) = {
  books.flatMap(book => book.authors.flatMap(author => bookAdaptations(author)
  .map(movie => s"You may like ${movie.title}, " + s"because you liked $author's ${book.title}")
  ))
}

// for comprehension
for {
  book <- books
  author <- book.authors
  movie <- bookAdaptations(author)
} yield s"You may like ${movie.title}, " + s"because you liked $author's ${book.title}"



