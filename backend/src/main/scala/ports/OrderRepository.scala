package ports

import cats.effect.IO
import domain.models.Order

/**
 * Order Repository trait defines the interface for user-related database operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
trait OrderRepository:
  def saveOrder(order: Order): IO[Unit]

