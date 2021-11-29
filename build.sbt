name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.7"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.3.0",
  "org.apache.kafka" % "kafka-clients" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalamock" %% "scalamock" % "5.1.0" % Test,
  "com.github.ben-manes.caffeine" % "caffeine" % "3.0.4"
)
