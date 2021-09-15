package org.example

import cats.effect.IO._
import cats.effect.kernel.Ref
import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

class CE3Spec extends AnyFunSuite with should.Matchers {
  test("ce3") {
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
