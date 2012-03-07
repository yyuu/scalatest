/*
 * Copyright 2001-2012 Artima, Inc.
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
package org.scalatest.concurrent

import org.scalatest.FunSpec
import org.scalatest.matchers.{HavePropertyMatchResult, HavePropertyMatcher, ShouldMatchers}

class DurationsSpec extends FunSpec with ShouldMatchers {
  def millis(expectedValue: Long) =
    HavePropertyMatcher { (dura: DurationConcept) =>
      HavePropertyMatchResult(
      dura.millis == expectedValue,
      "millis",
      expectedValue,
      dura.millis
      )
    }
  def nanos(expectedValue: Int) =
    HavePropertyMatcher { (dura: DurationConcept) =>
      HavePropertyMatchResult(
        dura.nanos == expectedValue,
        "nanos",
        expectedValue,
        dura.nanos
      )
    }

  describe("A Duration") {
    import Durations._
    it("should construct with valid nanoseconds passed") {
      Duration(1, Nanosecond) should have (millis(0), nanos(1))
      Duration(2, Nanoseconds) should have (millis(0), nanos(2))
    }
  }
}
