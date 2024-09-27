import cats.effect.{IO, Ref}
import cats.implicits._
import cats.effect.unsafe.implicits.global
import fs2.Stream

import scala.concurrent.duration._

object Learn {
    opaque type City = String
    object City {
        def apply(name: String): City = name
        extension (city: City) def name: String = city
    }
    case class CityStats(city: City, checkIns: Int)
    case class ProcessingCheckIns(currentRanking: IO[List[CityStats]], stop: IO[Unit])

    def processCheckIns(checkIns: Stream[IO, City]): IO[ProcessingCheckIns] = {
        for {
            storedCheckIns <- Ref.of[IO, Map[City, Int]](Map.empty)
            storedRanking <- Ref.of[IO, List[CityStats]](List.empty)

            rankingProgram = updateRanking(storedCheckIns, storedRanking)
            checkInsProgram = checkIns.evalMap(storeCheckIn(storedCheckIns)).compile.drain

            fiber <- List(rankingProgram, checkInsProgram).parSequence.start
        } yield ProcessingCheckIns(storedRanking.get, fiber.cancel)
    }

    // def processCheckIns(checkIns: Stream[IO, City]): IO[Unit] = {
    //     for {
    //         storedCheckIns <- Ref.of[IO, Map[City, Int]](Map.empty)
    //         storedRanking <- Ref.of[IO, List[CityStats]](List.empty)

    //         rankingProgram = updateRanking(storedCheckIns, storedRanking)
    //         checkInsProgram = checkIns.evalMap(storeCheckIn(storedCheckIns)).compile.drain
    //         outputProgram = showCurrentRankingPerSecond(storedRanking)

    //         _ <- List(rankingProgram, checkInsProgram, outputProgram).parSequence
    //     } yield ()
    // }

    def showCurrentRankingPerSecond(storedRanking: Ref[IO, List[CityStats]]): IO[Nothing] = {
        (for {
            _ <- IO.sleep(1.second)
            _ <- storedRanking.get.flatMap(IO.println)
        } yield ()).foreverM
    }

    def updateRanking(
        storedCheckIns: Ref[IO, Map[City, Int]], 
        storedRanking: Ref[IO, List[CityStats]]
    ): IO[Nothing] = {
        (for {
           rankings <- storedCheckIns.get.map(topCities)
           _ <- storedRanking.set(rankings)

        } yield ()).foreverM
    }

    def storeCheckIn(storedCheckIns: Ref[IO, Map[City, Int]])(city: City): IO[Unit] = {
        storedCheckIns.update(_.updatedWith(city)(_ match {
            case None => Some(1)
            case Some(checkIns) => Some(checkIns + 1)
        }))
    }

    def topCities(cityCheckIns: Map[City, Int]): List[CityStats] = {
        cityCheckIns
        .toList
        .map(_ match {
            case(city, checkIns) => CityStats(city, checkIns)
        })
        .sortBy(_.checkIns)
        .reverse
        .take(3)
    }
}

// val checkIns: Stream[IO, City] = Stream(
//         City("Sydney"),
//         City("Sydney"),
//         City("Cape Town"),
//         City("Singapore"),
//         City("Cape Town"),
//         City("Sydney")
//     ).covary[IO]

// val checkIns: Stream[IO, City] =
//     Stream(City("Sydney"), City("Dublin"), City("Cape Town"), City("Lima"), City("Singapore"))
//     .repeatN(100_000)
//     .append(Stream.range(0, 100_000).map(i => City(s"City $i")))
//     .append(Stream(City("Sydney"), City("Sydney"), City("Lima")))
//     .covary[IO]

