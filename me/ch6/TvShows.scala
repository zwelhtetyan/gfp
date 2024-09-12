case class TvShow(name: String, start: Int, end: Int)

// List("Breaking Bad (2008-2013)", "The Wire (2002-2008)", "Mad Men (2007-2015)")

def sortShows(shows: List[TvShow]): List[TvShow] = {
    shows.sortBy(show => show.end - show.start).reverse
}

def extractName(rawShow: String): Either[String, String] = {
    val bracketOpen = rawShow.indexOf('(')

    if (bracketOpen > 0) {
        Right(rawShow.substring(0, bracketOpen).trim)
    } else {
        Left(s"Can't extract name from $rawShow")
    }
}

def yearEither(str: String): Either[String, Int] = str.toIntOption.toRight(s"Can't parse $str")

def extractStartYear(rawShow: String): Either[String, Int] = {
    val bracketOpen = rawShow.indexOf('(')
    val dash = rawShow.indexOf('-')

    for {
        yearStr <- if (bracketOpen != -1 && dash > bracketOpen + 1) Right(rawShow.substring(bracketOpen + 1, dash)) 
                    else Left(s"Can't extract start year from $rawShow") 
        year <- yearEither(yearStr)
    } yield year
}

def extractEndYear(rawShow: String): Either[String, Int] = {
    val bracketClose = rawShow.indexOf(')')
    val dash = rawShow.indexOf('-')

    for {
        yearStrOpt <- if (dash != -1 && bracketClose > dash + 1) Right(rawShow.substring(dash + 1, bracketClose))
                        else Left(s"Can't extract end year from $rawShow")
        year <- yearEither(yearStrOpt)
    } yield year
}

def extractSingleYear(rawShow: String): Either[String, Int] = {
    val bracketOpen = rawShow.indexOf('(')
    val bracketClose = rawShow.indexOf(')')
    val dash = rawShow.indexOf('-')

    for {
        yearStrOpt <- if (dash == -1 && bracketOpen != -1 && bracketClose > bracketOpen + 1)
                        Right(rawShow.substring(bracketOpen + 1, bracketClose))
                    else Left(s"Can't extract single year from $rawShow")
        year <- yearEither(yearStrOpt)
    } yield year
}

def parseShow(rawShow: String): Either[String, TvShow] = {
    for {
        name <- extractName(rawShow)
        startYear <- extractStartYear(rawShow).orElse(extractSingleYear(rawShow))
        endYear <- extractEndYear(rawShow).orElse(extractSingleYear(rawShow))
    } yield TvShow(name, startYear, endYear)
}

// def parseShows(rawShows: List[String]): List[TvShow] = {
//     rawShows.map(parseShow).flatMap(_.toList)
// }

def parseShows(rawShows: List[String]): Either[String, List[TvShow]] = {
    rawShows.map(parseShow).foldLeft(Right(List.empty))(addOrResign)
}

type TvShowListEither = Either[String, List[TvShow]]
def addOrResign(parsedShows: TvShowListEither, newParsedShow: Either[String, TvShow]): TvShowListEither = {
    for {
       shows <- parsedShows
       newShow <- newParsedShow
    } yield shows.appended(newShow)
}

// addOrResign(Some(List(TvShow("a", 1, 2), TvShow("b", 3, 4))), Some(TvShow("new", 11, 11)))


// def ps(rawShow: String): Either[String, TvShow] = {
//     extractName(rawShow).flatMap(name => 
//         extractStartYear(rawShow).orElse(extractSingleYear(rawShow)).
//         flatMap(start => extractEndYear(rawShow).orElse(extractSingleYear(rawShow))
//         .map(end => TvShow(name, start, end))
//         )
//     )
// }

// def pss(rawShows: List[String]): Either[String, List[TvShow]] = {
//     val initial: Either[String, List[TvShow]] = Right(List.empty)
//     rawShows.foldLeft(initial)((t, c) => t.flatMap((shows) => ps(c).map(s => shows.appended(s))))
// }

def ps(rawShow: String): Either[String, TvShow] = {
    for {
        name <- extractName(rawShow)
        startYear <- extractStartYear(rawShow).orElse(extractSingleYear(rawShow))
        endYear <- extractEndYear(rawShow).orElse(extractSingleYear(rawShow))
    } yield TvShow(name, startYear, endYear)
}

def aor(shows: Either[String, List[TvShow]], show: Either[String, TvShow]) = {
    for {
        validShows <- shows
        s <- show
    } yield validShows :+ s
}

def pss(rawShows: List[String]): Either[String, List[TvShow]] = {
    rawShows.foldLeft(Right(List.empty): Either[String, List[TvShow]])((accu, curr) => aor(accu, ps(curr)))
}