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

import java.util.concurrent._
import java.util.concurrent.atomic.AtomicLong
import scala.util.control.Breaks.{break, breakable}

class ExecutorSpec extends AnyFunSuite with should.Matchers {
  test("Single thread executor") {
    testExecutorService(Executors.newSingleThreadExecutor())
  }

  test("Single thread executor order") {
    testExecutorServiceOrder(Executors.newSingleThreadExecutor())
  }

  test("Fork-join pool single thread executor") {
    testExecutorService(singleThreadForkJoinPool())
  }

  test("Fork-join pool single thread executor order") {
    testExecutorServiceOrder(singleThreadForkJoinPool())
  }

  test("Fork-join pool single thread executor order asyncMode == false") {
    testExecutorServiceOrder(singleThreadForkJoinPool(asyncMode = false))
  }

  private def singleThreadForkJoinPool(asyncMode: Boolean = true) = {
    new ForkJoinPool(
      1,
      ForkJoinPool.defaultForkJoinWorkerThreadFactory,
      null,
      asyncMode,
      1,
      1,
      0,
      null,
      60_000,
      TimeUnit.MILLISECONDS
    )
  }

  private def testExecutorServiceOrder(executor: ExecutorService) = {
    val queue = new ConcurrentLinkedQueue[Int]()

    val n = 10_000
    val range = 0 until n

    range.foreach { v =>
      executor.execute { () =>
        queue.offer(v)
        ()
      }
    }

    executor.close()

    val array = queue.toArray
    array.size shouldBe n
    array shouldBe range.toArray
  }

  private def testExecutorService(executor: ExecutorService) = {
    val threadCount = 10

    val work = new AtomicLong()

    val startTime = System.currentTimeMillis()

    val threads = (0 until threadCount).map { _ =>
      new Thread({ () =>
        breakable {
          while (true) {
            if (work.get() > 10_000_000) {
              println(
                s"Done in ${(System.currentTimeMillis() - startTime) / 1_000} seconds"
              )
              break()
            } else {
              executor.execute { () =>
                work.getAndIncrement()
//                println(s"${Thread.currentThread().getName} Work ${work.get()}")
                ()
              }
            }
          }
        }
      })
    }

    threads.foreach(_.start())
    threads.foreach(_.join())
    executor.shutdownNow()
  }
}
