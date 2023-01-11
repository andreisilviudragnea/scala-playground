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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.concurrent.{ConcurrentLinkedQueue, Executors}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success

class FutureSpec extends AnyFunSuite with should.Matchers {
  private val range: Range = 0 until 100

  test("future execution context") {
    Future("")
      .map { v =>
        println(Thread.currentThread())
        v
      }
      .map { v =>
        println(Thread.currentThread())
        v
      }(
        ExecutionContext.fromExecutorService(
          Executors.newSingleThreadExecutor()
        )
      )
  }

  test("map") {
    val promise = Promise[String]()
    promise.complete(Success(""))

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      promise.future.map { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }
    }

    Thread.sleep(1_000)

    val array = queue.toArray
    array.size shouldBe 100
//    array shouldBe range.toArray
  }

  test("map future completed later") {
    val promise = Promise[String]()

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      promise.future.map { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }
    }

    promise.complete(Success(""))
    Thread.sleep(1_000)

    val array = queue.toArray
    array.size shouldBe 100
//    array shouldBe range.toArray
  }

  test("map single-thread executor") {
    val promise = Promise[String]()
    promise.complete(Success(""))

    val queue = new ConcurrentLinkedQueue[Int]()
    val executionContext = ExecutionContext.fromExecutorService(
      Executors.newSingleThreadExecutor()
    )

    range.foreach { v =>
      promise.future.map { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }(executionContext)
    }

    Thread.sleep(1_000)

    val array = queue.toArray
    array.size shouldBe 100
    array shouldBe range.toArray
  }

  test("map single-thread executor future completed later") {
    val promise = Promise[String]()

    val queue = new ConcurrentLinkedQueue[Int]()
    val executionContext = ExecutionContext.fromExecutorService(
      Executors.newSingleThreadExecutor()
    )

    range.foreach { v =>
      promise.future.map { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }(executionContext)
    }

    promise.complete(Success(""))
    Thread.sleep(1_000)

    val array = queue.toArray
    array.size shouldBe 100
    array shouldBe range.toArray.reverse
  }

  test("map same-thread executor") {
    val promise = Promise[String]()
    promise.complete(Success(""))

    val queue = new ConcurrentLinkedQueue[Int]()

    range.foreach { v =>
      promise.future.map { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }(ExecutionContext.parasitic)
    }

    val array = queue.toArray
    array.size shouldBe 100
    array shouldBe range.toArray
  }

  test("map same-thread executor future completed later") {
    val promise = Promise[String]()

    val queue = new ConcurrentLinkedQueue[Int]()

    range.foreach { v =>
      promise.future.map { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }(ExecutionContext.parasitic)
    }

    promise.complete(Success(""))

    val array = queue.toArray
    array.size shouldBe 100
    array shouldBe range.toArray.reverse
  }
}
