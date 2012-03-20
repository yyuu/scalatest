package org.scalatest.tools
import org.scalatest.FunSuite
import org.scalasbt.testing.EventHandler
import org.scalasbt.testing.Event
import org.scalasbt.testing.ErrorEvent
import org.scalasbt.testing.FailureEvent
import org.scalasbt.testing.SkippedEvent
import org.scalasbt.testing.SuccessEvent
import org.scalasbt.testing.Logger
import org.scalasbt.testing.TestSelector
import org.scalasbt.testing.NestedTestSelector
import org.scalasbt.testing.SuiteSelector
import org.scalasbt.testing.NestedSuiteSelector

class ScalaTestFramework2Suite extends FunSuite {
  
  class TestEventHandler extends EventHandler {
    
    private var errorEvents = List[ErrorEvent]()
    private var failureEvents = List[FailureEvent]()
    private var skippedEvents = List[SkippedEvent]()
    private var successEvents = List[SuccessEvent]()
    
    override def handle(event: Event): Unit = {
      event match {
        case e: SuccessEvent => successEvents ::= e
        case e: ErrorEvent => errorEvents ::= e
        case e: FailureEvent => failureEvents ::= e
        case e: SkippedEvent => skippedEvents ::= e
      }
    }
    
    def errorEventsReceived = errorEvents.reverse
    def failureEventsReceived = failureEvents.reverse
    def skippedEventsReceived = skippedEvents.reverse
    def successEventsReceived = successEvents.reverse
  }
  
  class TestLogger extends Logger {
    
    private var errorList = List[String]()
    private var warnList = List[String]()
    private var infoList = List[String]()
    private var debugList = List[String]()
    private var traceList = List[Throwable]()
    
    def ansiCodesSupported = false
    def error(msg: String) {
      errorList ::= msg
    }
    def warn(msg: String) {
      warnList ::= msg
    }
    def info(msg: String) {
      infoList ::= msg
    }
    def debug(msg: String) {
      debugList ::= msg
    }
    def trace(t: Throwable) {
      traceList ::= t
    }
    
    def errorReceived = errorList.reverse
    def warnReceived = warnList.reverse
    def infoReceived = infoList.reverse
    def debugReceived = debugList.reverse
    def traceReceived = traceList.reverse
  }

  test("framework name") {
    assert(new ScalaTestFramework2().name() === "ScalaTest")
  }
  
  test("tests contains 3 test fingerprint, they are SubclassFingerprint for org.scalatest.Suite, AnnotatedFingerprint for org.scalatest.WrapWith, " +
  		"and DoNotDiscoverFingerprint for org.scalatest.DoNotDiscover"){
    val framework = new ScalaTestFramework2
    val fingerprints = framework.fingerprints
    assert(fingerprints.size === 3)

    val testFingerprint =
      fingerprints(0).asInstanceOf[org.scalasbt.testing.SubclassFingerprint]
    assert(testFingerprint.isModule === false)
    assert(testFingerprint.superclassName === "org.scalatest.Suite")
    
    val annotatedFingerprint = 
      fingerprints(1).asInstanceOf[org.scalasbt.testing.AnnotatedFingerprint]
    assert(annotatedFingerprint.isModule === false)
    assert(annotatedFingerprint.annotationName === "org.scalatest.WrapWith")
    
    val doNotDiscoverFingerprint = 
      fingerprints(2).asInstanceOf[org.scalasbt.testing.DoNotDiscoverFingerprint]
    assert(doNotDiscoverFingerprint.annotationName == "org.scalatest.DoNotDiscover")
  }
  
  val testClassLoader = getClass.getClassLoader
  val subClassFingerprint = new org.scalasbt.testing.SubclassFingerprint {
                              def superclassName = "org.scalatest.Suite"
                              def isModule = false
                            }
  
  val framework = new ScalaTestFramework2
  
  def assertSuiteSuccessEvent(event: SuccessEvent, suiteClassName: String, testName: String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case testSelector: TestSelector => 
        assert(testName === testSelector.getTestName)
      case _ => 
        fail("Expected to get TestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  def assertNestedSuiteSuccessEvent(event: SuccessEvent, suiteClassName: String, suiteId:String, testName: String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case nestedTestSelector: NestedTestSelector => 
        assert(suiteId === nestedTestSelector.getSuiteId)
        assert(testName === nestedTestSelector.getTestName)
      case _ => 
        fail("Expected to get NestedTestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  def assertSuiteFailureEvent(event: FailureEvent, suiteClassName: String, testName: String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case testSelector: TestSelector => 
        assert(testName === testSelector.getTestName)
      case _ => 
        fail("Expected to get TestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  def assertNestedSuiteFailureEvent(event: FailureEvent, suiteClassName: String, suiteId:String, testName: String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case nestedTestSelector: NestedTestSelector => 
        assert(suiteId === nestedTestSelector.getSuiteId)
        assert(testName === nestedTestSelector.getTestName)
      case _ => 
        fail("Expected to get NestedTestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  def assertSuiteErrorEvent(event: ErrorEvent, suiteClassName: String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case suiteSelector: SuiteSelector => 
        // Nothing more to check, just make sure it is SuiteSelector.
      case _ => 
        fail("Expected to get TestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  def assertNestedSuiteErrorEvent(event: ErrorEvent, suiteClassName: String, suiteId:String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case nestedSuiteSelector: NestedSuiteSelector => 
        assert(suiteId === nestedSuiteSelector.getSuiteId)
      case _ => 
        fail("Expected to get NestedTestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  def assertSuiteSkippedEvent(event: SkippedEvent, suiteClassName: String, testName: String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case testSelector: TestSelector => 
        assert(testName === testSelector.getTestName)
      case _ => 
        fail("Expected to get TestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  def assertNestedSuiteSkippedEvent(event: SkippedEvent, suiteClassName: String, suiteId:String, testName: String) {
    assert(suiteClassName === event.getFullyQualifiedName)
    val selector = event.getSelector
    selector match {
      case nestedTestSelector: NestedTestSelector => 
        assert(suiteId === nestedTestSelector.getSuiteId)
        assert(testName === nestedTestSelector.getTestName)
      case _ => 
        fail("Expected to get NestedTestSelector, but got: " + selector.getClass.getName)
    }
  }
  
  test("ScalaTestRunner should return task that run whole suite when valid class name is passed to task(fullyQualifiedName: String, fingerprint: Fingerprint)") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    
    val task = runner.task("org.scalatest.tools.scalasbt.SampleSuite", subClassFingerprint)
    assert(task != null)
    task.execute()
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 3)
    assertSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.SampleSuite", "test 1")
    assertSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.SampleSuite", "test 2")
    assertSuiteSuccessEvent(successEvents(2), "org.scalatest.tools.scalasbt.SampleSuite", "test 3")
    assert(testEventHandler.errorEventsReceived.length === 0)
    assert(testEventHandler.failureEventsReceived.length === 0)
    assert(testEventHandler.skippedEventsReceived.length === 0)
  }
  
  test("ScalaTestRunner should return task that run whole suite when valid class name is passed to task(fullyQualifiedName: String, fingerprint: Fingerprint), " +
  	   "even if the suite class is marked as @DoNotDiscover") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    
    val task = runner.task("org.scalatest.tools.scalasbt.DoNotDiscoverSuite", subClassFingerprint)
    assert(task != null)
    task.execute()
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 3)
    assertSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.DoNotDiscoverSuite", "test 1")
    assertSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.DoNotDiscoverSuite", "test 2")
    assertSuiteSuccessEvent(successEvents(2), "org.scalatest.tools.scalasbt.DoNotDiscoverSuite", "test 3")
    assert(testEventHandler.errorEventsReceived.length === 0)
    assert(testEventHandler.failureEventsReceived.length === 0)
    assert(testEventHandler.skippedEventsReceived.length === 0)
  }
  
  test("When suite is neither subclass of org.scalatest.Suite or annotated with WrapWith, IllegalArgumentException will be thrown") {
    intercept[IllegalArgumentException] {
      val runner = framework.runner(Array.empty, testClassLoader, new TestEventHandler, Array(new TestLogger))
      val notASuiteTask = runner.task("org.scalatest.tools.scalasbt.NotASuite", null)
      notASuiteTask.execute()
    }
  }
  
  test("When an invalid suite class name is passed into to task(fullyQualifiedName: String, fingerprint: Fingerprint), IllegalArgumentException " +
  	   "will be thrown") {
    intercept[IllegalArgumentException] {
      val runner = framework.runner(Array.empty, testClassLoader, new TestEventHandler, Array(new TestLogger))
      val doesNotExistTask = runner.task("org.scalatest.tools.scalasbt.DoesNotExist", null)
      doesNotExistTask.execute()
    }
  }
  
  test("Nested suites will be included in task returned from task(fullyQualifiedName: String, fingerprint: Fingerprint)") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    val task = runner.task("org.scalatest.tools.scalasbt.SuiteWithNestedSuites", subClassFingerprint)
    assert(task != null)
    task.execute()
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 9)
    assertNestedSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 1", "nested 1 test 1")
    assertNestedSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 1", "nested 1 test 2")
    assertNestedSuiteSuccessEvent(successEvents(2), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 1", "nested 1 test 3")
    assertNestedSuiteSuccessEvent(successEvents(3), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 2", "nested 2 test 1")
    assertNestedSuiteSuccessEvent(successEvents(4), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 2", "nested 2 test 2")
    assertNestedSuiteSuccessEvent(successEvents(5), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 2", "nested 2 test 3")
    assertSuiteSuccessEvent(successEvents(6), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "test 1")
    assertSuiteSuccessEvent(successEvents(7), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "test 2")
    assertSuiteSuccessEvent(successEvents(8), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "test 3")
    assert(testEventHandler.errorEventsReceived.length === 0)
    assert(testEventHandler.failureEventsReceived.length === 0)
    assert(testEventHandler.skippedEventsReceived.length === 0)
  }
  
  test("Ignore, pending, failed, canceled, suite aborted events should be translated and reported correctly for the suite and its nested suites") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    val task = runner.task("org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", subClassFingerprint)
    assert(task != null)
    task.execute()
    
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 3)
    assertNestedSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 1", "nested 1 success")
    assertNestedSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 2", "nested 2 success")
    assertSuiteSuccessEvent(successEvents(2), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "success")
    
    val failureEvents = testEventHandler.failureEventsReceived
    assert(failureEvents.length === 3)
    assertNestedSuiteFailureEvent(failureEvents(0), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 1", "nested 1 failed")
    assertNestedSuiteFailureEvent(failureEvents(1), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 2", "nested 2 failed")
    assertSuiteFailureEvent(failureEvents(2), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "failed")
    
    val errorEvents = testEventHandler.errorEventsReceived
    assert(errorEvents.length === 1)
    assertNestedSuiteErrorEvent(errorEvents(0), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 3")
    
    val skippedEvents = testEventHandler.skippedEventsReceived
    assert(skippedEvents.length === 9)
    assertNestedSuiteSkippedEvent(skippedEvents(0), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 1", "nested 1 ignored")
    assertNestedSuiteSkippedEvent(skippedEvents(1), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 1", "nested 1 pending")
    assertNestedSuiteSkippedEvent(skippedEvents(2), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 1", "nested 1 canceled")
    assertNestedSuiteSkippedEvent(skippedEvents(3), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 2", "nested 2 ignored")
    assertNestedSuiteSkippedEvent(skippedEvents(4), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 2", "nested 2 pending")
    assertNestedSuiteSkippedEvent(skippedEvents(5), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "nested 2", "nested 2 canceled")
    assertSuiteSkippedEvent(skippedEvents(6), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "ignored")
    assertSuiteSkippedEvent(skippedEvents(7), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "pending")
    assertSuiteSkippedEvent(skippedEvents(8), "org.scalatest.tools.scalasbt.SuiteWithFailedSkippedTests", "canceled")
  }
  
  test("SuiteSelector should select and run test(s) in selected suite") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    val task = runner.task("org.scalatest.tools.scalasbt.SampleSuite", false, Array(new SuiteSelector()))
    task.execute()
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 3)
    assertSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.SampleSuite", "test 1")
    assertSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.SampleSuite", "test 2")
    assertSuiteSuccessEvent(successEvents(2), "org.scalatest.tools.scalasbt.SampleSuite", "test 3")
    assert(testEventHandler.errorEventsReceived.length === 0)
    assert(testEventHandler.failureEventsReceived.length === 0)
  }
  
  test("TestSelector should select and run selected test(s) in suite, excluding nested suites") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    val task = runner.task("org.scalatest.tools.scalasbt.SampleSuite", false, Array(new TestSelector("test 1"), new TestSelector("test 3")))
    task.execute()
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 2)
    assertSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.SampleSuite", "test 1")
    assertSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.SampleSuite", "test 3")
    assert(testEventHandler.errorEventsReceived.length === 0)
    assert(testEventHandler.failureEventsReceived.length === 0)
    assert(testEventHandler.skippedEventsReceived.length === 0)
    
    val testEventHandler2 = new TestEventHandler
    val runner2 = framework.runner(Array.empty, testClassLoader, testEventHandler2, Array(new TestLogger))
    val task2 = runner2.task("org.scalatest.tools.scalasbt.SuiteWithNestedSuites", false, Array(new TestSelector("test 2")))
    task2.execute()
    val successEvents2 = testEventHandler2.successEventsReceived
    assert(successEvents2.length === 1)
    assertSuiteSuccessEvent(successEvents2(0), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "test 2")
    assert(testEventHandler2.errorEventsReceived.length === 0)
    assert(testEventHandler2.failureEventsReceived.length === 0)
    assert(testEventHandler2.skippedEventsReceived.length === 0)
  }
  
  test("NestedSuiteSelector should select and run test(s) in selected nested suite") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    val task = runner.task("org.scalatest.tools.scalasbt.SuiteWithNestedSuites", false, Array(new NestedSuiteSelector("nested 1")))
    task.execute()
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 3)
    assertNestedSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 1", "nested 1 test 1")
    assertNestedSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 1", "nested 1 test 2")
    assertNestedSuiteSuccessEvent(successEvents(2), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 1", "nested 1 test 3")
    assert(testEventHandler.errorEventsReceived.length === 0)
    assert(testEventHandler.failureEventsReceived.length === 0)
    assert(testEventHandler.skippedEventsReceived.length === 0)
  }
  
  test("NestedTestSelector should select and run selected test(s) in selected nested suite") {
    val testEventHandler = new TestEventHandler
    val runner = framework.runner(Array.empty, testClassLoader, testEventHandler, Array(new TestLogger))
    val task = runner.task("org.scalatest.tools.scalasbt.SuiteWithNestedSuites", false, Array(new NestedTestSelector("nested 1", "nested 1 test 1"), new NestedTestSelector("nested 2", "nested 2 test 3")))
    task.execute()
    val successEvents = testEventHandler.successEventsReceived
    assert(successEvents.length === 2)
    assertNestedSuiteSuccessEvent(successEvents(0), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 1", "nested 1 test 1")
    assertNestedSuiteSuccessEvent(successEvents(1), "org.scalatest.tools.scalasbt.SuiteWithNestedSuites", "nested 2", "nested 2 test 3")
    assert(testEventHandler.errorEventsReceived.length === 0)
    assert(testEventHandler.failureEventsReceived.length === 0)
    assert(testEventHandler.skippedEventsReceived.length === 0)
  }
  
  test("ScalaTestRunner should print summary and return true when SbtLogInfoReporter is used, and throw IllegalStateException if 'done' method is called twice.") {
    val testLogger = new TestLogger
    val runner = framework.runner(Array.empty, testClassLoader, new TestEventHandler, Array(testLogger))
    val task = runner.task("org.scalatest.tools.scalasbt.SampleSuite", false, Array(new SuiteSelector()))
    task.execute()
    assert(runner.done === true)
    intercept[IllegalStateException] {
      runner.done()
    }
  }
}