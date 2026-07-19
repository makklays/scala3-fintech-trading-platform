package domain

import java.time.Instant

// 1. Учет баланса трейдера (Margin Account)
case class User(
  id: Option[Long] = None,
  username: String,
  email: String,
  roles: List[String] = List("USER"),
  mobile: Option[String] = None,
  balance: Double = 0.00,    // Реальные деньги на счете без учета текущих сделок
  usedMargin: Double,        // Залог, заблокированный под открытые позиции
  freeMargin: Double,        // Средства, доступные для открытия новых сделок (Balance - UsedMargin + PnL)
  gender: Option[String] = None,
  age: Option[Int] = None,
  avatar: Option[String] = None,
  password: String,
  createdAt: Instant = Instant.now(),
  updatedAt: Instant = Instant.now()
)

// 2. Входной торговый приказ (Order)
case class OrderRequest(
  username: String,
  instrument: String, // Например, "EUR_USD", "GOLD_FUTURE"
  quantity: Double,   // Объем сделки в лотах (например, 0.1 лота или 1 лот = 100,000 базовой валюты)
  leverage: Int,      // Кредитное плечо, например, 30, 100, 500
  side: String        // "BUY" (Long / на повышение) или "SELL" (Short / на понижение)
)

// 3. Открытая Позиция (Position) — Сердце Forex-платформы
case class Position(
  id: Option[Long] = None,
  userId: Long,
  instrument: String,
  quantity: Double,
  leverage: Int,
  side: String,
  entryPrice: Double,     // Цена, по которой позиция была открыта
  currentPrice: Double,   // Текущая рыночная цена (обновляется из FS2-стрима котировок)
  unrealizedPnL: Double,  // Текущая плавающая прибыль/убыток (Profit and Loss)
  marginRequired: Double, // Залог, который списан под эту конкретную позицию
  status: String,         // "OPEN" или "CLOSED"
  openedAt: Instant = Instant.now(),
  closedAt: Option[Instant] = None
)

// 4. История закрытых сделок (Trade History / Снятие прибыли)
case class TradeRecord(
  id: Option[Long] = None,
  positionId: Long,
  userId: Long,
  instrument: String,
  side: String,
  quantity: Double,
  entryPrice: Double,
  exitPrice: Double,
  realizedPnL: Double,    // Итоговый зафиксированный финансовый результат
  executedAt: Instant = Instant.now()
)

// 5. Итоговый ответ от бэкенда, возвращаемый фронтенду в формате JSON.
case class TradeResult(
  success: Boolean,
  message: String
)

// 6. Рыночная котировка для стрима
case class Ticker(
  instrument: String, // Теперь здесь будет "EUR_USD", "GBP_USD" или "GOLD_FUT"
  price: Double,      // Текущая рыночная цена (на Forex обычно 5 знаков после запятой, например 1.08542)
  timestamp: Instant = Instant.now()
)

