package adapters.db

import skunk.*
import skunk.implicits.*
import skunk.codec.all.*
import domain.models.User
import java.time.OffsetDateTime
import java.time.Instant

/**
 * SQL Queries and Codecs definition for the 'users' table using Skunk DSL.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
object UserSql:

  // Настраиваем кодек, который маппит поля строки БД в наш кейс-класс User и обратно
  val userCodec: Codec[User] =
    (int8.opt ~ varchar ~ varchar ~ text.list ~ varchar.opt ~ numeric ~ numeric ~ numeric ~ varchar.opt ~ int4.opt ~ varchar.opt ~ varchar ~ timestamptz ~ timestamptz).gmap[User]

  // 1. Запрос на поиск пользователя по имени
  val selectByUsername: Query[String, User] =
    sql"""
      SELECT id, username, email, roles, mobile, usd_balance, btc_balance, gender, age, avatar, password, created_at, updated_at
      FROM users
      WHERE username = $varchar
    """.query(userCodec)

  // 2. Команда на обновление маржинальных балансов
  // На вход принимает кортеж (usd_balance, used_margin, free_margin, username)
  val updateBalances: Command[BigDecimal ~ BigDecimal ~ BigDecimal ~ String] =
    sql"""
      UPDATE users
      SET usd_balance = $numeric,
          used_margin = $numeric,
          free_margin = $numeric,
          updated_at = now()
      WHERE username = $varchar
    """.command

  // Вспомогательный хелпер для конвертации Instant в OffsetDateTime (Skunk работает с временными зонами через OffsetDateTime)
  implicit val instantIso: Isomorphism[OffsetDateTime, Instant] =
    Isomorphism(_.toInstant, _.atOffset(java.time.ZoneOffset.UTC))

