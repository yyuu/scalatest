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

import org.scalatest.matchers.{HavePropertyMatchResult, HavePropertyMatcher, ShouldMatchers}
import org.scalatest.{SeveredStackTraces, FunSpec}

class DurationsSpec extends FunSpec with ShouldMatchers with SeveredStackTraces {
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
  val MaxNanos = 999999

  describe("A Duration") {

    import Durations._
    it("should construct with valid nanoseconds passed") {
      Duration(0, Nanosecond) should have (millis(0), nanos(0))
      Duration(0, Nanoseconds) should have (millis(0), nanos(0))
      Duration(1, Nanosecond) should have (millis(0), nanos(1))
      Duration(2, Nanoseconds) should have (millis(0), nanos(2))
      Duration(MaxNanos, Nanoseconds) should have (millis(0), nanos(MaxNanos))
      Duration(MaxNanos + 1, Nanoseconds) should have (millis(1), nanos(0))
      Duration(MaxNanos + 2, Nanoseconds) should have (millis(1), nanos(1))
      Duration(Long.MaxValue, Nanoseconds) should have (millis(9223372036854L), nanos(775807))
    }

    it("should produce IAE if a negative nanoseconds is passed") {
      intercept[IllegalArgumentException] {
        Duration(-1, Nanosecond)
      }
      intercept[IllegalArgumentException] {
        Duration(-1, Nanoseconds)
      }
      intercept[IllegalArgumentException] {
        Duration(Long.MinValue, Nanosecond)
      }
      intercept[IllegalArgumentException] {
        Duration(Long.MinValue, Nanoseconds)
      }
    }

    it("should construct with valid microseconds passed") {
      Duration(0, Microsecond) should have (millis(0), nanos(0))
      Duration(0, Microseconds) should have (millis(0), nanos(0))
      Duration(1, Microsecond) should have (millis(0), nanos(1000))
      Duration(2, Microseconds) should have (millis(0), nanos(2000))
      Duration(1000, Microseconds) should have (millis(1), nanos(0))
      Duration(1001, Microseconds) should have (millis(1), nanos(1000))
      Duration(1002, Microseconds) should have (millis(1), nanos(2000))
      Duration(2000, Microseconds) should have (millis(2), nanos(0))
      Duration(2001, Microseconds) should have (millis(2), nanos(1000))
      Duration(2002, Microseconds) should have (millis(2), nanos(2000))
      Duration(Long.MaxValue, Microseconds) should have (millis(9223372036854775L), nanos(807000))
    }

    it("should produce IAE if a negative microseconds is passed") {
      intercept[IllegalArgumentException] {
        Duration(-1, Microsecond)
      }
      intercept[IllegalArgumentException] {
        Duration(-1, Microseconds)
      }
      intercept[IllegalArgumentException] {
        Duration(Long.MinValue, Microsecond)
      }
      intercept[IllegalArgumentException] {
        Duration(Long.MinValue, Microseconds)
      }
    }

  }
}
