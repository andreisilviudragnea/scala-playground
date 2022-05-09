/*
 * Copyright 2022 Andrei Silviu Dragnea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  ignore("ScalaMock5") {
    trait Logger {
      def info(message: => String): IO[Unit]
    }

    val logger = mock[Logger]

    (logger
      .info(_: String))
      .expects("")
      .returning(IO.unit)

    Console.println(s"${logger.info("").unsafeRunSync()}")
  }
}
