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

import java.util.concurrent.{
  CompletableFuture,
  ConcurrentLinkedQueue,
  Executors
}

class CompletableFutureSpec extends AnyFunSuite with should.Matchers {

  private val range: Range = 0 until 100

  test("CompletableFuture") {
    for (_ <- range) {
      val currentThread = Thread.currentThread()
      CompletableFuture
        .supplyAsync(() => 5)
        .thenAccept { _ =>
          val acceptThread = Thread.currentThread()
          println(acceptThread)
          acceptThread shouldNot equal(currentThread)
          ()
        }
    }
  }

  test("thenAcceptAsync") {
    val future = CompletableFuture.completedFuture("")

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      future.thenAcceptAsync { _ =>
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

  test("thenAcceptAsync future completed later") {
    val future = new CompletableFuture[String]()

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      future.thenAcceptAsync { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }
    }

    future.complete("")

    Thread.sleep(1_000)

    val array = queue.toArray
    array.size shouldBe 100
//    array shouldBe range.toArray
  }

  test("thenAcceptAsync single-threaded executor") {
    val future = CompletableFuture.completedFuture("")

    val executor = Executors.newSingleThreadExecutor()

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      future.thenAcceptAsync(
        { _ =>
          queue.offer(v)
          println(Thread.currentThread())
          ()
        },
        executor
      )
    }

    Thread.sleep(1_000)

    val array = queue.toArray
    array.size shouldBe 100
    array shouldBe range.toArray
  }

  test("thenAcceptAsync single-threaded executor future completed later") {
    val future = new CompletableFuture[String]()

    val executor = Executors.newSingleThreadExecutor()

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      future.thenAcceptAsync(
        { _ =>
          queue.offer(v)
          println(Thread.currentThread())
          ()
        },
        executor
      )
    }

    future.complete("")

    Thread.sleep(1_000)

    val array = queue.toArray
    array.size shouldBe 100
//    array shouldBe range.toArray.reverse
  }

  test("thenAccept") {
    val future = CompletableFuture.completedFuture("")

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      future.thenAccept { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }
    }

    val array = queue.toArray
    array.size shouldBe 100
    array shouldBe range.toArray
  }

  test("thenAccept future completed later") {
    val future = new CompletableFuture[String]()

    val queue = new ConcurrentLinkedQueue[Int]()
    range.foreach { v =>
      future.thenAccept { _ =>
        queue.offer(v)
        println(Thread.currentThread())
        ()
      }
    }

    future.complete("")

    val array = queue.toArray
    array.size shouldBe 100
    array shouldBe range.toArray.reverse
  }
}
