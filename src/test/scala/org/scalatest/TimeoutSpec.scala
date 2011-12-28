package org.scalatest

import matchers.ShouldMatchers
import ValueOnOption._
import Timeout._
import SharedHelpers.thisLineNumber

class TimeoutSpec extends Spec with ShouldMatchers {

  describe("The failAfter construct") {
    
    it("should blow up with TestFailedException when timeout") {
      val caught = evaluating {
        failAfter(3000) {
          Thread.sleep(6000)
        }
      } should produce [TestFailedException]
      caught.message.value should be (Resources("timeoutFailAfter", "3000"))
      caught.failedCodeLineNumber.value should equal (thisLineNumber - 5)
      caught.failedCodeFileName.value should be ("TimeoutSpec.scala")
    }
    
    it("should pass normally when timeout is not reached") {
      failAfter(6000) {
        Thread.sleep(3000)
      }
    }
  }
  
}