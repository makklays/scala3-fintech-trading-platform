package adapters.http

import cats.effect.IO
import fs2.Pipe
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import io.circe.generic.auto.*
import io.circe.syntax.* // Позволяет делать .asJson.noSpaces
import domain.MarketData

/**
 * Stream Routes WebSocket Adapter for pushing real-time market data to React.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
class StreamRoutes(wsb: WebSocketBuilder2[IO]):

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    // WS http://localhost:8080/api/ws/ticks
    case GET -> Root / "api" / "ws" / "ticks" =>

      // Направление ОТ сервера К клиенту (React)
      val toClient = MarketData.btcStream.map { ticker =>
        // Конвертируем кейс-класс в JSON-строку и заворачиваем в WebSocket текстовый фрейм
        WebSocketFrame.Text(ticker.asJson.noSpaces)
      }

      // Направление ОТ клиента К серверу (в данном случае мы просто игнорируем входящие сообщения)
      val fromClient: Pipe[IO, WebSocketFrame, Unit] = _.void

      // Склеиваем потоки в одно WebSocket-соединение
      wsb.build(toClient, fromClient)
  }
