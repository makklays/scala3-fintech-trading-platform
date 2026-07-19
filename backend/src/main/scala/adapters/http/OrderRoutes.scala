package adapters.http

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityDecoder.* // Авто-декодирование JSON в case-классы
import org.http4s.circe.CirceEntityEncoder.* // Авто-кодирование ответа в JSON
import io.circe.generic.auto.*               // Магия авто-генерации JSON-схем в Scala 3
import domain.models.OrderRequest
import domain.TradingEngine

/**
 * Order HTTP Routes Adapter for receiving trade orders from the Frontend.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
class OrderRoutes(tradingEngine: TradingEngine):

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    // POST http://localhost:8080/api/orders
    case req @ POST -> Root / "api" / "orders" =>
      for
        orderReq <- req.as[OrderRequest]
        result   <- tradingEngine.executeTrade(orderReq)
        response <- if result.success then Ok(result) else BadRequest(result)
      yield response
  }

/*
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"username": "demo_user", "symbol": "BTC", "amount": 0.5, "price": 40000.0, "side": "BUY"}'

{
  "success": true,
  "message": "Successfully bought 0.5 BTC"
}

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"username": "demo_user", "symbol": "BTC", "amount": 10.0, "price": 40000.0, "side": "BUY"}'

{
  "success": false,
  "message": "Insufficient USD balance"
}
*/