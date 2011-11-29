package org.scalatest.events
import org.scalatest.Stopper
import org.scalatest.Filter
import org.scalatest.Tracker
import org.scalatest.Suite
import org.scalatest.Ignore
import org.scalatest.MethodSuiteProp
import org.scalatest.junit.JUnit3Suite
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.{Ignore => JUnitIgnore}
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.{Test => TestNG}

trait Services { 
  def checkFun(event: Event): Unit
  def allChecked: Unit
}

class LocationMethodSuiteProp extends MethodSuiteProp {
  
  test("Method suites should have correct TopOfMethod location in test events.") {
    forAll(examples) { suite =>
      val reporter = new EventRecordingReporter
      suite.run(None, reporter, new Stopper {}, Filter(), Map(), None, new Tracker(new Ordinal(99)))
      val eventList = reporter.eventsReceived
      eventList.foreach { event => suite.checkFun(event) }
      suite.allChecked
    }
  }
  
  def checkTopOfMethod(suiteTypeName: String, methodName:String, event: Event) = {
    event.location match {
      case Some(location) => 
        location match {
          case topOfMethod:TopOfMethod => 
            val expectedClassName = "org.scalatest.events.LocationMethodSuiteProp$" + suiteTypeName
            val expectedMethodId = "public void " + expectedClassName + "." + methodName + "()"
            assert(expectedClassName == topOfMethod.className, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.className expected to be " + expectedClassName + ", but got " + topOfMethod.className)
            assert(expectedMethodId == topOfMethod.methodId, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.methodId expected to be " + expectedMethodId + ", but got " + topOfMethod.methodId)
          case _ => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " event expect to have TopOfMethod location, but got " + location.getClass.getName)
        }
      case None => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " does not have location (None)")
    }
    true
  }
  
  type FixtureServices = Services
  
  def suite = new TestLocationSuite
  class TestLocationSuite extends Suite with FixtureServices {
    def testSucceed() {
      
    }
    def testPending() {
      pending
    }
    def testCancel() {
      cancel
    }
    @Ignore
    def testIgnore() {
      
    }
    val suiteTypeName: String = "TestLocationSuite"
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "testSucceed" => startingSucceedEvent = checkTopOfMethod(suiteTypeName, "testSucceed", event)
            case "testPending" => startingPendingEvent = checkTopOfMethod(suiteTypeName, "testPending", event)
            case "testCancel" => startingCancelEvent = checkTopOfMethod(suiteTypeName, "testCancel", event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkTopOfMethod(suiteTypeName, "testSucceed", event)
        case testPending: TestPending => pendingEvent = checkTopOfMethod(suiteTypeName, "testPending", event)
        case testCancel: TestCanceled => canceledEvent = checkTopOfMethod(suiteTypeName, "testCancel", event)
        case testIgnored: TestIgnored => ignoredEvent = checkTopOfMethod(suiteTypeName, "testIgnore", event)
        case _ => fail("Unexpected event:" + event.getClass.getName + " in " + suiteTypeName)
      }
    }
    def allChecked = {
      assert(startingSucceedEvent, suiteTypeName + ": TestStarting for succeed not fired.")
      assert(startingPendingEvent, suiteTypeName + ": TestStarting for pending not fired.")
      assert(startingCancelEvent, suiteTypeName + ": TestStarting for cancel not fired.")
      assert(succeededEvent, suiteTypeName + ": TestSucceeded not fired.")
      assert(pendingEvent, suiteTypeName + ": TestPending not fired.")
      assert(canceledEvent, suiteTypeName + ": TestCanceled not fired.")
      assert(ignoredEvent, suiteTypeName + ": TestIgnored not fired.")
    }
  }
  
  def junit3Suite = new TestLocationJUnit3Suite
  
  def junitSuite = new TestLocationJUnitSuite
  
  def testngSuite = new TestLocationTestNGSuite
}

class TestLocationJUnit3Suite extends JUnit3Suite with Services {
  def testSucceed() { 
    
  }
  val suiteTypeName: String = "TestLocationJUnit3Suite"
  var startingSucceedEvent, succeededEvent = false
  def checkTopOfMethod(suiteTypeName: String, methodName:String, event: Event) = {
    event.location match {
      case Some(location) => 
        location match {
          case topOfMethod:TopOfMethod => 
            val expectedClassName = "org.scalatest.events." + suiteTypeName
            val expectedMethodId = "public void " + expectedClassName + "." + methodName + "()"
            assert(expectedClassName == topOfMethod.className, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.className expected to be " + expectedClassName + ", but got " + topOfMethod.className)
            assert(expectedMethodId == topOfMethod.methodId, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.methodId expected to be " + expectedMethodId + ", but got " + topOfMethod.methodId)
          case _ => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " event expect to have TopOfMethod location, but got " + location.getClass.getName)
        }
      case None => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " does not have location (None)")
    }
    true
  }
  def checkFun(event: Event) {
    event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "testSucceed(org.scalatest.events.TestLocationJUnit3Suite)" => startingSucceedEvent = checkTopOfMethod(suiteTypeName, "testSucceed", event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkTopOfMethod(suiteTypeName, "testSucceed", event)
        case _ => fail("Unexpected event:" + event.getClass.getName + " in " + suiteTypeName)
      }
    }
  def allChecked = {
    assert(startingSucceedEvent, suiteTypeName + ": TestStarting for testSucceed() not fired.")
    assert(succeededEvent, suiteTypeName + ": TestSucceeded not fired.")
  }
}

class TestLocationJUnitSuite extends JUnitSuite with Services {
  @Test
  def succeed() { 
      
  }
  @JUnitIgnore 
  def ignore() {
      
  }
  val suiteTypeName: String = "TestLocationJUnitSuite"
  var startingSucceedEvent, succeededEvent, ignoredEvent = false
  def checkTopOfMethod(suiteTypeName: String, methodName:String, event: Event) = {
    event.location match {
      case Some(location) => 
        location match {
          case topOfMethod:TopOfMethod => 
            val expectedClassName = "org.scalatest.events." + suiteTypeName
            val expectedMethodId = "public void " + expectedClassName + "." + methodName + "()"
            assert(expectedClassName == topOfMethod.className, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.className expected to be " + expectedClassName + ", but got " + topOfMethod.className)
            assert(expectedMethodId == topOfMethod.methodId, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.methodId expected to be " + expectedMethodId + ", but got " + topOfMethod.methodId)
          case _ => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " event expect to have TopOfMethod location, but got " + location.getClass.getName)
        }
      case None => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " does not have location (None)")
    }
    true
  }
  def checkFun(event: Event) {
    event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "succeed" => startingSucceedEvent = checkTopOfMethod(suiteTypeName, "succeed", event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkTopOfMethod(suiteTypeName, "succeed", event)
        case testIgnored: TestIgnored => ignoredEvent = checkTopOfMethod(suiteTypeName, "ignore", event)
        case _ => fail("Unexpected event:" + event.getClass.getName + " in " + suiteTypeName)
    }
  }
  def allChecked = {
    assert(startingSucceedEvent, suiteTypeName + ": TestStarting for succeed() not fired.")
    assert(succeededEvent, suiteTypeName + ": TestSucceeded not fired.")
    assert(succeededEvent, suiteTypeName + ": TestIgnored not fired.")
  }
}

class TestLocationTestNGSuite extends TestNGSuite with Services {
  @TestNG
  def succeed() { 
      
  }
  @TestNG(enabled=false) 
  def ignore() {
    
  }
  var startingSucceedEvent, succeededEvent, ignoredEvent = false
  val suiteTypeName: String = "TestLocationTestNGSuite"
  def checkTopOfMethod(suiteTypeName: String, methodName: String, event: Event) = {
    event.location match {
      case Some(location) => 
        location match {
          case topOfMethod:TopOfMethod => 
            val expectedClassName = "org.scalatest.events." + suiteTypeName
            val expectedMethodId = "public void " + expectedClassName + "." + methodName + "()"
            assert(expectedClassName == topOfMethod.className, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.className expected to be " + expectedClassName + ", but got " + topOfMethod.className)
            assert(expectedMethodId == topOfMethod.methodId, "Suite " + suiteTypeName + "'s " + event.getClass.getName + " event's TopOfMethod.methodId expected to be " + expectedMethodId + ", but got " + topOfMethod.methodId)
          case _ => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " event expect to have TopOfMethod location, but got " + location.getClass.getName)
        }
      case None => throw new RuntimeException("Suite " + suiteTypeName + "'s " + event.getClass.getName + " does not have location (None)")
    }
    true
  }
  def checkFun(event: Event) {
    event match {
      case testStarting: TestStarting => 
        testStarting.testName match {
          case "succeed" => startingSucceedEvent = checkTopOfMethod(suiteTypeName, "succeed", event)
          case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
        }
      case testSucceed: TestSucceeded => succeededEvent = checkTopOfMethod(suiteTypeName, "succeed", event)
      case testIgnored: TestIgnored => ignoredEvent = checkTopOfMethod(suiteTypeName, "ignore", event)
      case suiteStarting: SuiteStarting => // Tested in LocationSuiteProp
      case suiteCompleted: SuiteCompleted => // Tested in LocationSuiteProp
      case _ => fail("Unexpected event:" + event.getClass.getName + " in " + suiteTypeName)
    }
  }
  def allChecked = {
    assert(startingSucceedEvent, suiteTypeName + ": TestStarting for succeed() not fired.")
    assert(succeededEvent, suiteTypeName + ": TestSucceeded not fired.")
    assert(succeededEvent, suiteTypeName + ": TestIgnored not fired.")
  }
}