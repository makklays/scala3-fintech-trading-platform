package domain

import cats.effect.IO
import ports.UserRepository
import domain.models.{OrderRequest, TradeResult, User}

/**
 * TradingEngine is responsible for executing margin trades based on incoming orders.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 18.07.2026
 *
 * @param userRepo Outgoing adapter interface for user data operations
 */
class TradingEngine(userRepo: UserRepository):

  def executeTrade(order: OrderRequest): IO[TradeResult] =
    userRepo.findByUsername(order.username).flatMap {
      case Some(user) =>
        if order.side.toUpperCase == "BUY" then
          processBuy(user, order)
        else if order.side.toUpperCase == "SELL" then
          processSell(user, order)
        else
          IO.pure(TradeResult(success = false, message = s"Unknown side: ${order.side}"))
      case None =>
        IO.pure(TradeResult(success = false, message = s"User ${order.username} not found"))
    }

  // Логика маржинальной покупки (Long позиция на Forex)
  private def processBuy(user: User, order: OrderRequest): IO[TradeResult] =
    // Стандартный лот на Forex = 100,000 базовой валюты.
    val contractSize = 100000.0
    val totalPositionValue = order.quantity * contractSize

    // Рассчитываем необходимый залог (Margin) с учетом кредитного плеча
    // Например: 1 лот EUR/USD при плече 1:100 требует залога $1,000
    val marginRequired = totalPositionValue / order.leverage

    // Проверяем, достаточно ли у трейдера свободных денег (Free Margin) для залога
    if user.freeMargin >= marginRequired then
      val updatedUser = user.copy(
        usedMargin = user.usedMargin + marginRequired,
        freeMargin = user.freeMargin - marginRequired
      )

      // Вызываем порт для обновления балансовых показателей счета
      userRepo.updateBalances(updatedUser.username, updatedUser.usedMargin, updatedUser.freeMargin)
        .as(TradeResult(
          success = true,
          message = s"Successfully opened LONG position for ${order.quantity} lot(s) of ${order.instrument}. Margin locked: $$marginRequired USD."
        ))
    else
      IO.pure(TradeResult(success = false, message = s"Insufficient Free Margin. Required: $$marginRequired USD"))

  // Логика маржинальной продажи (Short позиция на Forex)
  private def processSell(user: User, order: OrderRequest): IO[TradeResult] =
    val contractSize = 100000.0
    val totalPositionValue = order.quantity * contractSize
    val marginRequired = totalPositionValue / order.leverage

    if user.freeMargin >= marginRequired then
      val updatedUser = user.copy(
        usedMargin = user.usedMargin + marginRequired,
        freeMargin = user.freeMargin - marginRequired
      )

      userRepo.updateBalances(updatedUser.username, updatedUser.usedMargin, updatedUser.freeMargin)
        .as(TradeResult(
          success = true,
          message = s"Successfully opened SHORT position for ${order.quantity} lot(s) of ${order.instrument}. Margin locked: $$marginRequired USD."
        ))
    else
      IO.pure(TradeResult(success = false, message = s"Insufficient Free Margin. Required: $$marginRequired USD"))

