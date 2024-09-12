import cats.effect.IO
import ch11_TravelGuide._
import ch11_TravelGuide.model._, PopCultureSubject._
import ch11_TravelGuide.AttractionOrdering._
import org.apache.jena.query.QuerySolution

object ch11_WikidataDataAccess {

  def getSparqlDataAccess(execQuery: String => IO[List[QuerySolution]]): DataAccess = new DataAccess {
    val prefixes = """
        |PREFIX wd: <http://www.wikidata.org/entity/>
        |PREFIX wdt: <http://www.wikidata.org/prop/direct/>
        |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        |PREFIX schema: <http://schema.org/>
        |""".stripMargin

    def findAttractions(name: String, ordering: AttractionOrdering, limit: Int): IO[List[Attraction]] = {
      val orderBy = ordering match {
        case ByName               => "?attractionLabel"
        case ByLocationPopulation => "DESC(?population)"
      }

      val query = s"""
        |$prefixes
        |SELECT DISTINCT ?attraction ?attractionLabel ?description ?location ?locationLabel ?population WHERE {
        |  ?attraction wdt:P31 wd:Q570116;
        |              rdfs:label ?attractionLabel;
        |              wdt:P131 ?location.
        |  FILTER(LANG(?attractionLabel) = "en").
        |
        |  OPTIONAL {
        |    ?attraction schema:description ?description.
        |    FILTER(LANG(?description) = "en").
        |  }
        |
        |  ?location wdt:P1082 ?population;
        |            rdfs:label ?locationLabel;
        |  FILTER(LANG(?locationLabel) = "en").
        |
        |  FILTER(CONTAINS(?attractionLabel, "$name")).
        |} ORDER BY $orderBy LIMIT $limit
        |""".stripMargin

      for {
        solutions   <- execQuery(query)
        attractions <- IO.delay(
                         solutions.map(s =>
                           Attraction( // introduce named parameters
                             name = s.getLiteral("attractionLabel").getString,
                             description =
                               if (s.contains("description")) Some(s.getLiteral("description").getString) else None,
                             location = Location(
                               LocationId(s.getResource("location").getLocalName),
                               s.getLiteral("locationLabel").getString,
                               s.getLiteral("population").getInt
                             )
                           )
                         )
                       )
      } yield attractions
    }

    def findArtistsFromLocation(locationId: LocationId, limit: Int): IO[List[Artist]] = {
      val query = s"""
        |$prefixes
        |SELECT DISTINCT ?artist ?artistLabel ?followers WHERE {
        |  ?artist wdt:P136 ?genre;
        |          wdt:P8687 ?followers;
        |          rdfs:label ?artistLabel.
        |  FILTER(LANG(?artistLabel) = "en").
        |
        |  ?artist wdt:P740 wd:${locationId.value}
        |
        |} ORDER BY DESC(?followers) LIMIT $limit
        |""".stripMargin

      for {
        solutions <- execQuery(query)
        artists   <-
          IO.delay(
            solutions.map[Artist](s =>
              Artist(name = s.getLiteral("artistLabel").getString, followers = s.getLiteral("followers").getInt)
            )
          )
      } yield artists
    }

    def findMoviesAboutLocation(locationId: LocationId, limit: Int): IO[List[Movie]] = {
      val query = s"""
        |$prefixes
        |SELECT DISTINCT ?subject ?subjectLabel ?boxOffice WHERE {
        |  ?subject wdt:P31 wd:Q11424;
        |           wdt:P2142 ?boxOffice;
        |           rdfs:label ?subjectLabel.
        |
        |  ?subject wdt:P840 wd:${locationId.value}
        |
        |  FILTER(LANG(?subjectLabel) = "en").
        |
        |} ORDER BY DESC(?boxOffice) LIMIT $limit
        |""".stripMargin

      for {
        solutions <- execQuery(query)
        movies    <- IO.delay(
                       solutions.map[Movie](s =>
                         Movie(name = s.getLiteral("subjectLabel").getString, boxOffice = s.getLiteral("boxOffice").getInt)
                       )
                     )
      } yield movies
    }

  }

}
