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
import cats.implicits._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should

import java.util.Random
import java.util.concurrent.ThreadLocalRandom
import java.util.random.RandomGenerator

class RandomSpec extends AnyFunSpec with should.Matchers {
  it("Random") {
    testRandom(new Random)
  }

  it("ThreadLocalRandom") {
    testRandom(ThreadLocalRandom.current())
  }

  def testRandom(randomGenerator: RandomGenerator): Unit = {
    val v: Seq[IO[Unit]] = (0 until 10).map { _ =>
      IO.blocking {
        (0 until 1_000_000).foreach { _ =>
          randomGenerator.nextInt()
        }
      }
    }
    v.parSequence.unsafeRunSync()
    ()
  }
}
