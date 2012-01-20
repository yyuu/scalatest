package org.scalatest.path

import org.scalatest.PrivateMethodTester
import org.scalatest.matchers.ShouldMatchers

class FunSpecSpec extends org.scalatest.FunSpec with ShouldMatchers with PrivateMethodTester {

  describe("FunSpec ThreadLocal variable") {
    it("should be set by setPath and clear by getPath") {
      val setPath = PrivateMethod[Unit]('setPath)
      val getPath = PrivateMethod[Option[List[Int]]]('getPath)
      
      FunSpec invokePrivate setPath(List(1, 2, 3))
      FunSpec invokePrivate getPath() should be (Some(List(1, 2, 3)))
      FunSpec invokePrivate getPath() should be (None)
    }
  }
  
}