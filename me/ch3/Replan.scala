def replan(plan: List[String], newCity: String, beforeCity: String): List[String] = {
  val beforeCityIdx = plan.indexOf(beforeCity)
  val beforeCities = plan.slice(0, beforeCityIdx)
  val afterCities = plan.slice(beforeCityIdx, plan.size)

  if (newCity.length() <= 0) return plan

  beforeCities.appended(newCity).appendAll(afterCities)
}


// "Alonzo Church" => "A. Church"
// def abbreviate(str: String): String = {
//   if (str.length() <= 0) throw new Error("Invalid string")

//   val idx = str.indexOf(" ")
//   if (idx == -1 ) return str
//   else if (idx >= str.length() - 1) return str.substring(0, str.length() - 1)

//   val firstLetter = str.substring(0, 1)
//   val lastName = str.substring(idx + 1)

//   s"$firstLetter. $lastName"
// }
