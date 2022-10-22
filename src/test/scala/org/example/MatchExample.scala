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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MatchExample extends AnyFunSuite with Matchers {
  test("match example boolean") {
    def func(pair: (Boolean, Boolean)): String = {
      pair match {
        case (true, true)   => "both true"
        case (true, false)  => "first true"
        case (false, true)  => "second true"
        case (false, false) => "both false"
      }
    }

    func((true, false)) shouldBe "first true"
    func((true, true)) shouldBe "both true"
  }

  test("match example null") {
    def func(pair: (Option[String], Option[String])): String = {
      pair match {
        case (Some(_), Some(_)) => "s1 and s2 not None"
        case (Some(_), None)    => "s2 None"
        case (None, Some(_))    => "s1 None"
        case (None, None)       => "s1 and s2 None"
      }
    }

    func((Some(""), Some(""))) shouldBe "s1 and s2 not None"
    func((Some(""), None)) shouldBe "s2 None"
  }
}
