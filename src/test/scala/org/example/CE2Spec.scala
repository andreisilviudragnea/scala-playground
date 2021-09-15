package org.example

import cats.effect.IO._
import cats.effect.concurrent.Ref
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.concurrent.atomic.AtomicReference

class CE2Spec extends AnyFunSuite with should.Matchers {
  test("ce2") {
    for (_ <- 0 until 1_000_000) {
      val ref = Ref.unsafe(5)
      val thread = new AtomicReference[Thread]()

      ref.get.map { v =>
        thread.set(Thread.currentThread())
        v
      }.unsafeRunSync()

      thread.get should be(Thread.currentThread())
    }
  }
}
