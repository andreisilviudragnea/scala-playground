package org.example

class Impl extends Trait {
  override def method(): String = ???

  val anonymous = new Trait {
    override def method(): String = ???
  }
}
