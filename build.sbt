name := """ReactiveDemo"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M2",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-M2"
)
