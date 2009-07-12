package org.scalatest.concurrent


import events._
import java.util.concurrent.atomic.AtomicReference

/**
 * Date: Jun 16, 2009
 * Time: 7:25:34 PM
 * @author Josh Cough
 */
trait ConductorMethods extends Suite with Logger{ thisSuite =>

  private val conductor = new AtomicReference[Conductor]()

  protected def thread[T](f: => T): Thread = conductor.get.thread{ f }
  protected def thread[T](name: String)(f: => T): Thread = conductor.get.thread(name){ f }
  protected def waitForTick(tick:Int) = conductor.get.waitForTick(tick)
  protected def tick = conductor.get.tick
  protected def finish(f: => Unit) = conductor.get.finish{ f } 
  protected implicit def addThreadsMethodToInt(nrThreads:Int) = conductor.get.addThreadsMethodToInt(nrThreads)

  /**
   * 
   */
  abstract override def runTest(testName: String, reporter: Reporter,
                                stopper: Stopper, properties: Map[String,Any], tracker:Tracker) {

    conductor.compareAndSet(conductor.get, new Conductor(this))

    val interceptor = new PassFailInterceptor(reporter)

    val startTime = System.currentTimeMillis

    super.runTest(testName, interceptor, stopper, properties, tracker)

    interceptor.failReport match {
      case Some(fail) => reporter(fail)
      case None => runConductor(testName, startTime, tracker, reporter, interceptor)
    }
  }

  /**
   *
   */
  private def runConductor(testName:String, startTime: Long, tracker: Tracker,
                           reporter:Reporter, interceptor:PassFailInterceptor){

    def testSucceededEvent = {
      TestSucceeded(
        tracker.nextOrdinal, suiteName,
        Some(getClass.getName), testName,
        Some(System.currentTimeMillis - startTime),
        interceptor.successReport.get.formatter,
        interceptor.successReport.get.rerunner,
        interceptor.successReport.get.payload
      )
    }

    def testFailedEvent(t: Throwable) = {
      TestFailed(
        tracker.nextOrdinal, t.getMessage, suiteName,
        Some(getClass.getName), testName, Some(t),
        Some(System.currentTimeMillis - startTime),
        interceptor.successReport.get.formatter,
        interceptor.successReport.get.rerunner,
        interceptor.successReport.get.payload
      )
    }

    def infoProvidedEvent(t: Throwable) = {
      InfoProvided(
        tracker.nextOrdinal, t.getMessage,
        Some(NameInfo(suiteName, Some(getClass.getName), Some(testName))),
        Some(t),
        interceptor.successReport.get.formatter,
        interceptor.successReport.get.payload
      )
    }

    var caughtException = false

    try {
      conductor.get.execute()
    } catch {
      case e => {
        caughtException = true
        reporter(testFailedEvent(e))
      }
    } finally {
      if (!caughtException) {

        val errors = conductor.get.errors

        if (errors.isEmpty) {
          reporter(testSucceededEvent)
        } else {
          reporter(testFailedEvent(errors.head))
          errors foreach{ e => reporter(infoProvidedEvent(e)) }
        }
      }
    }
  }

  /**
   * 
   */
  private class PassFailInterceptor(original: Reporter) extends Reporter {

    var successReport: Option[TestSucceeded] = None
    var failReport: Option[TestFailed] = None

    def apply(event:Event){
      event match {
        case pass:TestSucceeded => successReport = Some(pass)
        case fail:TestFailed => failReport = Some(fail)
        case _ => original(event)
      }
    }
  }
}