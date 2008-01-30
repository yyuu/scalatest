package org.scalatest.testng;

import org.scalatest.Suite
import org.testng.TestNG
import org.testng.TestListenerAdapter
import org.testng.ITestResult

trait TestNGSuite extends Suite{

  override def execute(testName: Option[String], reporter: Reporter, stopper: Stopper, includes: Set[String], excludes: Set[String],
      properties: Map[String, Any], distributor: Option[Distributor]) {
    
    runTestNG(reporter, includes);
    //super.execute(testName, reporter, stopper, includes, excludes, properties, distributor)
  }
  
  private class MyTestListenerAdapter( reporter: Reporter ) extends TestListenerAdapter{
    
    val className = TestNGSuite.this.getClass.getName
    
    override def onTestStart(result: ITestResult) = {
      reporter.testStarting( buildReport( result, None ))
    }
    
    override def onTestSuccess(itr: ITestResult) = {
      val report = buildReport( itr, None )
      reporter.testSucceeded( report )
    }
    
    override def onTestFailure(itr: ITestResult) = {
      reporter.testFailed( buildReport( itr, Some(itr.getThrowable)))
    }
    
    private def buildReport( itr: ITestResult, t: Option[Throwable] ): Report = {
      new Report(className + "." + itr.getName, className, t, None )
    }
  }

  private[testng] def runTestNG(reporter: Reporter) : TestListenerAdapter = runTestNG( reporter, Set() )
  
  private[testng] def runTestNG(reporter: Reporter, includes: Set[String]) : TestListenerAdapter = {
    val tla = new MyTestListenerAdapter(reporter)
    val testng = new TestNG()
    testng.setTestClasses(Array(this.getClass))
    testng.setGroups(includes.foldLeft(""){_+_})
    testng.addListener(tla)
    testng.run()
    tla
  }
  
}
