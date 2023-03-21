package org.example

import org.example.bug.SomeObject
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import scala.util.{Failure, Try}

class BugSpec extends AnyFunSuite with should.Matchers {
  ignore("init bug") {
    SomeObject.field1
  }
}
