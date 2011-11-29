package org.scalatest.events

import org.scalatest.FunctionSuiteProp
import org.scalatest.Stopper
import org.scalatest.Filter
import org.scalatest.Tracker
import org.scalatest.FunSuite
import org.scalatest.Spec
import org.scalatest.FeatureSpec
import org.scalatest.FlatSpec
import org.scalatest.FreeSpec
import org.scalatest.PropSpec
import org.scalatest.WordSpec
import org.scalatest.fixture.FixtureFeatureSpec
import org.scalatest.fixture.FixtureFlatSpec
import org.scalatest.fixture.FixtureFreeSpec
import org.scalatest.fixture.FixtureFunSuite
import org.scalatest.fixture.FixturePropSpec
import org.scalatest.fixture.FixtureSpec
import org.scalatest.fixture.FixtureWordSpec

class LocationFunctionSuiteProp extends FunctionSuiteProp {
  
  test("Function suites should have correct LineInFile location in test events.") {
    forAll(examples) { suite =>
      val reporter = new EventRecordingReporter
      suite.run(None, reporter, new Stopper {}, Filter(), Map(), None, new Tracker(new Ordinal(99)))
      val eventList = reporter.eventsReceived
      eventList.foreach { event => suite.checkFun(event) }
      suite.allChecked
    }
  }
  
  private def thisLineNumber = {
    val st = Thread.currentThread.getStackTrace

    if (!st(2).getMethodName.contains("thisLineNumber"))
      st(2).getLineNumber
    else
      st(3).getLineNumber
  }
  
  def checkFileNameLineNumber(suiteName:String, expectedLineNumber: Int, event: Event):Boolean = {
    event.location match {
      case Some(evt) =>
        val lineInFile = event.location.get.asInstanceOf[LineInFile]
        assert("LocationFunctionSuiteProp.scala" == lineInFile.fileName, "Suite " + suiteName + " - Event " + event.getClass.getName + " expected LocationFunctionSuiteProp.scala, got " + lineInFile.fileName)
        assert(expectedLineNumber == lineInFile.lineNumber, "Suite " + suiteName + " - Event " + event.getClass.getName + " expected " + expectedLineNumber + ", got " + lineInFile.lineNumber)
        true
      case None => 
        fail("Suite " + suiteName + " - Event " + event.toString() + " does not have location.")
    }
  }
  
  trait Services { 
    def checkFun(event: Event): Unit
    def allChecked: Unit
  }
  
  type FixtureServices = Services
  
  def funSuite = new FunSuite with FixtureServices {
    test("succeed") {
      
    }
    test("pending") {
      pending
    }
    test("cancel") {
      cancel
    }
    ignore("ignore") {
      
    }
    val suiteTypeName: String = "FunSuite"
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
            case "pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 16, event)
            case "cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 14, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 23, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 21, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
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
  
  def fixtureFunSuite = new FixtureFunSuite with FixtureServices {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
    test("succeed") { param =>
      
    }
    test("pending") { param =>
      pending
    }
    test("cancel") { param =>
      cancel
    }
    ignore("ignore") { param =>
      
    }
    val suiteTypeName: String = "FunSuite"
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
            case "pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 16, event)
            case "cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 14, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 23, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 21, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
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
  
  def spec = new Spec with FixtureServices {
    describe("A Spec") {
      it("succeed") {
        
      }
      it("pending") {
        pending
      }
      it("cancel") {
        cancel
      }
      ignore("ignore") {
      
      }
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "Spec"
    def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "A Spec succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
            case "A Spec pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
            case "A Spec cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 15, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 24, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 22, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 20, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def fixtureSpec = new FixtureSpec with FixtureServices {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
    describe("A Spec") {
      it("succeed") { param =>
        
      }
      it("pending") { param =>
        pending
      }
      it("cancel") { param =>
        cancel
      }
      ignore("ignore") { param =>
      
      }
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "Spec"
    def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "A Spec succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
            case "A Spec pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
            case "A Spec cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 15, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 24, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 22, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 20, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def featureSpec = new FeatureSpec with FixtureServices {
    scenario("succeed") {
      
    }
    scenario("pending") {
      pending
    }
    scenario("cancel") {
      cancel
    }
    ignore("ignore") {
      
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "FeatureSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Scenario: succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
            case "Scenario: pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 16, event)
            case "Scenario: cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 14, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 23, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 21, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
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
  
  def fixtureFeatureSpec = new FixtureFeatureSpec with FixtureServices {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
    feature("Test") {
      scenario("succeed") { param =>
      
      }
      scenario("pending") { param =>
        pending
      }
      scenario("cancel") { param =>
        cancel
      }
      ignore("ignore") { param =>
      
      }
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "FixtureFeatureSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test Scenario: succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
            case "Test Scenario: pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
            case "Test Scenario: cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 15, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 24, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 22, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 20, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def flatSpec = new FlatSpec with FixtureServices {
    "Test" should "succeed" in {
      
    }
    "Test" should "pending" in {
      pending
    }
    "Test" should "cancel" in {
      cancel
    }
    ignore should "ignore" in {
      
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "FlatSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 16, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 14, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 23, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 21, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def fixtureFlatSpec = new FixtureFlatSpec with FixtureServices {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
    "Test" should "succeed" in { param =>
      
    }
    "Test" should "pending" in { param =>
      pending
    }
    "Test" should "cancel" in { param =>
      cancel
    }
    ignore should "ignore" in { param =>
      
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "FixtureFlatSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 16, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 14, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 23, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 21, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def freeSpec = new FreeSpec with FixtureServices {
    "Test" - {
      "should succeed" in {
        
      }
      "should pending" in {
        pending
      }
      "should cancel" in {
        cancel
      }
      "should ignore" ignore {
        
      }
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "FreeSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 15, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 24, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 22, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 20, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def fixtureFreeSpec = new FixtureFreeSpec with FixtureServices {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
    "Test" - {
      "should succeed" in { param =>
        
      }
      "should pending" in { param =>
        pending
      }
      "should cancel" in { param =>
        cancel
      }
      "should ignore" ignore { param =>
        
      }
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "FreeSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 15, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 24, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 22, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 20, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def propSpec = new PropSpec with FixtureServices {
    property("Test should succeed") {
      
    }
    property("Test should pending") {
      pending
    }
    property("Test should cancel") {
      cancel
    }
    ignore("Test should ignore") {
        
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "PropSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 16, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 14, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 23, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 21, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
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
  
  def fixturePropSpec = new FixturePropSpec with FixtureServices {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
    property("Test should succeed") { param =>
      
    }
    property("Test should pending") { param =>
      pending
    }
    property("Test should cancel") { param =>
      cancel
    }
    ignore("Test should ignore") { param =>
        
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "PropSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 16, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 14, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 23, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 21, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
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
  
  def wordSpec = new WordSpec with FixtureServices {
    "Test" should {
      "succeed" in {
        
      }
      "pending" in {
        pending
      }
      "cancel" in {
        cancel
      }
      "ignore " ignore {
        
      }
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "WordSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 15, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 24, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 22, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 20, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
  
  def fixtureWordSpec = new FixtureWordSpec with FixtureServices {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
    "Test" should {
      "succeed" in { param =>
        
      }
      "pending" in { param =>
        pending
      }
      "cancel" in { param =>
        cancel
      }
      "ignore " ignore { param =>
        
      }
    }
    var startingSucceedEvent, startingPendingEvent, startingCancelEvent, scopeOpenedEvent, scopeClosedEvent, succeededEvent, pendingEvent, canceledEvent, ignoredEvent = false
    val suiteTypeName: String = "WordSpec"
      def checkFun(event: Event) {
      event match {
        case testStarting: TestStarting => 
          testStarting.testName match {
            case "Test should succeed" => startingSucceedEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 19, event)
            case "Test should pending" => startingPendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 17, event)
            case "Test should cancel" => startingCancelEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 15, event)
            case _ => fail("Unknown TestStarting for testName=" + testStarting.testName + " in " + suiteTypeName)
          }
        case testSucceed: TestSucceeded => succeededEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 24, event)
        case testPending: TestPending => pendingEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 22, event)
        case testCancel: TestCanceled => canceledEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 20, event)
        case testIgnored: TestIgnored => ignoredEvent = checkFileNameLineNumber(suiteTypeName, thisLineNumber - 18, event)
        case scopeOpened: ScopeOpened => scopeOpenedEvent = true
        case scopeClosed: ScopeClosed => scopeClosedEvent = true
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
      assert(scopeOpenedEvent, suiteTypeName + ": ScopeOpened not fired.")
      assert(scopeClosedEvent, suiteTypeName + ": ScopeClosed not fired.")
    }
  }
}