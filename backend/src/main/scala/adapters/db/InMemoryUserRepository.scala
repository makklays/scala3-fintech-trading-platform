package adapters.db

import cats.effect.*
import ports.UserRepository
import domain.models.User

/**
 * В конструктор передаем Ref (AtomicReference) со слепком Map[String, User]
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 18.07.2026
 */
class InMemoryUserRepository(state: Ref[IO, Map[String, User]]) extends UserRepository:

  def findByUsername(username: String): IO[Option[User]] =
    state.get.map(_.get(username))

  def updateBalances(username: String, usd: Double, btc: Double): IO[Unit] =
    state.update { currentMap =>
      currentMap.get(username) match
        case Some(user) =>
          val updatedUser = user.copy(usdBalance = usd, btcBalance = btc)
          currentMap + (username -> updatedUser)
        case None =>
          currentMap // Если юзер не найден, ничего не меняем
    }

