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

import io.circe.generic.JsonCodec
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import io.circe.{Codec, DecodingFailure}
import org.example.CirceGenericAdtSpec.Base
import org.example.CirceGenericAdtSpec.Base._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

object CirceGenericAdtSpec {
  @JsonCodec sealed trait Base

  object Base {
    @JsonCodec final case class A(a: String) extends Base
    @JsonCodec final case class B(b: Int) extends Base
  }
}

class CirceGenericAdtSpec extends AnyFunSuite with should.Matchers {
  test("io.circe.generic.JsonCodec ADT") {
    val a = A("15")
    val encodedA = "{\"a\":\"15\"}"

    a.asJson.noSpaces shouldBe encodedA
    decode[A](encodedA) shouldBe Right(a)

    decode[Base](encodedA) shouldBe Left(
      DecodingFailure("JSON decoding to CNil should never happen", List.empty)
    )

    val b = B(4)
    val encodedB = "{\"b\":4}"

    b.asJson.noSpaces shouldBe encodedB
    decode[B](encodedB) shouldBe Right(b)

    decode[Base](encodedB) shouldBe Left(
      DecodingFailure("JSON decoding to CNil should never happen", List.empty)
    )
  }

  test("null handling JsonCodec") {
    @JsonCodec final case class Test(a: Option[String])
    val a = Test(None)
    val encoded = "{\"a\":null}"

    a.asJson.noSpaces shouldBe encoded
    decode[Test](encoded) shouldBe Right(a)

    decode[Test](encoded) shouldBe Right(a)
  }

  test("missing field handling JsonCodec") {
    @JsonCodec final case class Test(a: Option[String])
    val a = Test(None)
    val encoded = "{}"

    a.asJson.noSpaces shouldBe "{\"a\":null}"
    decode[Test](encoded) shouldBe Right(a)

    decode[Test](encoded) shouldBe Right(a)
  }

  test("null handling Codec.forProduct") {
    final case class Test(a: Option[String])

    object Test {
      implicit val testCodec = Codec.forProduct1("a")(Test.apply)(v => v.a)
    }

    val a = Test(None)
    val encoded = "{\"a\":null}"

    a.asJson.noSpaces shouldBe encoded
    decode[Test](encoded) shouldBe Right(a)

    decode[Test](encoded) shouldBe Right(a)
  }

  test("missing field handling Codec.forProduct") {
    final case class Test(a: Option[String])

    object Test {
      implicit val testCodec = Codec.forProduct1("a")(Test.apply)(v => v.a)
    }

    val a = Test(None)
    val encoded = "{}"

    a.asJson.noSpaces shouldBe "{\"a\":null}"
    decode[Test](encoded) shouldBe Right(a)

    decode[Test](encoded) shouldBe Right(a)
  }
}
