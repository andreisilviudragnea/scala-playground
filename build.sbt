import sbt.Keys.startYear

name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.11"

inThisBuild(
  Seq(
    crossScalaVersions := Seq("2.13.11"),
    tlBaseVersion := "0.1",
    organization := "io,dragnea",
    organizationName := "Andrei Silviu Dragnea",
    startYear := Some(2022),
    githubWorkflowJavaVersions := Seq(JavaSpec.oracle("20")),
    tlJdkRelease := Some(20),
    scalacOptions ++= Seq("-Ymacro-annotations", "-Xlint:-byname-implicit")
  )
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.6-0142603",
  "org.apache.kafka" % "kafka-clients" % "3.5.1",
  "org.scalatest" %% "scalatest" % "3.2.16" % Test,
  "org.scalamock" %% "scalamock" % "5.2.0" % Test,
  "com.github.ben-manes.caffeine" % "caffeine" % "3.1.6",
  "co.fs2" %% "fs2-core" % "3.8-1580d81",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
)
