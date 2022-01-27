package org.example

import cats.effect.IO
import cats.effect.IO._
import cats.effect.kernel.Ref
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import cats.implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.DurationInt
import scala.jdk.FutureConverters.FutureOps

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

  test("fromCompletableFuture supplyAsync") {
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
}
