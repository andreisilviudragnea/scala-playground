package org.example

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class FutureSpec extends AnyFunSuite with should.Matchers {
  test("Future") {
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
}
