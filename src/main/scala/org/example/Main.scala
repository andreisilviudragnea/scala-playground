package org.example

import cats.effect.IO._
import cats.effect.kernel.Ref
import cats.effect.unsafe.implicits.global

object Main extends App {
  for (_ <- 0 until 1_000_000) {
    val ref = Ref.unsafe(5)

    val va = ref.get.map { v =>
      Console.println(Thread.currentThread().getName)
      v
    }.unsafeRunSync()

    Console.println(va)
  }
}
