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
import natchez.Trace.Implicits.noop // Обязательный неявный параметр для трейсинга в Skunk

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

  // Конфигурация пула соединений к PostgreSQL через Skunk в виде управляемого ресурса
  private val databasePool: Resource[IO, Resource[IO, Session[IO]]] =
    Session.pooled[IO](
      host     = "localhost", // Измените на "postgres_db" при запуске внутри Docker-сети
      port     = 5432,
      user     = "postgres_admin",
      database = "trading_platform",
      password = Some("secret_password"),
      max      = 10          // Максимальный размер пула соединений
    )

  val run: IO[Unit] =
    for
      _ <- logger.info("Bootstrapping Scala 3 Forex Trading Platform...")

      // Запускаем наше приложение внутри контекста пула баз данных
      _ <- databasePool.use { pool =>
        for
          _ <- logger.info("Database connection pool initialized successfully.")

          // 1. Инициализируем исходящие адаптеры (Инфраструктура / DB)
          userRepository = PostgresUserRepository(pool)

          // 2. Инициализируем ядро бизнес-логики (Domain / Use Cases)
          tradingEngine = TradingEngine(userRepository)

          // 3. Инициализируем входящие HTTP/WebSocket адаптеры
          orderRoutes = OrderRoutes(tradingEngine)
          userRoutes = UserRoutes(userRepository)

          _ <- logger.info("Starting High-Performance Ember HTTP & WebSocket Server...")

          // 4. Запуск неблокирующего сервера Ember с поддержкой WebSockets
          _ <- EmberServerBuilder
            .default[IO]
            .withHost(ipv4"0.0.0.0")
            .withPort(port"8080")
            // Подключаем WebSocketBuilder (wsb) для динамической трансляции котировок FS2 в React
            .withHttpWebSocketApp { wsb =>
              val streamRoutes = StreamRoutes(wsb)

              // Объединяем маршруты всех сущностей (User, Order, Ticker) в единую матрицу API
              val allRoutes = userRoutes.routes <+> orderRoutes.routes <+> streamRoutes.routes

              // Навешиваем CORS-политики, чтобы React-фронтенд мог отправлять запросы
              CORS.policy.withAllowOriginAll.apply(allRoutes).orNotFound
            }
            .build
            .use(_ => logger.info("Forex Trading Platform Core is ONLINE on port 8080!") >> IO.never)
        yield ()
      }
    yield ()

