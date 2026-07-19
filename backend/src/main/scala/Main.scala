import cats.effect.*
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import org.typelevel.log4cats.slf4j.Slf4jLogger

// Импортируем компоненты нашей гексагональной структуры
import domain.models.User
import domain.TradingEngine
import adapters.db.InMemoryUserRepository
import adapters.http.OrderRoutes

/**
 * Main Class for the Scala 3 backend application.
 * Composition Root that initializes layers and boots the server.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 18.07.2026
 */
object Main extends IOApp.Simple:
  private val logger = Slf4jLogger.getLogger[IO]

  val run: IO[Unit] =
    for
      _ <- logger.info("Initializing In-Memory Database State...")

      // 1. Создаем демо-пользователя, наполняя структуру согласно новой схеме PostgreSQL
      demoUser = User(
        id = Some(1L),
        username = "demo_user",
        email = "demo@techmatrix18.com",
        usdBalance = 50000.00, // Стартовые 50k USD
        btcBalance = 1.25,     // Стартовые 1.25 BTC
        password = "encrypted_password_here"
      )

      // 2. Инициализируем атомарный контейнер состояния базы данных в памяти (Ref)
      dbState <- Ref.of[IO, Map[String, User]](Map("demo_user" -> demoUser))

      // 3. Собираем зависимости гексагональной архитектуры (Связывание слоев)
      userRepository = InMemoryUserRepository(dbState)
      tradingEngine  = TradingEngine(userRepository)
      orderRoutes    = OrderRoutes(tradingEngine)
      userRoutes     = UserRoutes(userRepository)

      // 4. Оборачиваем наши HTTP-роуты в middleware для поддержки CORS (запросы от React)
      corsService = CORS.policy.withAllowOriginAll.apply(orderRoutes.routes)

      _ <- logger.info("Starting Http4s Ember Server with WebSockets...")

      // 5. Запуск сервера с поддержкой WebSockets
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        // Подключаем WebSocketBuilder на этапе инициализации HTTP-приложения
        .withHttpWebSocketApp(wsb =>
          val streamRoutes = StreamRoutes(wsb)
          // Склеиваем все роуты вместе с помощью оператора <+>
          val allRoutes = userRoutes.routes <+> orderRoutes.routes <+> streamRoutes.routes
          CORS.policy.withAllowOriginAll.apply(allRoutes).orNotFound
        )
        .build
        .use(_ => logger.info("Scala 3 Trading Platform is ONLINE!") >> IO.never)
    yield ()

