package ports

import cats.effect.IO
import domain.models.User

/**
 * User Repository trait defines the interface for user-related database operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 18.07.2026
 */
trait UserRepository:
  def findByUsername(username: String): IO[Option[User]]

  // Обновленная сигнатура под маржинальную модель Forex
  def updateBalances(username: String, usedMargin: Double, freeMargin: Double): IO[Unit]

