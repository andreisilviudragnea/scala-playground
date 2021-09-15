package org.example

import cats.effect.concurrent.Ref
import cats.effect.IO._

object Main extends App {
  for (_ <- 0 until 1_000_000) {
    val ref = Ref.unsafe(5)

    val va = ref.get.map { v =>
      println(Thread.currentThread().getName)
      v
    }.unsafeRunSync()

    println(va)
  }
}
