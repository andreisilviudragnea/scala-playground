import sbt.Keys.startYear

name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.8"

inThisBuild(
  Seq(
    crossScalaVersions := Seq("2.13.8"),
    tlBaseVersion := "0.1",
    organization := "io,dragnea",
    organizationName := "Andrei Silviu Dragnea",
    startYear := Some(2022),
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))
  )
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.4-389-3862cf0",
  "org.apache.kafka" % "kafka-clients" % "3.2.0",
  "org.scalatest" %% "scalatest" % "3.2.12" % Test,
  "org.scalamock" %% "scalamock" % "5.2.0" % Test,
  "com.github.ben-manes.caffeine" % "caffeine" % "3.1.1",
  "co.fs2" %% "fs2-core" % "3.2.10"
)
