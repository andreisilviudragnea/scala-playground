package org.example.bug

object SomeObject {
  val field1 = field2.substring(0)
  val field2 = ""
}
