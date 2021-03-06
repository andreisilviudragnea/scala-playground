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

import com.github.benmanes.caffeine.cache.{AsyncLoadingCache, Caffeine}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

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
}
