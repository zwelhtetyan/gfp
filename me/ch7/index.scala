import javax.swing.Popup
import java.time.Year

object model {
    case class Artist(name: String, genre: Genre, origin: Location, yearActive: YearActive)

    enum Genre {
        case HipHop
        case Pop
        case Rock
    }

    enum SearchCondition {
        case SearchByGenre(genres: Set[Genre])
        case SearchByLocation(locations: Set[Location])
        case SearchByPeriod(periods: ActiveBetween)
        case SearchByActiveLength(howLong: Int, until: Int)
    }

    opaque type Location = String
    object Location {
      def apply(value: String): Location = value
      extension (a: Location) def name: String = a
    }

    case class ActiveBetween(start: Int, end: Int)
    enum YearActive {
        case StillActive(since: Int, previousPeriods: List[ActiveBetween])
        case YearsInPeriod(periods: List[ActiveBetween])
    }

    def activeLength(artist: Artist, currentYear: Int) = {
        val periods = artist.yearActive match {
            case YearActive.StillActive(since, previousPeriods) => 
                previousPeriods.appended(ActiveBetween(since, currentYear))
            case YearActive.YearsInPeriod(periods) => periods
        }

        periods.map(p => p.end - p.start).foldLeft(0)((x, y) => x + y)
    }

    def periodOverlapsWithPeriods(checkedPeriod: ActiveBetween, periods: List[ActiveBetween]): Boolean = {
        periods.exists(p => p.start <= checkedPeriod.end && p.end >= checkedPeriod.start)
    }

    def wasArtistActive(artist: Artist, searchedPeriod: ActiveBetween): Boolean = {
        artist.yearActive match {
            case YearActive.StillActive(since, previousPeriods) =>
                since <= searchedPeriod.end || periodOverlapsWithPeriods(searchedPeriod, previousPeriods) 
            case YearActive.YearsInPeriod(periods) =>
                periodOverlapsWithPeriods(searchedPeriod, periods)
        }
    }
    
    def searchArtists(
        artists: List[Artist], searchCondition: Set[SearchCondition]
    ): List[Artist] = {
        artists.filter(artist => 
            searchCondition.forall(condition => condition match {
                    case SearchCondition.SearchByGenre(genres) => genres.contains(artist.genre)
                    case SearchCondition.SearchByLocation(locations) => locations.contains(artist.origin)
                    case SearchCondition.SearchByPeriod(periods) => wasArtistActive(artist, periods)
                    case SearchCondition.SearchByActiveLength(howLong, until) => activeLength(artist, until) >= howLong
                } 
            )
        )
    }
}