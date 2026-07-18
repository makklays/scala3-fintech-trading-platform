import cats.effect.*
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityDecoder.*
import io.circe.generic.auto.*
import org.typelevel.log4cats.slf4j.Slf4jLogger

case class Order(symbol: String, amount: Double, side: String)

object Main extends IOApp.Simple:
private val logger = Slf4jLogger.getLogger[IO]

val tradingRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "api" / "orders" =>
    for
      order <- req.as[Order]
  _     <- logger.info(s"Получен ордер от React: $order")
  resp  <- Ok(s"Ордер на ${order.symbol} успешно обработан бэкендом")
  yield resp
}

val corsService = CORS.policy.withAllowOriginAll.apply(tradingRoutes)

val run: IO[Unit] =
  EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(corsService.orNotFound)
    .build
    .use(_ => logger.info("Scala 3 Бэкенд успешно запущен на порту 8080!") >> IO.never)

