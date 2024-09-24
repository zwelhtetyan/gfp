import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._
import fs2.Stream

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.jdk.CollectionConverters.MapHasAsScala

object Learn {
    object model { 
      opaque type Currency = String
      object Currency {
        def apply(name: String): Currency = name
        extension (currency: Currency) def name: String = currency
      }
    }
    import model._
    import ch08_SchedulingMeetings.retry
    
    def exchangeRatesTableApiCall(currency: String): Map[String, BigDecimal] = {
      ch09_CurrencyExchangeImpure.exchangeRatesTableApiCall(currency).asScala.view.mapValues(BigDecimal(_)).toMap
    }

    def exchangeTableApi(from: Currency): IO[Map[Currency, BigDecimal]] = {
      IO.delay(exchangeRatesTableApiCall(from.name)).map(table => table.map(kv => kv match {
        case(currency, rate) => (Currency(currency), rate)
      }))
    }

    def extractSingleCurrencyRate(to: Currency)(rates: Map[Currency, BigDecimal]): Option[BigDecimal] = {
      rates.get(to)
    }

    def getSingleRate(from: Currency, to: Currency): IO[BigDecimal] = {
      for {
        table <- retry(exchangeTableApi(from), 11)
        rate <- extractSingleCurrencyRate(to)(table) match {
          case Some(rate) => IO.pure(rate)
          case None => getSingleRate(from, to)
        }
      } yield rate
    }

    def getRateList(from: Currency, to: Currency, n: Int): IO[List[BigDecimal]] = {
      if (n < 1) IO.pure(List.empty)
      else {
        for {
          rate <- getSingleRate(from, to)
          remainingRates <- if (n == 1) IO.pure(List.empty)
              else getRateList(from, to, n -1)
        } yield remainingRates.appended(rate)
      }
    }

    def isTrending(rateList: List[BigDecimal]): Boolean = {
      rateList.size > 1 && rateList.zip(rateList.drop(1)).forall(ratePair => ratePair match {
        case (prevRate, currRate) => currRate > prevRate
      })
    }

    def exchangeCurrency(from: Currency, to: Currency, amount: BigDecimal): IO[BigDecimal] = {
      for {
        rateList <- getRateList(from, to, 3)
        result <- if (isTrending(rateList)) IO.pure(rateList.last * amount)
                else exchangeCurrency(from, to, amount)
      } yield result
    }

    // Stream based implementation
    def rateStream(from: Currency, to: Currency): Stream[IO, BigDecimal] = {
      Stream.eval(exchangeTableApi(from)).repeat.map(extractSingleCurrencyRate(to)).unNone.orElse(rateStream(from, to))
    }
    
    val delay: FiniteDuration = FiniteDuration(1, TimeUnit.SECONDS)
    val ticks: Stream[IO, Unit] = Stream.fixedRate[IO](delay)
    
    def streamBasedExchangeCurrency(from: Currency, to: Currency, amount: BigDecimal): IO[BigDecimal] = {
      rateStream(from, to)
      .zipLeft(ticks)
      .sliding(3)
      .map(_.toList)
      .filter(isTrending)
      .map(_.last)
      .take(1)
      .compile
      .lastOrError
      .map(_ * amount)
    }

    // val program = exchangeCurrency(Currency("USD"), Currency("EUR"), 111)
    // val program = streamBasedExchangeCurrency(Currency("USD"), Currency("EUR"), 111)
    // program.unsafeRunSync()
  }