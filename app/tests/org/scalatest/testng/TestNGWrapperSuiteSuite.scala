import org.scalatest.testng._
import org.scalatest.jmock.SMocker
import org.scalatest.jmock.SMockFunSuite
import java.io.File
import org.apache.commons.io.FileUtils

package org.scalatest.testng{

  class TestNGWrapperSuiteSuite extends SMockFunSuite{
  
    val simpleSuite = 
      <suite name="Simple Suite">
        <test verbose="10" name="org.scalatest.testng.test" annotations="JDK">
          <classes>
            <class name="org.scalatest.testng.test.LegacySuite"/>
          </classes>
        </test>
      </suite>

    mockTest("wrapper suite properly notifies reporter when tests start, and pass"){
    
      val tmp = File.createTempFile( "testng", "wrapper" )
      FileUtils.writeStringToFile( tmp, simpleSuite.toString )
    
      val reporter = mock(classOf[Reporter])

      expecting { 
        one(reporter).testStarting(any(classOf[Report])) 
        one(reporter).testSucceeded(any(classOf[Report])) 
        one(reporter).testStarting(any(classOf[Report])) 
        one(reporter).testSucceeded(any(classOf[Report])) 
      }
    
      val wrapperSuite = new TestNGWrapperSuite(List(tmp.getAbsolutePath))
      wrapperSuite.runTestNG(reporter)
    }
  }
  
  package test{
    import org.testng.annotations._
  
    class LegacySuite extends TestNGSuite {
      @Test def testThatPasses() {}
      @Test def testThatPasses2() {}
    }
  }

}
