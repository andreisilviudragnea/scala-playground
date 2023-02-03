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

import com.github.benmanes.caffeine.cache._
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import scala.concurrent.duration.DurationInt
import scala.jdk.DurationConverters.ScalaDurationOps
import scala.util.Try

class CaffeineSpec extends AnyFunSuite with should.Matchers {

  ignore("caffeine race condition") {
    for (i <- 0 until 100) {
      val cache: AsyncLoadingCache[String, String] = Caffeine
        .newBuilder()
        .buildAsync { _: String =>
          if (true) throw new RuntimeException else ""
        }

      cache.get("")

      // when buildAsync closure fails, the future is removed from the map on another thread
      // however, cache.asMap() can sometimes still catch the exceptionally completed future inside the map

      cache
        .asMap()
        .forEach((_, v) => {
          try {
            v.get()
            ()
          } catch {
            case e: Throwable =>
              Console.println(s"Failed at iteration $i")
              throw e
          }
        })
    }
  }

  test("cleanUp") {
    val cache: LoadingCache[String, String] = Caffeine
      .newBuilder()
      .removalListener { (k: String, v: String, removalCause: RemovalCause) =>
        println(s"${Thread.currentThread()}")
        println(s"Removed k=$k v=$v removalCause=$removalCause")
      }
      .build { k: String =>
        println(s"Added $k")
        "value"
      }

    cache.get("a")
    cache.get("b")

    cache.cleanUp()

    Thread.sleep(1_000)
  }

  test("removalListener remove") {
    val cache: LoadingCache[String, String] = Caffeine
      .newBuilder()
      .removalListener { (k: String, v: String, removalCause: RemovalCause) =>
        println(s"${Thread.currentThread()}")
        println(s"Removed k=$k v=$v removalCause=$removalCause")
      }
      .build { k: String =>
        println(s"Added $k")
        "value"
      }

    cache.get("a")
    cache.get("b")

    val map = cache.asMap()
    map.remove("a")
    map.remove("b")
  }

  test("removalListener invalidateAll unbounded local cache") {
    val cache: LoadingCache[String, String] = Caffeine
      .newBuilder()
      .removalListener { (k: String, v: String, removalCause: RemovalCause) =>
        println(s"${Thread.currentThread()}")
        println(s"Removed k=$k v=$v removalCause=$removalCause")
      }
      .build { k: String =>
        println(s"Added $k")
        "value"
      }

    cache.get("a")
    cache.get("b")

    cache.invalidateAll()
  }

  test("removalListener invalidateAll bounded local cache") {
    val cache: LoadingCache[String, String] = Caffeine
      .newBuilder()
      .removalListener { (k: String, v: String, removalCause: RemovalCause) =>
        println(s"${Thread.currentThread()}")
        println(s"Removed k=$k v=$v removalCause=$removalCause")
      }
      .expireAfterAccess(5.seconds.toJava)
      .build { k: String =>
        println(s"Added $k")
        "value"
      }

    cache.get("a")
    cache.get("b")

    cache.invalidateAll()
  }

  test("AsyncLoadingCache failing future") {
    val cache: AsyncLoadingCache[String, String] = Caffeine
      .newBuilder()
      .expireAfterAccess(100.seconds.toJava)
      .buildAsync { (_, _) =>
        Thread.sleep(1_000)
        throw new RuntimeException("failed here")
      }

    Try(
      cache
        .get("a")
        .whenComplete((_, throwable) => {
          println(s"here ${throwable.getMessage}")
        })
    ).failure.exception.getMessage shouldBe "failed here"
  }

  test("AsyncLoadingCache failing future 2") {
    val cache: AsyncLoadingCache[String, String] = Caffeine
      .newBuilder()
      .expireAfterAccess(100.seconds.toJava)
      .buildAsync { _ =>
        Thread.sleep(3_000)
        throw new RuntimeException("failed here")
      }

    val future = cache.get("a")

    (0 to 100).foreach { v =>
      future
        .thenAcceptAsync { value =>
          println(s"won't happen $v $value")
        }
        .whenComplete((_, throwable) => {
          println(
            s"${Thread.currentThread().getName} $v ${throwable.getMessage}"
          )
        })
    }

    Thread.sleep(5_000)
  }
}
