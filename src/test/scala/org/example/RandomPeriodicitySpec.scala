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

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should

import scala.util.Random

/** @see
  *   https://en.wikipedia.org/wiki/Linear_congruential_generator
  * @see
  *   https://www.javamex.com/tutorials/random_numbers/lcg_bit_positions.shtml
  */
class RandomPeriodicitySpec extends AnyFunSpec with should.Matchers {
  it(s"Random.nextInt().toByte periodicity (1L << 24) == 16_777_216") {
    val random = new Random
    periodicity(1L << 24, random.nextInt().toByte)
  }

  it(
    s"Random.nextInt().toByte generates the same sequence of values, no matter the seed; the seed only shifts the values"
  ) {
    val period = 1 << 24

    val r1 = new Random()
    val a = Array.fill(period)(r1.nextInt().toByte)

    val r2 = new Random()
    val b = Array.fill(2 * period)(r2.nextInt().toByte)

    b.containsSlice(a) shouldBe true
  }

  //  it(s"Random.nextInt() periodicity (1L << 48) == 281_474_976_710_656") {
  //    val random = new Random
  //    periodicity(1L << 48, random.nextInt())
  //  }
  //
  //  it(s"sid periodicity (1L << (48 - 3)) == 35_184_372_088_832") {
  //    val random = new Random
  //    periodicity(1L << 45, ApiSid.make(0)(random.nextBytes(_)))
  //  }

  @SuppressWarnings(
    Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.while")
  )
  private def periodicity[T](period: Long, block: => T): Unit = {
    val v1 = block
    var count = 0
    while (count < period - 1) {
      block
      count += 1
    }
    val v2 = block
    v2 shouldBe v1
    ()
  }
}
