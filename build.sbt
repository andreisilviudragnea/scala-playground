name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.3.4",
  "org.apache.kafka" % "kafka-clients" % "3.1.0",
  "org.scalatest" %% "scalatest" % "3.2.11" % Test,
  "org.scalamock" %% "scalamock" % "5.2.0" % Test,
  "com.github.ben-manes.caffeine" % "caffeine" % "3.0.5",
  "co.fs2" %% "fs2-core" % "3.2.4"
)
