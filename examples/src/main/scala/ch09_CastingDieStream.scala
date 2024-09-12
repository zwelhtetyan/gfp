import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2.{Pure, Stream}

object ch09_CastingDieStream extends App {
  { // creating values
    val numbers    = Stream(1, 2, 3)
    val oddNumbers = numbers.filter(_ % 2 != 0)

    assert(oddNumbers.toList == List(1, 3))
    assert(numbers.toList == List(1, 2, 3))
    assert(oddNumbers.map(_ + 17).take(1).toList == List(18))
  }

  { // append & take
    val stream1 = Stream(1, 2, 3)
    val stream2 = Stream(4, 5, 6)

    val stream3 = stream1.append(stream2)
    assert(stream3.toList == List(1, 2, 3, 4, 5, 6))

    val stream4 = stream1.append(stream1)
    assert(stream4.toList == List(1, 2, 3, 1, 2, 3))

    val stream5 = stream4.take(4)
    assert(stream5.toList == List(1, 2, 3, 1))
  }

  { // infinite streams
    def numbers(): Stream[Pure, Int] = {
      Stream(1, 2, 3).append(numbers())
    }

    val infinite123s = numbers()
    assert(infinite123s.take(8).toList == List(1, 2, 3, 1, 2, 3, 1, 2))
  }

  { // infinite streams using .repeat
    val numbers = Stream(1, 2, 3).repeat

    assert(numbers.take(8).toList == List(1, 2, 3, 1, 2, 3, 1, 2))
  }

  { // quick exercise: what do these expression return
    assert(Stream(1).repeat.take(3).toList == List(1, 1, 1))
    assert(Stream(1).append(Stream(0, 1).repeat).take(4).toList == List(1, 0, 1, 0))
    assert(Stream(2).map(_ * 13).repeat.take(1).toList == List(26))
    assert(Stream(13).filter(_ % 2 != 0).repeat.take(2).toList == List(13, 13))
  }

  { // streams of IO values
    import ch08_CastingDieImpure.NoFailures.castTheDieImpure

    def castTheDie(): IO[Int] = IO.delay(castTheDieImpure())

    val dieCast: Stream[IO, Int]         = Stream.eval(castTheDie())
    val oneDieCastProgram: IO[List[Int]] = dieCast.compile.toList

    assert(oneDieCastProgram.unsafeRunSync().size == 1)

    val infiniteDieCasts: Stream[IO, Int]      = Stream.eval(castTheDie()).repeat
    val infiniteDieCastsProgram: IO[List[Int]] = infiniteDieCasts.compile.toList
    // println(infiniteDieCastsProgram.unsafeRunSync()) // will never finish

    val infiniteDieCastsProgramDrain: IO[Unit] = infiniteDieCasts.compile.drain
    // println(infiniteDieCastsProgramDrain.unsafeRunSync()) // will never finish

    val firstThreeCasts: IO[List[Int]] = infiniteDieCasts.take(3).compile.toList
    assert(firstThreeCasts.unsafeRunSync().size == 3)

    val six: IO[List[Int]] = infiniteDieCasts.filter(_ == 6).take(1).compile.toList
    assert(six.unsafeRunSync() == List(6))

    // Practicing stream operations:
    // 1. filter odd numbers only and return the first three such casts
    assert(infiniteDieCasts.filter(_ % 2 != 0).take(3).compile.toList.unsafeRunSync().size == 3)

    // 2. return first five die casts, but make sure all sixes values are doubled (so 1, 2, 3, 6, 4 becomes 1, 2, 3, 12, 4)
    assert {
      val result = infiniteDieCasts.take(5).map(x => if (x == 6) 12 else x).compile.toList.unsafeRunSync()
      !result.contains(6) && result.size == 5
    }

    // 3. return the sum of the first three casts
    assert {
      val result = infiniteDieCasts.take(3).compile.toList.map(_.sum).unsafeRunSync()
      result >= 3 && result <= 18
    }

    // 4. cast the die until there is a five and then cast it two more times, returning three last results back
    assert {
      val result =
        infiniteDieCasts.filter(_ == 5).take(1).append(infiniteDieCasts.take(2)).compile.toList.unsafeRunSync()
      result.size == 3 && result.head == 5
    }

    // 5. make sure the die is cast one hundred times and values are discarded
    assert(infiniteDieCasts.take(100).compile.drain.isInstanceOf[IO[Unit]])

    // 6. return first three casts unchanged and next three casts tripled (six in total)
    assert {
      val result = infiniteDieCasts.take(3).append(infiniteDieCasts.take(3).map(_ * 3)).compile.toList.unsafeRunSync()
      result.size == 6 && result.slice(0, 3).forall(_ <= 6) && result.slice(3, 6).forall(_ >= 3)
    }

    // 7. cast the die until there are two sixes in a row
    assert(
      infiniteDieCasts
        .scan(0)((sixesInRow, current) => if (current == 6) sixesInRow + 1 else 0)
        .filter(_ == 2)
        .take(1)
        .compile
        .toList
        .unsafeRunSync() == List(2)
    )
  }
}
