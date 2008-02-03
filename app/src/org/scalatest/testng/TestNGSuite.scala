package org.scalatest.testng;

import org.scalatest.Suite
import org.scalatest.Report
import org.scalatest.TestRerunner
import org.testng.TestNG
import org.testng.TestListenerAdapter
import org.testng.ITestResult

trait TestNGSuite extends Suite{

  override def execute(testName: Option[String], reporter: Reporter, stopper: Stopper, includes: Set[String], excludes: Set[String],
      properties: Map[String, Any], distributor: Option[Distributor]) {
    
    runTestNG(reporter, includes, excludes);
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
    
    override def onTestSkipped(itr: ITestResult) = {
      reporter.testIgnored( buildReport( itr, Some(itr.getThrowable)))
    }
    
    private def buildReport( itr: ITestResult, t: Option[Throwable] ): Report = {
      val testName = className + "." + itr.getName
      new Report(testName, className, t, Some(new TestRerunner(className, testName)) )
    }
  }

  private[testng] def runTestNG(reporter: Reporter) : TestListenerAdapter = runTestNG( reporter, Set(), Set() )
  
  private[testng] def runTestNG(reporter: Reporter, groupsToInclude: Set[String], 
      groupsToExclude: Set[String]) : TestListenerAdapter = {
    
    val tla = new MyTestListenerAdapter(reporter)
    val testng = new TestNG()
    testng.setTestClasses(Array(this.getClass))
    testng.setGroups(groupsToInclude.foldLeft(""){_+","+_})
    testng.setExcludedGroups(groupsToExclude.foldLeft(""){_+","+_})
    testng.addListener(tla)
    testng.run()
    tla
  }
  
  
  
  
  /**
     TODO
    --- done all but error message (12:02:05 AM) bvenners@mac.com: onTestSkipped -> I think maybe testIgnored
    ---(12:02:12 AM) bvenners@mac.com: but skip means skipped because a dependency failed i think
    (12:02:27 AM) bvenners@mac.com: onTestFailedButWithinSuccessPercentage(ITestResult tr) 
    (12:02:34 AM) bvenners@mac.com: maybe a testSucceeded with some extra info in the report
    (12:02:49 AM) bvenners@mac.com: onStart and onFinish are starting and finishing what, a run?
    (12:02:57 AM) bvenners@mac.com: if so then runStarting and runCompleted
    (12:03:14 AM) bvenners@mac.com: onConfiguration/Success/Failure we don't have, so put that in an infoProvided
    (12:03:29 AM) joshcoughx: ok
    (12:03:50 AM) bvenners@mac.com: i
    (12:03:56 AM) bvenners@mac.com: i'm not sure what a config success failure is
    (12:04:18 AM) joshcoughx: me either. 
    (12:04:22 AM) joshcoughx: and no javadoc.
    (12:05:57 AM) joshcoughx: i can ask him though
    (12:06:11 AM) joshcoughx: and i can always look at the code
    (12:06:14 AM) joshcoughx: i have it 
    (12:06:51 AM) bvenners@mac.com: whatever it is i'm pretty sure it will map to infoProvided in ScalaTest
    **/
  
}
