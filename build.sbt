name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.2.9",
  "org.apache.kafka" % "kafka-clients" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.2.9" % Test,
  "org.scalamock" %% "scalamock" % "5.1.0" % Test
)
