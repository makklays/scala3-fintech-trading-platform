package adapters.db

import skunk.*
import skunk.implicits.*
import skunk.codec.all.*
import domain.models.Position
import java.time.OffsetDateTime
import java.time.Instant

/**
 * SQL Queries and Codecs definition for the 'positions' table using Skunk DSL.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 19.07.2026
 */
object PositionSql:

  // Изоморфизм для работы Skunk с типами Instant через OffsetDateTime
  implicit val instantIso: Isomorphism[OffsetDateTime, Instant] =
    Isomorphism(_.toInstant, _.atOffset(java.time.ZoneOffset.UTC))

  // Кодек для маппинга полей таблицы positions в case class Position (ровно 13 полей)
  val positionCodec: Codec[Position] =
    (int8.opt ~ int8 ~ varchar ~ numeric ~ int4 ~ varchar ~ numeric ~ numeric ~ numeric ~ numeric ~ varchar ~ timestamptz ~ timestamptz.opt).gmap[Position]

  // Команда на добавление (открытие) новой маржинальной позиции
  // Порядок плейсхолдеров $ в VALUES строго совпадает с порядком полей в кодеке!
  val insertPosition: Command[Position] =
    sql"""
      INSERT INTO positions (
        id, user_id, instrument, quantity, leverage, side,
        entry_price, current_price, unrealized_pnl, margin_required,
        status, opened_at, closed_at
      )
      VALUES (
        ${int8.opt}, $int8, $varchar, $numeric, $int4, $varchar,
        $numeric, $numeric, $numeric, $numeric,
        $varchar, $timestamptz, ${timestamptz.opt}
      )
    """.command

