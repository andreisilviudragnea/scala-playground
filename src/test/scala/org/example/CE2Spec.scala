package org.example

import cats.effect.IO._
import cats.effect.concurrent.Ref
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

class CE2Spec extends AnyFunSuite with should.Matchers {
  test("ce2") {
    for (_ <- 0 until 1_000_000) {
      val ref = Ref.unsafe(5)

      val va = ref.get.map { v =>
        Console.println(Thread.currentThread().getName)
        v
      }.unsafeRunSync()

      Console.println(va)
    }
  }
}
