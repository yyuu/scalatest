package org.scalatest.testng;

import org.scalatest.Suite
import org.scalatest.Report
import org.scalatest.TestRerunner

import org.testng.internal.annotations.ITest
import org.testng.internal.annotations.IAnnotationTransformer
import org.testng.TestNG
import org.testng.ITestResult
import org.testng.TestListenerAdapter
import java.lang.reflect.Method
import java.lang.reflect.Constructor


class TestNGWrapperSuite(suiteXMLFilePaths: List[String]) extends TestNGSuite{
  
  override def execute(testName: Option[String], reporter: Reporter, stopper: Stopper, includes: Set[String], 
      excludes: Set[String], properties: Map[String, Any], distributor: Option[Distributor]) {
    
    runTestNG(reporter, includes, excludes);
  }
  
  
  private[testng] def runTestNG(reporter: Reporter, groupsToInclude: Set[String], 
      groupsToExclude: Set[String]) : TestListenerAdapter = {
    
    val testng = new TestNG()
    handleGroups( groupsToInclude, groupsToExclude, testng )
    
    val files = new java.util.ArrayList
    suiteXMLFilePaths.foreach( { files add _ } )
    testng.setTestSuites(files)
    
    run( testng, reporter )
  }
  
}
