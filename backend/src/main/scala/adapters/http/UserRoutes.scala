package adapters.http

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityDecoder.* // Авто-декодирование JSON из тела запроса
import org.http4s.circe.CirceEntityEncoder.* // Авто-кодирование объектов в JSON ответ
import io.circe.generic.auto.*               // Компилятор сам генерирует JSON схемы
import ports.UserRepository
import domain.models.DepositRequest

/**
 * User HTTP Routes Adapter for profile management and balance operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
class UserRoutes(userRepo: UserRepository):

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    // 1. GET http://localhost:8080/api/users/demo_user
    // Запрос профиля и текущих балансов для React-компонента
    case GET -> Root / "api" / "users" / username =>
      userRepo.findByUsername (username).flatMap {
        case Some (user) => Ok (user) // Возвращает полную модель User со всеми полями как JSON
        case None => NotFound (s"User with username '$username' not found")
      }

    // 2. POST http://localhost:8080/api/users/demo_user/deposit
    // Симуляция зачисления демо-USD на баланс пользователя
    case req @POST -> Root / "api" / "users" / username / "deposit" =>
      userRepo.findByUsername (username).flatMap {
        case Some (user) =>
          for
            deposit <- req.as[DepositRequest]
            newUsd = user.usdBalance + deposit.amount
            _ <- userRepo.updateBalances (user.username, newUsd, user.btcBalance)
            response <- Ok (s"Successfully deposited $${deposit.amount} USD. New balance: $$newUsd USD")
          yield response
        case None =>
          NotFound (s"User '$username' not found")
      }
  }

