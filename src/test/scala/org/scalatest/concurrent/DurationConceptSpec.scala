package org.scalatest.concurrent

import org.scalatest.FunSpec

class DurationConceptSpec extends FunSpec {
  describe("A DurationConcept") {
    it("should construct fine if non-negative, in-range values are passed for millis and nanos") {
      new DurationConcept(0) {}  // These should not throw an exception
      new DurationConcept(0, 0) {}
      new DurationConcept(1, 1) {}
      new DurationConcept(1, 999999) {}
      new DurationConcept(Integer.MAX_VALUE) {}
      new DurationConcept(Integer.MAX_VALUE, 999999) {}
    }
    it("should throw IAE if a negative value is passed for millis") {
      intercept[IllegalArgumentException] {
        new DurationConcept(-1) {}
      }
      intercept[IllegalArgumentException] {
        new DurationConcept(-1, 1) {}
      }
    }
    it("should throw IAE if a negative value is passed for nanos") {
      intercept[IllegalArgumentException] {
        new DurationConcept(1, -1) {}
      }
    }
    it("should throw IAE if an out-of-range positive value is passed for nanos") {
      intercept[IllegalArgumentException] {
        new DurationConcept(1, 1000000) {}
      }
    }
  }
}
