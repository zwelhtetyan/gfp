import cats.effect.{IO, Ref, Resource}
import cats.implicits._
import cats.effect.unsafe.implicits.global
import WikidataAccess.getSparqlDataAccess
import org.apache.jena.query.{QueryExecution, QueryFactory, QuerySolution}
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionRemote}

import scala.concurrent.duration._
import scala.jdk.javaapi.CollectionConverters.asScala

object Learn {
  // Data Model
  object model {
    opaque type LocationId = String
    object LocationId {
      def apply(value: String): LocationId = value
      extension (a: LocationId) def value: String = a
    }

    case class Location(
      id: LocationId,
      name: String,
      population: Long
    )

    case class Attraction(
      name: String,
      description: Option[String],
      location: Location
    )

    enum PopCultureSubject {
      case Artist(name: String, followers: Int)
      case Movie(name: String, boxOffice: Int)
    }

    case class TravelGuide(attraction: Attraction, subjects: List[PopCultureSubject])
  }
  import model._, model.PopCultureSubject._

  // Data access
  enum AttractionOrdering {
    case ByName
    case ByLocationPopulation
  }

  trait DataAccess {
    def findArtistsFromLocation(locationId: LocationId, limit: Int): IO[List[Artist]]
    def findMoviesAboutLocation(locationId: LocationId, limit: Int): IO[List[Movie]]
    def findAttractions(name: String, ordering: AttractionOrdering, limit: Int): IO[List[Attraction]]
  }

  // Query Services
  def createExecution(connection: RDFConnection, query: String): IO[QueryExecution] = IO.blocking(
    connection.query(QueryFactory.create(query))
  )
  def closeExecution(execution: QueryExecution): IO[Unit] = IO.blocking(
    execution.close()
  )
  def execQuery(connection: RDFConnection)(query: String): IO[List[QuerySolution]] = {
    val executionResource: Resource[IO, QueryExecution] = 
      Resource.make(createExecution(connection, query))(closeExecution)

    executionResource.use(execution => IO.blocking(asScala(execution.execSelect()).toList))
  }

  def parseAttraction(s: QuerySolution): IO[Attraction] = {
    IO.delay(
      Attraction(
        name = s.getLiteral("attractionLabel").getString,
        description = if (s.contains("description")) Some(s.getLiteral("description").getString) else None,
        location = Location(
          id = LocationId(s.getResource("location").getLocalName),
          name = s.getLiteral("locationLabel").getString,
          population = s.getLiteral("population").getInt
        )
      )
    )
  }

  // Business logic
  def guideScore(guide: TravelGuide): Int = {
    val descriptionScore = guide.attraction.description.map(_ => 30).getOrElse(0)
    val quantityScore    = Math.min(40, guide.subjects.size * 10)

    val totalFollowers = guide.subjects
      .map(_ match {
        case Artist(_, followers) => followers
        case _                    => 0
      })
      .sum
    val totalBoxOffice = guide.subjects
      .map(_ match {
        case Movie(_, boxOffice) => boxOffice
        case _                   => 0
      })
      .sum

    val followersScore = Math.min(15, totalFollowers / 100_000)
    val boxOfficeScore = Math.min(15, totalBoxOffice / 10_000_000)
    descriptionScore + quantityScore + followersScore + boxOfficeScore
  }

  def travelGuide(data: DataAccess, attractionName: String): IO[Option[TravelGuide]] = {
    for {
      attractions <- data.findAttractions(attractionName, AttractionOrdering.ByLocationPopulation, 3)
      guide <- attractions.map(attraction => {
        List(
          data.findArtistsFromLocation(attraction.location.id, 2),
          data.findMoviesAboutLocation(attraction.location.id, 2)
        ).parSequence.map(_.flatten)
        .map(popCultureSubjects => TravelGuide(attraction, popCultureSubjects))
      }).parSequence
    } yield guide.sortBy(guideScore).reverse.headOption
  }

  // Main Application
  val connectionResource: Resource[IO, RDFConnection] = Resource.make(
    IO.blocking(
      RDFConnectionRemote.create
        .destination("https://query.wikidata.org/")
        .queryEndpoint("sparql")
        .build
    )
  )(connection => IO.blocking(connection.close()))

  val dataAccessResource = connectionResource.map(connection => getSparqlDataAccess(execQuery(connection)))
  
  val program: IO[Option[TravelGuide]] = dataAccessResource.use(dataAccess => travelGuide(dataAccess, "Yellowstone"))

  // program.unsafeRunSync()
}
