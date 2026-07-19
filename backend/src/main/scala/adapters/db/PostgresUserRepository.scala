package adapters.db

import cats.effect.*
import ports.UserRepository
import domain.models.User
import skunk.*
import skunk.implicits.*

/**
 * Production-ready Postgres implementation of UserRepository using Skunk.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
class PostgresUserRepository(pool: Resource[IO, Session[IO]]) extends UserRepository:

  override def findByUsername(username: String): IO[Option[User]] =
    // Берём свободную сессию из пула соединений
    pool.use { session =>
      // Готовим (prepare) SQL-запрос на уровне СУБД для защиты от SQL-инъекций
      session.prepare(UserSql.selectByUsername).flatMap { preparedQuery =>
        // Выполняем и забираем первую найденную строку
        preparedQuery.option(username)
      }
    }

  override def updateBalances(username: String, usd: Double, btc: Double): IO[Unit] =
    // В маржинальной торговле мы обновляем баланс счета. Пересчитаем freeMargin прямо перед записью
    pool.use { session =>
      session.prepare(UserSql.updateBalances).flatMap { preparedCommand =>
        // Конвертируем double в BigDecimal, как требует тип данных NUMERIC в PostgreSQL
        val usdBd = BigDecimal(usd)
        val btcBd = BigDecimal(btc) // В нашей Forex-модели это будет usedMargin и freeMargin

        // Выполняем команду, передавая кортеж параметров через Skunk-оператор ~
        preparedCommand.execute(usdBd ~ btcBd ~ btcBd ~ username).void
      }
    }

