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


class SpanConceptSpec extends FunSpec with SeveredStackTraces {

  val MaxNanos = 999999

  describe("A SpanConcept") {
    it("should construct fine if non-negative, in-range values are passed for millis and nanos") {
      new SpanConcept(0) {}  // These should not throw an exception
      new SpanConcept(0, 0) {}
      new SpanConcept(1, 1) {}
      new SpanConcept(1, 999999) {}
      new SpanConcept(Int.MaxValue) {}
      new SpanConcept(Int.MaxValue, MaxNanos) {}
    }
    it("should throw IAE if a negative value is passed for millis") {
      intercept[IllegalArgumentException] {
        new SpanConcept(-1) {}
      }
      intercept[IllegalArgumentException] {
        new SpanConcept(-1, 1) {}
      }
    }
    it("should throw IAE if a negative value is passed for nanos") {
      intercept[IllegalArgumentException] {
        new SpanConcept(1, -1) {}
      }
    }
    it("should throw IAE if an out-of-range positive value is passed for nanos") {
      intercept[IllegalArgumentException] {
        new SpanConcept(1, 1000000) {}
      }
    }
    describe("when constructed with Long.MaxValue millis") {
      it("should throw IAE when a non-zero nanos is passed") {
        // These should not throw an exception:
        new SpanConcept(Long.MaxValue) {}
        new SpanConcept(Long.MaxValue, 0) {}

        // But these should throw IAE
        intercept[IllegalArgumentException] {
          new SpanConcept(Long.MaxValue, -1) {}
        }
        intercept[IllegalArgumentException] {
          new SpanConcept(Long.MaxValue, 1) {}
        }
        intercept[IllegalArgumentException] {
          new SpanConcept(Long.MaxValue, MaxNanos) {}
        }
      }
    }
  }
}
