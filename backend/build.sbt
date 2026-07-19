import sbt.*  // Принудительный импорт sbt API для IDE
import sbt.Keys.*

val Scala3Version = "3.3.3"
val Http4sVersion = "0.23.27"
val CirceVersion  = "0.14.7"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "fintech-backend",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := Scala3Version,

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"         % "3.5.4",
      "co.fs2"        %% "fs2-core"            % "3.10.2",
      "org.http4s"    %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"    %% "http4s-dsl"          % Http4sVersion,
      "org.http4s"    %% "http4s-circe"        % Http4sVersion,
      "io.circe"      %% "circe-generic"       % CirceVersion,
      "io.circe"      %% "circe-parser"        % CirceVersion,
      "org.typelevel" %% "log4cats-slf4j"      % "2.7.0",
      "ch.qos.logback" % "logback-classic"     % "1.5.6",

      // Добавьте эту строчку внутрь libraryDependencies ++= Seq( ... )
      "org.tpolecat" %% "skunk-core" % "0.6.3",

      // Добавьте это в libraryDependencies ++= Seq( ... ) в вашем build.sbt// Добавьте это в libraryDependencies ++= Seq( ... ) в вашем build.sbt
      "org.flywaydb" % "flyway-core" % "10.15.0",
      "org.flywaydb" % "flyway-database-postgresql" % "10.15.0",
      "org.postgresql" % "postgresql" % "42.7.3"
    )
  )

