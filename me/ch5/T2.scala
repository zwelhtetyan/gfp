object T2 {
    // Example
  case class Event(name: String, start: Int, end: Int)

  // name should be a nonempty String.
  // end year should be a reasonable number—say, less than 3,000.
  // start should be less than or equal to end.

  def validateName(str: String): Option[String] = {
    if (str.size > 0) Some(str) else None
  }
  def validateEndYear(year: Int): Option[Int] = {
    if (year < 3000) Some(year) else None
  }
  def validateStartYear(start: Int, end: Int): Option[Int] = {
    if (start <= end ) Some(start) else None
  }
  def validateLength(start: Int, end: Int, length: Int): Option[Int] = {
    if (start < end && end - start >= length) Some(end - start)
    else None
  }

  def parse(name: String, start: Int, end: Int): Option[Event] = {
    for {
      validName <- validateName(name)
      validEnd <- validateEndYear(end)
      validStart <- validateStartYear(start, end)
    } yield Event(validName, validStart, validEnd)
  }

   def parseLongEvent(name: String, start: Int, end: Int, length: Int): Option[Event] = {
    for {
      validName <- validateName(name)
      validEnd <- validateEndYear(end)
      validStart <- validateStartYear(start, end)
      _ <- validateLength(validStart, validEnd, length)
    } yield Event(validName, validStart, validEnd)
  }
}
