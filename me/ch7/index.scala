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
        case searchByGenre(genres: Set[Genre])
        case searchByLocation(locations: Set[Location])
        case searchByPeriod(start: Int, end: Int)
    }

    opaque type Location = String
    object Location {
      def apply(value: String): Location       = value 
      extension (a: Location) def name: String = a
    }

    enum YearActive {
        case stillActive(since: Int)
        case activeBetween(start: Int, end: Int)
    }

    def activeLength(artist: Artist, currentYear: Int) = {
        artist.yearActive match {
            case YearActive.stillActive(since) => currentYear - since
            case YearActive.activeBetween(start, end) => end - start
        }
    }

    def wasActive(artist: Artist, startYear: Int, endYear: Int) = artist.yearActive.match {
        case YearActive.stillActive(since) => since <= endYear
        case YearActive.activeBetween(start, end) => start <= endYear && end >= startYear
    }
    
    def searchArtists(
        artists: List[Artist], searchCondition: Set[SearchCondition]
    ): List[Artist] = {
        artists.filter(artist => 
            searchCondition.forall(condition => condition match {
                    case SearchCondition.searchByGenre(genres) => genres.contains(artist.genre)
                    case SearchCondition.searchByLocation(locations) => locations.contains(artist.origin)
                    case SearchCondition.searchByPeriod(start, end) => wasActive(artist, start, end)
                } 
            )
        )
    }
}