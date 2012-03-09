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

import org.scalatest.{SeveredStackTraces, FunSpec}


class SpanSugarSpec extends FunSpec with SeveredStackTraces {
  
  import SpanSugar._
 
  describe("The SpanSugar trait") {
    
    it("should provide implicit conversions for Int time spans") {
      assert((1 millisecond) === Span(1, Millisecond))
      assert((2 milliseconds) === Span(2, Milliseconds))
      assert((2 millis) === Span(2, Millis))
      assert((2 seconds) === Span(2, Seconds))
      assert((1 second) === Span(1, Second))
      assert((2 seconds) === Span(2, Seconds))
      assert((1 minute) === Span(1, Minute))
      assert((2 minutes) === Span(2, Minutes))
      assert((1 hour) === Span(1, Hour))
      assert((2 hours) === Span(2, Hours))
      assert((1 day) === Span(1, Day))
      assert((2 days) === Span(2, Days))
    }
    
    it("should provide implicit conversions for Long time spans") {
      assert((1L millisecond) === Span(1, Millisecond))
      assert((2L milliseconds) === Span(2, Milliseconds))
      assert((2L millis) === Span(2, Millis))
      assert((2L seconds) === Span(2, Seconds))
      assert((1L second) === Span(1, Second))
      assert((2L seconds) === Span(2, Seconds))
      assert((1L minute) === Span(1, Minute))
      assert((2L minutes) === Span(2, Minutes))
      assert((1L hour) === Span(1, Hour))
      assert((2L hours) === Span(2, Hours))
      assert((1L day) === Span(1, Day))
      assert((2L days) === Span(2, Days))
    }
                                      // TODO: Need to add micros and nanos
    it("should provide an implicit conversion from GrainOfTime to Long") {
      def getALong(aSpan: Span) = aSpan.totalNanos
      assert(getALong(1 millisecond) === 1L * 1000 * 1000)
      assert(getALong(2 milliseconds) === 2L * 1000 * 1000)
      assert(getALong(2 millis) === 2L * 1000 * 1000)
      assert(getALong(2 seconds) === 2L * 1000 * 1000 * 1000)
      assert(getALong(1 second) === 1000L * 1000 * 1000)
      assert(getALong(2 seconds) === 2L * 1000 * 1000 * 1000)
      assert(getALong(1 minute) === 1000L * 60 * 1000 * 1000)
      assert(getALong(2 minutes) === 2L * 1000 * 60 * 1000 * 1000)
      assert(getALong(1 hour) === 1000L * 60 * 60 * 1000 * 1000)
      assert(getALong(2 hours) === 2L * 1000 * 60 * 60 * 1000 * 1000)
      assert(getALong(1 day) === 1000L * 60 * 60 * 24 * 1000 * 1000)
      assert(getALong(2 days) === 2L * 1000 * 60 * 60 * 24 * 1000 * 1000)
      assert(getALong(1L millisecond) === 1L * 1000 * 1000)
      assert(getALong(2L milliseconds) === 2L * 1000 * 1000)
      assert(getALong(2L millis) === 2L * 1000 * 1000)
      assert(getALong(2L seconds) === 2L * 1000 * 1000 * 1000)
      assert(getALong(1L second) === 1000L * 1000 * 1000)
      assert(getALong(2L seconds) === 2L * 1000 * 1000 * 1000)
      assert(getALong(1L minute) === 1000L * 60 * 1000 * 1000)
      assert(getALong(2L minutes) === 2L * 1000 * 60 * 1000 * 1000)
      assert(getALong(1L hour) === 1000L * 60 * 60 * 1000 * 1000)
      assert(getALong(2L hours) === 2L * 1000 * 60 * 60 * 1000 * 1000)
      assert(getALong(1L day) === 1000L * 60 * 60 * 24 * 1000 * 1000)
      assert(getALong(2L days) === 2L * 1000 * 60 * 60 * 24 * 1000 * 1000)
    }
  }
}
