package org.scalatest.junit

import java.net.URL
import java.net.MalformedURLException
import java.net.URLClassLoader

import org.scalatest.Suite
import org.scalatest.Report

import _root_.junit.framework._
import _root_.junit.textui._


class JUnit3WrapperSuite (testCaseClassNames: List[String]) extends Suite with JUnitTestCaseRunner{

  println(testCaseClassNames)
  
  override def execute(testName: Option[String], reporter: Reporter, stopper: Stopper, includes: Set[String], 
      excludes: Set[String], properties: Map[String, Any], distributor: Option[Distributor]) {
    
    getTestCases.foreach( runAllTests( _ , reporter ))
  }
  
 
  private def getTestCases: List[TestCase] = List()
}
