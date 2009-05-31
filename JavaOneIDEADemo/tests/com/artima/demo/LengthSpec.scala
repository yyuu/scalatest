package com.artima.demo

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by IntelliJ IDEA.
 * User: bv
 * Date: Dec 10, 2008
 * Time: 4:43:48 AM
 * To change this template use File | Settings | File Templates.
 */

class LengthSpec extends Spec with ShouldMatchers {

  describe("The '<obj> should have length <N>' syntax") {

    it("should invoke length on a string") {
      "hello" should have length (5)
    }

    it("should invoke length on an array") {
      Array(1, 2, 3) should have length (3)
    }

    it("should invoke length on a document") {
      import javax.swing.text.PlainDocument
      val doc = new PlainDocument
      doc should have length (0)
    }

    case class SpaceFlight(val length: Int)

    it("should access the length field on a SpaceFlight") {
      (new SpaceFlight(99)) should have length (99)
    }

    object GoldenGateBridge {  
      def getLength() = 2737 // meters
    }

    it("should invoke getLength on a GoldenGateBridge") {
      GoldenGateBridge should have length (2737)
    }
  }
}