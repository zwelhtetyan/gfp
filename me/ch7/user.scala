object user {
  case class User(name: String, city: Option[String], favoriteArtists: List[String])

  val users = List(
    User("Alice", Some("Melbourne"), List("Bee Gees")),
    User("Bob", Some("Lagos"), List("Bee Gees")),
    User("Eve", Some("Tokyo"), List.empty),
    User("Mallory", None, List("Metallica", "Bee Gees")),
    User("Trent", Some("Buenos Aires"), List("Led Zeppelin"))
  )

  // f1: users that haven’t specified their city or live in Melbourne
  def f1() = users.filter(_.city.forall(_  == "Melbourne"))

  // f2: users that live in Lagos
  def f2() = users.filter(_.city.contains("Lagos"))

  // f3: users that like Bee Gees
  def f3() = users.filter(_.favoriteArtists.contains("Bee Gees"))

  // f4: users that live in cities that start with the letter T
  def f4() = users.filter(_.city.exists(_.startsWith("T")))

  // f5: users that only like artists that have a name longer than 
  // eight characters (or no favorite artists at all)
  def f5() = users.filter(_.favoriteArtists.forall(_.replaceAll(" ", "").length > 8))

  // f6: users that like some artists whose names start with an M
  def f6() = users.filter(_.favoriteArtists.exists(_.startsWith("M")))
}