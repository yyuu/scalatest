package org.scalatest.concurrent

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class WhenReadySpec extends FunSpec with ShouldMatchers {

  describe("A FutureSoBright") {
  /*  
    class Now(val isExpired: Boolean, val isDropped: Boolean) extends FutureSoBright[String] {
      def value: Option[Either[Throwable,String]] = Some(Right("hello"))
    }
    
    it("should have a way to give back the value") {
      val now = new Now(true, true)
      now.value should equal (Some(Right("hello")))
    }
*/
    it("should have a way to determine if the thing was canceled") (pending)
    it("should have a way to determine if the thing had timed out") (pending)
    it("needs have a way to determine if the future has 'arrived'") (pending)
  }
}