name := "formation-scala"

version := "1.0"

scalaVersion := "2.12.2"

lazy val versions = new {
  val cats = "1.0.1"
  val circe = "0.9.0"
  val playjson = "2.6.8"
  val playjsonderived = "4.0.0"
  val akkahttp = "10.0.11"
  val scalatest = "3.0.4"

  val finatra = "18.2.0"
  val pureconfig = "0.9.0"
  val guice = "4.0"
  val logback = "1.2.3"
  val mockito = "1.9.5"
}

libraryDependencies ++= List(
  "org.typelevel" %% "cats-core" % versions.cats,
  "io.circe" %% "circe-core" % versions.circe,
  "io.circe" %% "circe-generic" % versions.circe,
  "io.circe" %% "circe-parser" % versions.circe,
  "com.typesafe.play" %% "play-json" % versions.playjson,
  "org.julienrf" %% "play-json-derived-codecs" % versions.playjsonderived,
  "com.typesafe.akka" %% "akka-http" % versions.akkahttp,
  "org.scalatest" %% "scalatest" % versions.scalatest % "test",

  "com.twitter" %% "finatra-http" % versions.finatra,
  "com.github.pureconfig" %% "pureconfig" % versions.pureconfig,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "com.twitter" %% "finatra-http" % versions.finatra % "test",
  "com.twitter" %% "inject-server" % versions.finatra % "test",
  "com.twitter" %% "inject-app" % versions.finatra % "test",
  "com.twitter" %% "inject-core" % versions.finatra % "test",
  "com.twitter" %% "inject-modules" % versions.finatra % "test",
  "com.twitter" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
  "org.mockito" % "mockito-core" % versions.mockito % "test"
)
