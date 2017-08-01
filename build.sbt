name := "formation-scala"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= List(
  "io.circe" %% "circe-core" % "0.8.0",
  "io.circe" %% "circe-generic" % "0.8.0",
  "io.circe" %% "circe-parser" % "0.8.0",
  "com.typesafe.play" %% "play-json" % "2.6.0-M3",
  "org.julienrf" %% "play-json-derived-codecs" % "4.0.0-RC1",
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)
