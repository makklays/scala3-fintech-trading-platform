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
      "ch.qos.logback" % "logback-classic"     % "1.5.6"
    )
  )

