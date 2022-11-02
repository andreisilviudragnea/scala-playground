/*
 * Copyright 2022 Andrei Silviu Dragnea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example

import io.circe.Decoder
import io.circe.generic.semiauto._
import org.example.CirceGenericSemiAutoSpec.Person
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

object CirceGenericSemiAutoSpec {
  final case class Person(
      f1: String,
      f2: Int,
      f3: BigDecimal,
      f4: BigInt,
      f5: String,
      f6: String,
      f7: String,
      f8: String,
      f9: String,
      f10: String,
      address: Address
  )

  object Person {
    implicit val personDecoder: Decoder[Person] = deriveDecoder
  }

  final case class Address(
      name: Option[String]
  )

  object Address {
    implicit val addressDecoder: Decoder[Address] = deriveDecoder
  }
}

class CirceGenericSemiAutoSpec extends AnyFunSuite with should.Matchers {
  test("io.circe.generic.semiauto._") {
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
    println(Decoder[Person])
  }
}
