package org.scalatest.testng.example;

import org.testng.annotations.Test
import org.testng.annotations.BeforeMethod
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeSuite
import org.testng.annotations.AfterMethod
import org.testng.annotations.AfterClass
import org.testng.annotations.AfterSuite
import org.scalatest.Report
import org.scalatest.Reporter
import org.specs.mock._

class ExampleTestNGSuite extends TestNGSuite with JMocker{

  @AfterSuite
  def failAfterSuite(){ throw new Exception("fail in before method") }

  @BeforeMethod def passBeforeMethod(){}
  @BeforeClass def passBeforeClass(){}
  @BeforeSuite def passBeforeSuite(){}
  
  @AfterMethod def passAfterMethod(){}
  @AfterClass def passAfterClass(){}
  @AfterSuite def passAfterSuite(){}
  
  @Test{val invocationCount=10} def thisTestRunsTenTimes = {}
  
  @Test{val groups=Array("runMe")} 
  def testWithException(){ 
    throw new Exception("exception!!!") 
   }
  
  @Test{val groups=Array("runMe")} def testWithAssertFail = assert( 1 === 2, "assert fail!!!" )
  
  @Test{val dependsOnMethods=Array("testWithException")} def testToGetSkipped = {}

  @Test
  def testWithMocking = {
    //val mockReporter = mock(classOf[Reporter])
    //expect {
    //  one(mockReporter).testSucceeded(any(classOf[Report]))
    //}
    //mockReporter.testSucceeded( new Report( "test", "test" ) )
  }
  
}

