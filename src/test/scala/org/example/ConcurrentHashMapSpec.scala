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
import cats.implicits.catsSyntaxParallelSequence1
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.concurrent.ConcurrentHashMap

class ConcurrentHashMapSpec extends AnyFunSuite with should.Matchers {
  test("ConcurrentHashMap.computeIfAbsent() does not block reads") {
    val map = new ConcurrentHashMap[String, String]

    Seq(
      IO.blocking {
        map.computeIfAbsent(
          "key",
          { _ =>
            Thread.sleep(5_000)
            "value"
          }
        )
      },
      IO.blocking {
        println("start key1")
        map.get("key1") shouldBe null
        println("end key1")
      },
      IO.blocking {
        Thread.sleep(1_000)
        println("start")
        map.get("key") shouldBe null
        println("end1")
        map.get("key") shouldBe null
        println("end2")
        map.get("key") shouldBe null
        println("end3")
        map.get("key") shouldBe null
        println("end4")
        map.get("key") shouldBe null
        println("end5")
        Thread.sleep(5_000)
        map.get("key") shouldBe "value"
        println("end6")
      }
    ).parSequence.unsafeRunSync()
  }

  test("ConcurrentHashMap.computeIfAbsent() blocks writes") {
    val map = new ConcurrentHashMap[String, String]

    Seq(
      IO.blocking {
        println("start1")
        map.computeIfAbsent(
          "key",
          { _ =>
            println("executed1")
            Thread.sleep(5_000)
            "value"
          }
        )
        println("end1")
      },
      IO.blocking {
        println("start2")
        map.computeIfAbsent(
          "key",
          { _ =>
            println("executed2")
            Thread.sleep(5_000)
            "value"
          }
        )
        println("end2")
      }
    ).parSequence.unsafeRunSync()
  }
}
