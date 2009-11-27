package org.scalatest.tools

import org.scalatools.testing._
import org.scalatest._

/**The test runner for ScalaTest suites. It is compiled in a second step after the rest of sbt.*/
class ScalaTestRunner(val testLoader: ClassLoader, val loggers: Array[Logger]) extends Runner
{
  def run(testClassName: String, fingerprint: TestFingerprint, eventListener: EventHandler, args: Array[String]){
    val testClass = Class.forName(testClassName, true, testLoader).asSubclass(classOf[Suite])

    if (isAccessibleSuite(testClass)) {
      val test = testClass.newInstance
      test.run(None, new ScalaTestReporter(eventListener), new Stopper {}, Filter(), Map.empty, None, new Tracker)
    }
    else throw new IllegalArgumentException("class is not an org.scalatest.Suite or something: " + testClassName)
  }

  private val emptyClassArray = new Array[java.lang.Class[T] forSome {type T}](0)

  private def isAccessibleSuite(clazz: java.lang.Class[_]): Boolean = {
    import java.lang.reflect.Modifier

    try {
      classOf[Suite].isAssignableFrom(clazz) &&
              Modifier.isPublic(clazz.getModifiers) &&
              !Modifier.isAbstract(clazz.getModifiers) &&
              Modifier.isPublic(clazz.getConstructor(emptyClassArray: _*).getModifiers)
    } catch {
      case nsme: NoSuchMethodException => false
      case se: SecurityException => false
    }
  }

  private def logError(msg: String) = loggers.foreach(_ error msg)
  private def logWarn(msg: String) = loggers.foreach(_ warn msg)
  private def logInfo(msg: String) = loggers.foreach(_ info msg)
  private def logDebug(msg: String) = loggers.foreach(_ debug msg)

  private class ScalaTestReporter(eventListener: EventHandler) extends Reporter with NotNull
  {
    import org.scalatest.events._
    var succeeded = true

    def newEvent(tn: String, r: Result, e: Option[Throwable]) = {
      r match {
        case Result.Skipped => logInfo("Test Skipped: " + tn)
        case Result.Failure => logError("Test Failed: " + tn)
        case Result.Success => logInfo("Test Passed: " + tn)
      }
      eventListener.handle(new org.scalatools.testing.Event {
        def testName = tn
        def description = tn
        def result = r
        def error = e getOrElse null
      })
    }

    def apply(event: Event) {
      event match {

        // just log this
        case t: TestStarting => logInfo("Test Starting: " + t.testName)

        // the results of running an actual test
        case t: TestPending => newEvent(t.testName, Result.Skipped, None)
        case t: TestFailed => newEvent(t.testName, Result.Failure, t.throwable)
        case t: TestSucceeded => newEvent(t.testName, Result.Success, None)
        case t: TestIgnored => newEvent(t.testName, Result.Skipped, None)

        // just log on these, no events because they aren't related to a single test case.
        case s: SuiteCompleted => logInfo("Suite Completed: " + s.suiteName)
        case s: SuiteAborted => logError("Suite Aborted: " + s.suiteName)
        case s: SuiteStarting => logInfo("Suite Starting: " + s.suiteName)
        case ip: InfoProvided => logInfo(ip.message)

        // i dont think these guys even happen when we run a single suite
        case rc: RunCompleted =>
        case rs: RunStarting =>
        case rs: RunStopped =>
        case ra: RunAborted =>
      }
    }
  }
}

