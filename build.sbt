name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.2.8",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "org.apache.kafka" % "kafka-clients" % "3.0.0"
)
