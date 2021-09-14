package org.example

import cats.effect.concurrent.Ref
import cats.effect.IO._

object Main extends App {
  val ref = Ref.unsafe(5)

  val va = ref.get.map { v =>
    println(Thread.currentThread().getName)
    v
  }.unsafeRunSync()

  println(va)
}
