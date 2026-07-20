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

  // Вспомогательный хелпер для конвертации Instant в OffsetDateTime (Skunk работает с временными зонами через OffsetDateTime)
  implicit val instantIso: Isomorphism[OffsetDateTime, Instant] =
    Isomorphism(_.toInstant, _.atOffset(java.time.ZoneOffset.UTC))

  // Кодек маппит ровно 14 колонок из БД в кейс-класс User (через метод .gmap)
  val userCodec: Codec[User] =
    (int8.opt ~ varchar ~ varchar ~ text.list ~ varchar.opt ~ numeric ~ numeric ~ numeric ~ varchar.opt ~ int4.opt ~ varchar.opt ~ varchar ~ timestamptz ~ timestamptz).gmap[User]

  // Метод для поиска пользователя по ID
  // На вход принимает Long (id), на выходе возвращает User
  val selectById: Query[Long, User] =
    sql"""
      SELECT id, username, email, roles, mobile, balance, used_margin, free_margin, gender, age, avatar, password, created_at, updated_at
      FROM users
      WHERE id = $int8
    """.query (userCodec)


  // 1. Запрос на поиск пользователя по имени (Ровно 14 колонок в SELECT)
  val selectByUsername: Query[String, User] =
    sql"""
      SELECT id, username, email, roles, mobile, balance, used_margin, free_margin, gender, age, avatar, password, created_at, updated_at
      FROM users
      WHERE username = $varchar
    """.query(userCodec)

  // 2. Команда на обновление маржинальных балансов
  // На вход принимает кортеж параметров (balance, used_margin, free_margin, username)
  val updateBalances: Command[BigDecimal ~ BigDecimal ~ BigDecimal ~ String] =
    sql"""
      UPDATE users
      SET balance = $numeric,
          used_margin = $numeric,
          free_margin = $numeric,
          updated_at = now()
      WHERE username = $varchar
    """.command

