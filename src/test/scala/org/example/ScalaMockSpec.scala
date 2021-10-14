package org.example

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

class ScalaMockSpec extends AnyFunSuite with should.Matchers with MockFactory {
  test("ScalaMock") {
    trait Logger {
      def info(message: => String): IO[Unit]
    }

    val logger = mock[Logger]

    (logger.info _: (=> String) => IO[Unit])
      .expects(*)
      .returning(IO.unit)

    Console.println(s"${logger.info("").unsafeRunSync()}")
  }

  test("ScalaMock2") {
    trait Logger {
      def info(message: String): IO[Unit]
    }

    val logger = mock[Logger]

    (logger.info _)
      .expects(*)
      .returning(IO.unit)

    Console.println(s"${logger.info("").unsafeRunSync()}")
  }

  test("ScalaMock3") {
    trait Logger {
      def info(message: String): IO[Unit]
    }

    val logger = stub[Logger]

    (logger.info _)
      .when(*)
      .returns(IO.unit)

    Console.println(s"${logger.info("").unsafeRunSync()}")

    (logger.info _).verify("")
  }

  test("ScalaMock4") {
    trait Logger {
      def info(message: => String): IO[Unit]
    }

    val logger = stub[Logger]

    (logger.info _: (=> String) => IO[Unit])
      .when(*)
      .returns(IO.unit)

    Console.println(s"${logger.info("").unsafeRunSync()}")

    (logger.info _: (=> String) => IO[Unit]).verify(*)
  }

  test("ScalaMock5") {
    trait Logger {
      def info(message: => String): IO[Unit]
    }

    val logger = mock[Logger]

    (logger.info(_: String))
      .expects("")
      .returning(IO.unit)

    Console.println(s"${logger.info("").unsafeRunSync()}")
  }
}
