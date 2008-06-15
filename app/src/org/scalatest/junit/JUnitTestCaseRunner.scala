package org.scalatest.junit

import org.scalatest.Suite
import org.scalatest.Report

import _root_.junit.framework._
import _root_.junit.textui._

trait JUnitTestCaseRunner {

  def testNames: Set[String]
  
  def runAllTests( testcase: TestCase, reporter: Reporter ){
    reporter.suiteStarting( buildReport( this.getClass.getName, None ) ) 
    testNames.foreach(runSingleTest( testcase, _, reporter))
    reporter.suiteCompleted( buildReport( this.getClass.getName, None ) )
  }
  
  def runSingleTest( testcase: TestCase, testName: String, reporter: Reporter ) = {
    testcase.setName(testName)
    testcase.run(new MyTestResult(reporter))
  }
   
  private def buildReport(testName: String, t: Option[Throwable]): Report = {
    new Report(testName, this.getClass.getName, t, None)
  }
  
  private def buildReport(test: Test, t: Option[Throwable]): Report = {
    buildReport(JUnitVersionHelper.getTestCaseName(test), t)
  }
  
  private class MyTestResult(reporter: Reporter) extends TestResult {

    override def addFailure(test: Test, t: AssertionFailedError) = {
      super.addFailure(test, t)
      reporter.testFailed(buildReport(test, Some(t))) 
    }

    override def addError(test: Test, t: Throwable) = {
      super.addError(test, t)
      reporter.testFailed(buildReport(test, Some(t)))
    }
    
    override def startTest(test: Test) = {
      super.startTest(test)
      reporter.testStarting(buildReport(test, None))
    }
    
    override def endTest(test: Test) = {
      super.endTest(test)
      if (this.wasSuccessful) reporter.testSucceeded(buildReport(test, None)) 
    }
  } 
}
