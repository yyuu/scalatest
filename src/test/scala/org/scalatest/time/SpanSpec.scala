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
package org.scalatest.time

import org.scalatest.matchers.{HavePropertyMatchResult, HavePropertyMatcher, ShouldMatchers}
import org.scalatest.{SeveredStackTraces, FunSpec}

class SpanSpec extends FunSpec with ShouldMatchers with SeveredStackTraces {
  def millis(expectedValue: Long) =
    HavePropertyMatcher { (dura: SpanConcept) =>
      HavePropertyMatchResult(
      dura.millis == expectedValue,
      "millis",
      expectedValue,
      dura.millis
      )
    }

  def nanos(expectedValue: Int) =
    HavePropertyMatcher { (dura: SpanConcept) =>
      HavePropertyMatchResult(
        dura.nanos == expectedValue,
        "nanos",
        expectedValue,
        dura.nanos
      )
    }

  val MaxNanos = 999999
  val MaxNanosAsDouble = 999999.0

  describe("A Span") {

    it("should produce IAE if a negative length is passed") {
      for (u <- Seq(Nanosecond, Nanoseconds, Microsecond, Microseconds, Millisecond, Milliseconds, Second, Seconds,
          Minute, Minutes, Hour, Hours, Day, Days)) {
        for (i <- Seq(-1, -2, -3, Long.MinValue)) {
          withClue("u was: " + u + "; i was: " + i) {
            intercept[IllegalArgumentException] {
              Span(i, u)
            }
          }
        }
        for (d <- Seq(-1, -2, -3, -1.5, -9.98, Double.MinValue)) {
          withClue("u was: " + u + "; d was: " + d) {
            intercept[IllegalArgumentException] {
              Span(d, u)
            }
          }
        }
      }
    }

    it("should produce IAE if anything other than 1 is passed for singular units forms") {
      for (u <- Seq(Nanosecond, Microsecond, Millisecond, Second, Minute, Hour, Day)) {
        for (i <- Seq(0, 2, 3, Long.MaxValue)) {
          withClue("u was: " + u + "; i was: " + i) {
            intercept[IllegalArgumentException] {
              Span(i, u)
            }
          }
        }
        for (d <- Seq(0.0, 0.1, 1.1, 2.0, 9.98, Double.MaxValue)) {
          withClue("u was: " + u + "; d was: " + d) {
            intercept[IllegalArgumentException] {
              Span(d, u)
            }
          }
        }
      }
    }

    it("should construct with valid nanoseconds passed") {

      Span(0, Nanoseconds) should have (millis(0), nanos(0))
      Span(1, Nanosecond) should have (millis(0), nanos(1))
      Span(1, Nanoseconds) should have (millis(0), nanos(1))
      Span(2, Nanoseconds) should have (millis(0), nanos(2))
      Span(MaxNanos, Nanoseconds) should have (millis(0), nanos(MaxNanos))
      Span(MaxNanos + 1, Nanoseconds) should have (millis(1), nanos(0))
      Span(MaxNanos + 2, Nanoseconds) should have (millis(1), nanos(1))
      Span(Long.MaxValue, Nanoseconds) should have (millis(9223372036854L), nanos(775807))

      Span(0.0, Nanoseconds) should have (millis(0), nanos(0))
      Span(1.0, Nanosecond) should have (millis(0), nanos(1))
      Span(1.0, Nanoseconds) should have (millis(0), nanos(1))
      Span(2.0, Nanoseconds) should have (millis(0), nanos(2))
      Span(MaxNanosAsDouble, Nanoseconds) should have (millis(0), nanos(MaxNanos))
      Span(MaxNanosAsDouble + 1, Nanoseconds) should have (millis(1), nanos(0))
      Span(MaxNanosAsDouble + 2, Nanoseconds) should have (millis(1), nanos(1))
      Span(0.1, Nanoseconds) should have (millis(0), nanos(0))
      Span(1.1, Nanoseconds) should have (millis(0), nanos(1))
      Span(1.2, Nanoseconds) should have (millis(0), nanos(1))
      Span(1.499, Nanoseconds) should have (millis(0), nanos(1))
      Span(1.5, Nanoseconds) should have (millis(0), nanos(1))
      Span(1.9, Nanoseconds) should have (millis(0), nanos(1))
      Span(2.2, Nanoseconds) should have (millis(0), nanos(2))
      Span(Long.MaxValue.toDouble, Nanoseconds) should have (millis(9223372036854L), nanos(775808))
    }

    it("should throw IAE if a Double nanos value larger than the largest expressible amount is passed.") {
      val biggest = Long.MaxValue.toDouble
      for (d <- Seq(biggest + 1e10, biggest + 2e10, biggest + 3e10, Double.MaxValue)) {
        withClue("d was: " + d) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(d, Nanoseconds)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should construct with valid microseconds passed") {

      Span(0, Microseconds) should have (millis(0), nanos(0))
      Span(1, Microsecond) should have (millis(0), nanos(1000))
      Span(1, Microseconds) should have (millis(0), nanos(1000))
      Span(2, Microseconds) should have (millis(0), nanos(2000))
      Span(1000, Microseconds) should have (millis(1), nanos(0))
      Span(1001, Microseconds) should have (millis(1), nanos(1000))
      Span(1002, Microseconds) should have (millis(1), nanos(2000))
      Span(2000, Microseconds) should have (millis(2), nanos(0))
      Span(2001, Microseconds) should have (millis(2), nanos(1000))
      Span(2002, Microseconds) should have (millis(2), nanos(2000))
      Span(Long.MaxValue, Microseconds) should have (millis(9223372036854775L), nanos(807000))

      Span(0.0, Microseconds) should have (millis(0), nanos(0))
      Span(1.0, Microsecond) should have (millis(0), nanos(1000))
      Span(1.0, Microseconds) should have (millis(0), nanos(1000))
      Span(2.0, Microseconds) should have (millis(0), nanos(2000))
      Span(1000.0, Microseconds) should have (millis(1), nanos(0))
      Span(1001.0, Microseconds) should have (millis(1), nanos(1000))
      Span(1002.0, Microseconds) should have (millis(1), nanos(2000))
      Span(2000.0, Microseconds) should have (millis(2), nanos(0))
      Span(2001.0, Microseconds) should have (millis(2), nanos(1000))
      Span(2002.0, Microseconds) should have (millis(2), nanos(2000))
      Span(0.1, Microseconds) should have (millis(0), nanos(100))
      Span(1.1, Microseconds) should have (millis(0), nanos(1100))
      Span(1.2, Microseconds) should have (millis(0), nanos(1200))
      Span(1.499, Microseconds) should have (millis(0), nanos(1499))
      Span(1.5, Microseconds) should have (millis(0), nanos(1500))
      Span(1.9, Microseconds) should have (millis(0), nanos(1900))
      Span(2.2, Microseconds) should have (millis(0), nanos(2200))
      Span(Long.MaxValue.toDouble, Microseconds) should have (millis(9223372036854776L), nanos(808000))
    }

    it("should construct with valid milliseconds passed") {

      Span(0, Milliseconds) should have (millis(0), nanos(0))
      Span(0, Millis) should have (millis(0), nanos(0))
      Span(1, Millisecond) should have (millis(1), nanos(0))
      Span(1, Milliseconds) should have (millis(1), nanos(0))
      Span(2, Milliseconds) should have (millis(2), nanos(0))
      Span(2, Millis) should have (millis(2), nanos(0))
      Span(1000, Milliseconds) should have (millis(1000), nanos(0))
      Span(1001, Milliseconds) should have (millis(1001), nanos(0))
      Span(1002, Milliseconds) should have (millis(1002), nanos(0))
      Span(2000, Milliseconds) should have (millis(2000), nanos(0))
      Span(2001, Milliseconds) should have (millis(2001), nanos(0))
      Span(2002, Milliseconds) should have (millis(2002), nanos(0))
      Span(Long.MaxValue, Milliseconds) should have (millis(Long.MaxValue), nanos(0))
     // TODO: Repeat tests for Millis as well as Milliseconds
      Span(0.0, Milliseconds) should have (millis(0), nanos(0))
      Span(0.0, Millis) should have (millis(0), nanos(0))
      Span(1.0, Millisecond) should have (millis(1), nanos(0))
      Span(1.0, Milliseconds) should have (millis(1), nanos(0))
      Span(2.0, Milliseconds) should have (millis(2), nanos(0))
      Span(2.0, Millis) should have (millis(2), nanos(0))
      Span(1000.0, Milliseconds) should have (millis(1000), nanos(0))
      Span(1001.0, Milliseconds) should have (millis(1001), nanos(0))
      Span(1002.0, Milliseconds) should have (millis(1002), nanos(0))
      Span(2000.0, Milliseconds) should have (millis(2000), nanos(0))
      Span(2001.0, Milliseconds) should have (millis(2001), nanos(0))
      Span(2002.0, Milliseconds) should have (millis(2002), nanos(0))
      Span(0.1, Milliseconds) should have (millis(0), nanos(100000))
      Span(1.1, Milliseconds) should have (millis(1), nanos(100000))
      Span(1.2, Milliseconds) should have (millis(1), nanos(199999))
      Span(1.499, Milliseconds) should have (millis(1), nanos(499000))
      Span(1.5, Milliseconds) should have (millis(1), nanos(500000))
      Span(1.9, Milliseconds) should have (millis(1), nanos(899999))
      Span(2.2, Milliseconds) should have (millis(2), nanos(200000))
      Span(Long.MaxValue.toDouble, Milliseconds) should have (millis(Long.MaxValue), nanos(0))
    }
                // TODO: Write one for the max number of seconds
    it("should construct with valid seconds passed") {

      Span(0, Seconds) should have (millis(0), nanos(0))
      Span(1, Second) should have (millis(1000), nanos(0))
      Span(1, Seconds) should have (millis(1000), nanos(0))
      Span(2, Seconds) should have (millis(2000), nanos(0))
      Span(1000, Seconds) should have (millis(1000000), nanos(0))
      Span(1001, Seconds) should have (millis(1001000), nanos(0))
      Span(1002, Seconds) should have (millis(1002000), nanos(0))
      Span(2000, Seconds) should have (millis(2000000), nanos(0))
      Span(2001, Seconds) should have (millis(2001000), nanos(0))
      Span(2002, Seconds) should have (millis(2002000), nanos(0))

      Span(0.0, Seconds) should have (millis(0), nanos(0))
      Span(1.0, Second) should have (millis(1000), nanos(0))
      Span(1.0, Seconds) should have (millis(1000), nanos(0))
      Span(2.0, Seconds) should have (millis(2000), nanos(0))
      Span(1000.0, Seconds) should have (millis(1000000), nanos(0))
      Span(1001.0, Seconds) should have (millis(1001000), nanos(0))
      Span(1002.0, Seconds) should have (millis(1002000), nanos(0))
      Span(2000.0, Seconds) should have (millis(2000000), nanos(0))
      Span(2001.0, Seconds) should have (millis(2001000), nanos(0))
      Span(2002.0, Seconds) should have (millis(2002000), nanos(0))
      Span(0.1, Seconds) should have (millis(100), nanos(0))
      Span(1.1, Seconds) should have (millis(1100), nanos(0))
      Span(1.2, Seconds) should have (millis(1200), nanos(0))
      Span(1.499, Seconds) should have (millis(1499), nanos(0))
      Span(1.5, Seconds) should have (millis(1500), nanos(0))
      Span(1.9, Seconds) should have (millis(1900), nanos(0))
      Span(2.2, Seconds) should have (millis(2200), nanos(0))
      Span(0.001, Seconds) should have (millis(1), nanos(0))
      Span(88.0001, Seconds) should have (millis(88000), nanos(100000))
      Span(88.000001, Seconds) should have (millis(88000), nanos(1000))
      Span(88.000000001, Seconds) should have (millis(88000), nanos(1))
    }

    it("should throw IAE if a seconds value larger than the largest expressible amount is passed.") {
      val biggest = Long.MaxValue / 1000
      for (i <- Seq(biggest + 1, biggest + 2, biggest + 3, Long.MaxValue)) {
        withClue("i was: " + i) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(i, Seconds)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should throw IAE if a Double seconds value larger than the largest expressible amount is passed.") {
      val biggest = (Long.MaxValue / 1000).toDouble
      for (d <- Seq(biggest + 1e10, biggest + 2e10, biggest + 3e10, Double.MaxValue)) {
        withClue("d was: " + d) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(d, Seconds)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should construct with valid minutes passed") {

      Span(0, Minutes) should have (millis(0), nanos(0))
      Span(1, Minute) should have (millis(1000 * 60), nanos(0))
      Span(1, Minutes) should have (millis(1000 * 60), nanos(0))
      Span(2, Minutes) should have (millis(2 * 1000 * 60), nanos(0))
      Span(1000, Minutes) should have (millis(1000 * 1000 * 60), nanos(0))
      Span(1001, Minutes) should have (millis(1001 * 1000 * 60), nanos(0))
      Span(1002, Minutes) should have (millis(1002 * 1000 * 60), nanos(0))
      Span(2000, Minutes) should have (millis(2000 * 1000 * 60), nanos(0))
      Span(2001, Minutes) should have (millis(2001 * 1000 * 60), nanos(0))
      Span(2002, Minutes) should have (millis(2002 * 1000 * 60), nanos(0))

      Span(0.0, Minutes) should have (millis(0), nanos(0))
      Span(1.0, Minute) should have (millis(1000 * 60), nanos(0))
      Span(1.0, Minutes) should have (millis(1000 * 60), nanos(0))
      Span(2.0, Minutes) should have (millis(2 * 1000 * 60), nanos(0))
      Span(1000.0, Minutes) should have (millis(1000 * 1000 * 60), nanos(0))
      Span(1001.0, Minutes) should have (millis(1001 * 1000 * 60), nanos(0))
      Span(1002.0, Minutes) should have (millis(1002 * 1000 * 60), nanos(0))
      Span(2000.0, Minutes) should have (millis(2000 * 1000 * 60), nanos(0))
      Span(2001.0, Minutes) should have (millis(2001 * 1000 * 60), nanos(0))
      Span(2002.0, Minutes) should have (millis(2002 * 1000 * 60), nanos(0))
      Span(0.1, Minutes) should have (millis(100 * 60), nanos(0))
      Span(1.1, Minutes) should have (millis(1100 * 60), nanos(0))
      Span(1.2, Minutes) should have (millis(1200 * 60), nanos(0))
      Span(1.499, Minutes) should have (millis(1499 * 60), nanos(0))
      Span(1.5, Minutes) should have (millis(1500 * 60), nanos(0))
      Span(1.9, Minutes) should have (millis(1900 * 60), nanos(0))
      Span(2.2, Minutes) should have (millis(2200 * 60), nanos(0))
      Span(0.001, Minutes) should have (millis(60), nanos(0))
      Span(88.0001, Minutes) should have (millis(5280006), nanos(0))
      Span(88.000001, Minutes) should have (millis(5280000), nanos(60000))
      Span(88.000000001, Minutes) should have (millis(5280000), nanos(60))
    }

    it("should throw IAE if a minutes value larger than the largest expressible amount is passed.") {
      val biggest = Long.MaxValue / 1000 / 60
      for (i <- Seq(biggest + 1, biggest + 2, biggest + 3, Long.MaxValue)) {
        withClue("i was: " + i) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(i, Minutes)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should throw IAE if a Double minutes value larger than the largest expressible amount is passed.") {
      val biggest = (Long.MaxValue / 1000 / 60).toDouble
      for (d <- Seq(biggest + 1, biggest + 2, biggest + 3, Double.MaxValue)) {
        withClue("d was: " + d) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(d, Minutes)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should construct with valid hours passed") {

      Span(0, Hours) should have (millis(0), nanos(0))
      Span(1, Hour) should have (millis(1000 * 60 * 60), nanos(0))
      Span(1, Hours) should have (millis(1000 * 60 * 60), nanos(0))
      Span(2, Hours) should have (millis(2 * 1000 * 60 * 60), nanos(0))
      Span(1000, Hours) should have (millis(1000L * 1000 * 60 * 60), nanos(0))
      Span(1001, Hours) should have (millis(1001L * 1000 * 60 * 60), nanos(0))
      Span(1002, Hours) should have (millis(1002L * 1000 * 60 * 60), nanos(0))
      Span(2000, Hours) should have (millis(2000L * 1000 * 60 * 60), nanos(0))
      Span(2001, Hours) should have (millis(2001L * 1000 * 60 * 60), nanos(0))
      Span(2002, Hours) should have (millis(2002L * 1000 * 60 * 60), nanos(0))

      Span(0.0, Hours) should have (millis(0), nanos(0))
      Span(1.0, Hour) should have (millis(1000 * 60 * 60), nanos(0))
      Span(1.0, Hours) should have (millis(1000 * 60 * 60), nanos(0))
      Span(2.0, Hours) should have (millis(2 * 1000 * 60 * 60), nanos(0))
      Span(1000.0, Hours) should have (millis(1000L * 1000 * 60 * 60), nanos(0))
      Span(1001.0, Hours) should have (millis(1001L * 1000 * 60 * 60), nanos(0))
      Span(1002.0, Hours) should have (millis(1002L * 1000 * 60 * 60), nanos(0))
      Span(2000.0, Hours) should have (millis(2000L * 1000 * 60 * 60), nanos(0))
      Span(2001.0, Hours) should have (millis(2001L * 1000 * 60 * 60), nanos(0))
      Span(2002.0, Hours) should have (millis(2002L * 1000 * 60 * 60), nanos(0))
      Span(0.1, Hours) should have (millis(100 * 60 * 60), nanos(0))
      Span(1.1, Hours) should have (millis(1100 * 60 * 60), nanos(0))
      Span(1.2, Hours) should have (millis(1200 * 60 * 60), nanos(0))
      Span(1.499, Hours) should have (millis(1499 * 60 * 60), nanos(0))
      Span(1.5, Hours) should have (millis(1500 * 60 * 60), nanos(0))
      Span(1.9, Hours) should have (millis(1900 * 60 * 60), nanos(0))
      Span(2.2, Hours) should have (millis(2200 * 60 * 60), nanos(0))
      Span(0.001, Hours) should have (millis(60 * 60), nanos(0))
      Span(88.0001, Hours) should have (millis(5280006 * 60), nanos(0))
      Span(88.000001, Hours) should have (millis(316800003), nanos(600000))
      Span(88.000000001, Hours) should have (millis(5280000 * 60), nanos(3600))
    }

    it("should throw IAE if an hours value larger than the largest expressible amount is passed.") {
      val biggest = Long.MaxValue / 1000 / 60 / 60
      for (i <- Seq(biggest + 1, biggest + 2, biggest + 3, Long.MaxValue)) {
        withClue("i was: " + i) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(i, Hours)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should throw IAE if a Double hours value larger than the largest expressible amount is passed.") {
      val biggest = (Long.MaxValue / 1000 / 60 / 60).toDouble
      for (d <- Seq(biggest + 1, biggest + 2, biggest + 3, Double.MaxValue)) {
        withClue("d was: " + d) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(d, Hours)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should construct with valid days passed") {

      Span(0, Days) should have (millis(0), nanos(0))
      Span(1, Day) should have (millis(1000 * 60 * 60 * 24), nanos(0))
      Span(1, Days) should have (millis(1000 * 60 * 60 * 24), nanos(0))
      Span(2, Days) should have (millis(2 * 1000 * 60 * 60 * 24), nanos(0))
      Span(1000, Days) should have (millis(1000L * 1000 * 60 * 60 * 24), nanos(0))
      Span(1001, Days) should have (millis(1001L * 1000 * 60 * 60 * 24), nanos(0))
      Span(1002, Days) should have (millis(1002L * 1000 * 60 * 60 * 24), nanos(0))
      Span(2000, Days) should have (millis(2000L * 1000 * 60 * 60 * 24), nanos(0))
      Span(2001, Days) should have (millis(2001L * 1000 * 60 * 60 * 24), nanos(0))
      Span(2002, Days) should have (millis(2002L * 1000 * 60 * 60 * 24), nanos(0))

      Span(0.0, Days) should have (millis(0), nanos(0))
      Span(1.0, Day) should have (millis(1000 * 60 * 60 * 24), nanos(0))
      Span(1.0, Days) should have (millis(1000 * 60 * 60 * 24), nanos(0))
      Span(2.0, Days) should have (millis(2 * 1000 * 60 * 60 * 24), nanos(0))
      Span(1000.0, Days) should have (millis(1000L * 1000 * 60 * 60 * 24), nanos(0))
      Span(1001.0, Days) should have (millis(1001L * 1000 * 60 * 60 * 24), nanos(0))
      Span(1002.0, Days) should have (millis(1002L * 1000 * 60 * 60 * 24), nanos(0))
      Span(2000.0, Days) should have (millis(2000L * 1000 * 60 * 60 * 24), nanos(0))
      Span(2001.0, Days) should have (millis(2001L * 1000 * 60 * 60 * 24), nanos(0))
      Span(2002.0, Days) should have (millis(2002L * 1000 * 60 * 60 * 24), nanos(0))
      Span(0.1, Days) should have (millis(100 * 60 * 60 * 24), nanos(0))
      Span(1.1, Days) should have (millis(1100 * 60 * 60 * 24), nanos(0))
      Span(1.2, Days) should have (millis(1200 * 60 * 60 * 24), nanos(0))
      Span(1.499, Days) should have (millis(1499 * 60 * 60 * 24), nanos(0))
      Span(1.5, Days) should have (millis(1500 * 60 * 60 * 24), nanos(0))
      Span(1.9, Days) should have (millis(1900 * 60 * 60 * 24), nanos(0))
      Span(2.2, Days) should have (millis(2200 * 60 * 60 * 24), nanos(0))
      Span(0.001, Days) should have (millis(60 * 60 * 24), nanos(0))
      Span(88.0001, Days) should have (millis(5280006L * 60 * 24), nanos(0))
      Span(88.000001, Days) should have (millis(7603200086L), nanos(400000))
      Span(88.000000001, Days) should have (millis(5280000L * 60 * 24), nanos(86400))
    }

    it("should throw IAE if a days value larger than the largest expressible amount is passed.") {
      val biggest = Long.MaxValue / 1000 / 60 / 60 / 24
      for (i <- Seq(biggest + 1, biggest + 2, biggest + 3, Long.MaxValue)) {
        withClue("i was: " + i) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(i, Days)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }

    it("should throw IAE if a Double days value larger than the largest expressible amount is passed.") {
      val biggest = (Long.MaxValue / 1000 / 60 / 60 / 24).toDouble
      for (d <- Seq(biggest + 1, biggest + 2, biggest + 3, Double.MaxValue)) {
        withClue("d was: " + d) {
          val caught =
            intercept[IllegalArgumentException] {
              Span(d, Days)
            }
          caught.getMessage should include ("Passed length")
        }
      }
    }
  }
}
