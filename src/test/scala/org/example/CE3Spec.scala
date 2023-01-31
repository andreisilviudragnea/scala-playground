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
import cats.effect.IO._
import cats.effect.kernel.Ref
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import cats.implicits._
import com.github.benmanes.caffeine.cache.{CacheLoader, Caffeine}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{CompletableFuture, Executors}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.jdk.FutureConverters.FutureOps
import scala.util.Try

class CE3Spec extends AnyFunSuite with should.Matchers {
  test("ce3") {
    for (_ <- 0 until 1_000_000) {
      val ref = Ref.unsafe(5)
      val thread = new AtomicReference[Thread]()

      ref.get
        .map { v =>
          thread.set(Thread.currentThread())
          v
        }
        .unsafeRunSync()

      thread.get should not be Thread.currentThread()
    }
  }

  test("parTraverse") {
    (1 to 1_000).toList
      .parTraverse(v => IO.sleep(10.seconds) *> IO.pure(v))
      .unsafeRunSync()
  }

  test("fromCompletableFuture") {
    val exception = new RuntimeException
    IO
      .fromCompletableFuture(IO(CompletableFuture.failedFuture(exception)))
      .attempt
      .map {
        case Left(throwable) =>
          throwable shouldBe exception
          None
        case Right(_) => Some(1)
      }
      .unsafeToFuture()
      .asJava
      .toCompletableFuture
      .get() shouldBe None
  }

  ignore("fromCompletableFuture supplyAsync") {
    for (_ <- 0 until 1_000_000) {
      val exception = new RuntimeException
      IO
        .fromCompletableFuture(IO(CompletableFuture.supplyAsync[Int] { () =>
          Thread.sleep(1)
          throw exception
        }))
        .attempt
        .map {
          case Left(throwable) =>
            throwable shouldBe exception
            Some(1)
          case Right(_) => Some(2)
        }
        .unsafeToFuture()
        .asJava
        .toCompletableFuture
        .get() shouldBe Some(1)
    }
  }

  test("Stream") {
    Queue
      .unbounded[IO, Option[Int]]
      .flatMap { queue =>
        for {
          _ <- queue.offer(1.some)
          _ <- queue.offer(2.some)
          _ <- queue.offer(None)
        } yield fs2.Stream.fromQueueNoneTerminated(queue)
      }
      .flatMap { stream =>
        stream
          .evalTap { v => println(v) >> IO.pure(v) }
          .compile
          .drain
      }
      .unsafeRunSync()
  }

  test("evalOn") {
    IO(println("here"))
      .map { _ =>
        println("there")
        ()
      }
      .evalOn(
        ExecutionContext.fromExecutorService(
          Executors.newSingleThreadExecutor()
        )
      )
      .unsafeRunSync()
  }

  test("evalOn timeout not working") {
    Try(
      IO(Thread.sleep(10_000))
        .evalOn(
          ExecutionContext.fromExecutorService(
            Executors.newSingleThreadExecutor()
          )
        )
        .timeout(1.second)
        .unsafeRunSync()
    )
  }

  test("timeout not working") {
    Try(
      IO(Thread.sleep(10_000))
        .timeout(1.second)
        .unsafeRunSync()
    )
  }

  test("blocking timeout not working") {
    Try(
      IO.blocking(Thread.sleep(10_000))
        .timeout(1.second)
        .unsafeRunSync()
    )
  }

  test("interruptible timeout working") {
    Try(
      IO.interruptible(Thread.sleep(10_000))
        .timeout(1.second)
        .unsafeRunSync()
    )
  }

  test("interruptibleMany timeout working") {
    Try(
      IO.interruptibleMany(Thread.sleep(10_000))
        .timeout(1.second)
        .unsafeRunSync()
    )
  }

  test("parSequence") {
    Seq(
      IO.blocking {
        Thread.sleep(3_000)
        Console.println("0")
      },
      IO.blocking {
        Thread.sleep(3_000)
        Console.println("1")
      },
      IO.blocking {
        Thread.sleep(3_000)
        Console.println("2")
      }
    ).parSequence.unsafeRunSync()
  }

  test("parSequence cancellation blocking") {
    Try(
      Seq(
        IO.blocking {
          Thread.sleep(1_000)
          Console.println("0")
          throw new RuntimeException()
        },
        IO.blocking {
          Thread.sleep(2_000)
          Console.println("1")
        }.onCancel(IO {
          Console.println("Cancelled 1")
        }),
        IO.blocking {
          Thread.sleep(3_000)
          Console.println("2")
        }.onCancel(IO {
          Console.println("Cancelled 2")
        })
      ).parSequence.unsafeRunSync()
    )
  }

  test("parSequence cancellation interruptibleMany") {
    Try(
      Seq(
        IO.interruptibleMany {
          Thread.sleep(1_000)
          Console.println("0")
          throw new RuntimeException()
        },
        IO.interruptibleMany {
          Thread.sleep(2_000)
          Console.println("1")
        }.onCancel(IO {
          Console.println("Cancelled 1")
        }),
        IO.interruptibleMany {
          Thread.sleep(3_000)
          Console.println("2")
        }.onCancel(IO {
          Console.println("Cancelled 2")
        })
      ).parSequence.unsafeRunSync()
    )
  }

  test("parSequence evalOn") {
    (0 to 100).toVector
      .map { v =>
        IO {
          Thread.sleep(5_000)
          Console.println(s"${Thread.currentThread().getName} $v")
        }.evalOn(
          ExecutionContext.fromExecutorService(
            Executors.newSingleThreadExecutor()
          )
        )
      }
      .parSequence
      .map { _ => Console.println("the end") }
      .unsafeRunSync()
  }

  test("parSequence evalOn Caffeine cache") {
    val cache = Caffeine
      .newBuilder()
      .initialCapacity(
        100
      ) // without reasonable initial capacity, the cache has very high contention on many concurrent writes
      .build[Int, String](new CacheLoader[Int, String] {
        override def load(key: Int): String = {
          Thread.sleep(5_000)
          key.toString
        }
      })
    (0 to 100).toVector
      .map { key =>
        IO {
          val value = cache.get(key)
          Console.println(s"${Thread.currentThread().getName} $value")
        }.evalOn(
          ExecutionContext.fromExecutorService(
            Executors.newSingleThreadExecutor()
          )
        )
      }
      .parSequence
      .map { _ => Console.println("the end") }
      .unsafeRunSync()
  }

  test("parSequence evalOn Caffeine two caches") {
    val cache = Caffeine
      .newBuilder()
      .initialCapacity(
        100
      ) // without reasonable initial capacity, the cache has very high contention on many concurrent writes
      .build[Int, String](new CacheLoader[Int, String] {
        override def load(key: Int): String = {
          Thread.sleep(5_000)
          key.toString
        }
      })
    val executorCache = Caffeine
      .newBuilder()
      .initialCapacity(
        100
      ) // without reasonable initial capacity, the cache has very high contention on many concurrent writes
      .build[Int, ExecutionContext](new CacheLoader[Int, ExecutionContext] {
        override def load(key: Int): ExecutionContext = {
//          Thread.sleep(5_000)
          ExecutionContext
            .fromExecutorService(Executors.newSingleThreadExecutor())
        }
      })
    (0 to 100).toVector
      .map { key =>
        IO {
          val value = cache.get(key)
          Console.println(s"${Thread.currentThread().getName} $value")
        }.evalOn(executorCache.get(key))
      }
      .parSequence
      .map { _ => Console.println("the end") }
      .unsafeRunSync()
  }

  test("parSequence evalOn Caffeine async cache") {
    val cache = Caffeine
      .newBuilder()
//      .initialCapacity(
//        100
//      ) // without reasonable initial capacity, the cache has very high contention on many concurrent writes
      .buildAsync[Int, String](new CacheLoader[Int, String] {
        override def load(key: Int): String = {
          Console.println(s"Init ${Thread.currentThread().getName} $key")
          Thread.sleep(5_000)
          key.toString
        }
      })
    (0 to 100).toVector
      .map { key =>
        IO.fromCompletableFuture(IO(cache.get(key)))
          .map { value =>
            Console.println(s"${Thread.currentThread().getName} $value")
          }
          .evalOn(
            ExecutionContext.fromExecutorService(
              Executors.newSingleThreadExecutor()
            )
          )
      }
      .parSequence
      .map { _ => Console.println("the end") }
      .unsafeRunSync()
  }

  test("parSequence blocking") {
    (0 to 100).toVector
      .map { v =>
        IO.blocking {
          Thread.sleep(5_000)
          Console.println(s"${Thread.currentThread().getName} $v")
        }
      }
      .parSequence
      .map { _ => Console.println("the end") }
      .unsafeRunSync()
  }
}
