package org.scalatest.testng;

import org.testng.annotations.Test

class ActualTestNGSuite extends TestNGSuite{

  @Test{val groups=Array("runMe")} def testWithException() {
    //throw new Exception("exception!!!")
  }
  
  @Test{val groups=Array("runMe")} def testWithAssertFail() {
    //assert( 1 === 2, "assert fail!!!" )
  }
  
  @Test{val dependsOnMethods=Array("testWithException")}
  def testToGetSkipped() = {}
  
}

