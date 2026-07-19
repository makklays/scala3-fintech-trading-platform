package domain

import cats.effect.IO
import fs2.Stream
import domain.models.Ticker
import java.time.Instant
import scala.concurrent.duration.*
import scala.util.Random

/**
 * Market Data Generator using FS2 Streams for real-time price simulation.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
object MarketData:

  // Бесконечный поток котировок Биткоина
  val forexStream: Stream[IO, Ticker] =
    // Стрим просыпается каждые 500мс
    Stream.awakeEvery[IO](500.millis)
      .evalMap { _ =>
        IO.delay {
          // Симулируем микро-колебания (пипсы) для пары EUR/USD в районе 1.08500
          val randomChange = (Random.nextDouble() - 0.5) * 0.00040
          val currentPrice = 1.08500 + randomChange

          Ticker(
            instrument = "EUR_USD",
            // Округляем до 5 знаков после запятой — стандарт Forex
            price = BigDecimal(currentPrice).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble,
            timestamp = Instant.now()
          )
        }
      }

