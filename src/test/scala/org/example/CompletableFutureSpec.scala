package org.example

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.concurrent.CompletableFuture

class CompletableFutureSpec extends AnyFunSuite with should.Matchers {

  test("CompletableFuture") {
    for (_ <- 0 until 100) {
//      val currentThread = Thread.currentThread()
      CompletableFuture
        .supplyAsync(() => 5)
        .thenAccept { _ =>
          val acceptThread = Thread.currentThread()
          println(acceptThread)
//          acceptThread shouldNot be currentThread
        }
    }
  }
}
