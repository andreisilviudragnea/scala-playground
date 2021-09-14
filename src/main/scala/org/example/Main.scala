package org.example

import cats.effect.IO._
import cats.effect.kernel.Ref
import cats.effect.unsafe.implicits.global

object Main extends App {
  val ref = Ref.unsafe(5)

  val va = ref.get.map { v =>
    Console.println(Thread.currentThread().getName)
    v
  }.unsafeRunSync()

  Console.println(va)
}
