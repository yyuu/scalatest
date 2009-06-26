package org.scalatest.multi

import java.io.{StringWriter, PrintWriter}
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.{ArrayBlockingQueue, Semaphore, CountDownLatch, TimeUnit}
import MultiThreadedTest._

/**
 *
 * 
 * Date: Jun 16, 2009
 * Time: 7:25:34 PM
 * @author Josh Cough
 */
trait MultiThreadedSuite extends Suite with MultiThreadedTest{ thisSuite =>

  /**
   * 
   */
  override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, groupsToInclude: Set[String],
      groupsToExclude: Set[String], properties: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {

    def buildReport(ex:Option[Throwable]) = {
      new Report(getClass.getName, getClass.getName, ex, None)
    }

    reporter.testStarting(buildReport(None))

    runMultiThreadedTest()

    if( errors.isEmpty ) reporter.testSucceeded(buildReport(None))
    else reporter.testFailed(buildReport(Some(errors.peek)))
  }
}
