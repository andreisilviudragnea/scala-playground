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

      val future = cache.get("")

      // when buildAsync closure fails, the future is removed from the map on another thread
      // however, cache.asMap() can sometimes still catch the exceptionally completed future inside the map

      cache
        .asMap()
        .forEach((_, v) => {
          try {
            v.get()
          } catch {
            case e: Throwable =>
              Console.println(s"Failed at iteration $i")
              throw e
          }
        })
    }
  }
}
