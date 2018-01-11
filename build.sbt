name := "formation-scala"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= List(
  "org.typelevel" %% "cats-core" % "1.0.1",
  "io.circe" %% "circe-core" % "0.9.0",
  "io.circe" %% "circe-generic" % "0.9.0",
  "io.circe" %% "circe-parser" % "0.9.0",
  "com.typesafe.play" %% "play-json" % "2.6.8",
  "org.julienrf" %% "play-json-derived-codecs" % "4.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
