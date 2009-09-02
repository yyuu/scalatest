package org.scalatest

class SpecSuite extends FunSuite {

  test("an example must get invoked by execute") {
    class MySpec extends Spec {
      var exampleWasInvoked = false
      it should "get invoked" in {
        exampleWasInvoked = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.exampleWasInvoked)
  }
  
  test("two examples must get invoked by execute") {
    class MySpec extends Spec {
      var exampleWasInvoked = false
      var example2WasInvoked = false
      it should "get invoked" in {
        exampleWasInvoked = true
      }
      it should "also get invoked" in {
        example2WasInvoked = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.exampleWasInvoked)
    assert(a.example2WasInvoked)
  }

  test("three examples must get invoked by execute") {
    class MySpec extends Spec {
      var exampleWasInvoked = false
      var example2WasInvoked = false
      var example3WasInvoked = false
      it should "get invoked" in {
        exampleWasInvoked = true
      }
      it should "also get invoked" in {
        example2WasInvoked = true
      }
      it should "also also get invoked" in {
        example3WasInvoked = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.exampleWasInvoked)
    assert(a.example2WasInvoked)
    assert(a.example3WasInvoked)
  }

  test("two examples should be invoked in order") {
    class MySpec extends Spec {
      var example1WasInvoked = false
      var example2WasInvokedAfterExample1 = false
      it should "get invoked" in {
        example1WasInvoked = true
      }
      it should "also get invoked" in {
        if (example1WasInvoked)
          example2WasInvokedAfterExample1 = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.example1WasInvoked)
    assert(a.example2WasInvokedAfterExample1)
  }

  test("three examples should be invoked in order") {
    class MySpec extends Spec {
      var example1WasInvoked = false
      var example2WasInvokedAfterExample1 = false
      var example3WasInvokedAfterExample2 = false
      it should "get invoked" in {
        example1WasInvoked = true
      }
      it should "also get invoked" in {
        if (example1WasInvoked)
          example2WasInvokedAfterExample1 = true
      }
      it should "also also get invoked" in {
        if (example2WasInvokedAfterExample1)
          example3WasInvokedAfterExample2 = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.example1WasInvoked)
    assert(a.example2WasInvokedAfterExample1)
    assert(a.example3WasInvokedAfterExample2)
  }

  test("three examples should be invoked in order even when two are surrounded by a describe") {
    class MySpec extends Spec {
      var example1WasInvoked = false
      var example2WasInvokedAfterExample1 = false
      var example3WasInvokedAfterExample2 = false
      it should "get invoked" in {
        example1WasInvoked = true
      }
      describe("Stack") {
        it should "also get invoked" in {
          if (example1WasInvoked)
            example2WasInvokedAfterExample1 = true
        }
        it should "also also get invoked" in {
          if (example2WasInvokedAfterExample1)
            example3WasInvokedAfterExample2 = true
        }
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.example1WasInvoked)
    assert(a.example2WasInvokedAfterExample1)
    assert(a.example3WasInvokedAfterExample2)
  }

  test("an example should show up in testNames") {
    class MySpec extends Spec {
      var exampleWasInvoked = false
      it should "get invoked" in {
        exampleWasInvoked = true
      }
    }
    val a = new MySpec
    assert(a.testNames.size === 1)
    println("@@@******" + a.testNames)
    assert(a.testNames.contains("it should get invoked"))
  }
   
  test("two examples should show up in testNames") {
    class MySpec extends Spec {
      var example1WasInvoked = false
      var example2WasInvokedAfterExample1 = false
      it should "get invoked" in {
        example1WasInvoked = true
      }
      it should "also get invoked" in {
        if (example1WasInvoked)
          example2WasInvokedAfterExample1 = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.testNames.size === 2)
    assert(a.testNames.contains("it should get invoked"))
    assert(a.testNames.contains("it should also get invoked"))
  }
   
  test("two examples should show up in order of appearance in testNames") {
    class MySpec extends Spec {
      var example1WasInvoked = false
      var example2WasInvokedAfterExample1 = false
      it should "get invoked" in {
        example1WasInvoked = true
      }
      it should "also get invoked" in {
        if (example1WasInvoked)
          example2WasInvokedAfterExample1 = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.testNames.size === 2)
    assert(a.testNames.elements.toList(0) === "it should get invoked")
    assert(a.testNames.elements.toList(1) === "it should also get invoked")
  }
 
  test("test names should include an enclosing describe string, separated by a space") {
    class MySpec extends Spec {
      describe("A Stack") {
        it should "allow me to pop" in {}
        it should "allow me to push" in {}
      }
    }
    val a = new MySpec
    assert(a.testNames.size === 2)
    assert(a.testNames.elements.toList(0) === "A Stack should allow me to pop")
    assert(a.testNames.elements.toList(1) === "A Stack should allow me to push")
  }

  test("test names should properly nest descriptions in test names") {
    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when not empty)") {
          it should "allow me to pop" in {}
        }
        describe("(when not full)") {
          it should "allow me to push" in {}
        }
      }
    }
    val a = new MySpec
    assert(a.testNames.size === 2)
    assert(a.testNames.elements.toList(0) === "A Stack (when not empty) should allow me to pop")
    assert(a.testNames.elements.toList(1) === "A Stack (when not full) should allow me to push")
  }
  
  test("should be able to mix in ImpSuite without any problems") {
    class MySpec extends Spec with ImpSuite {
      describe("A Stack") {
        describe("(when not empty)") {
          it should "allow me to pop" in {}
        }
        describe("(when not full)") {
          it should "allow me to push" in {}
        }
      }
    }
    val a = new MySpec
    a.execute()
  }

  test("A given reporter clause should work") {
    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when not empty)") {
          it should "allow me to pop" given reporter in {
            reporter => ()
          }
        }
        describe("(when not full)") {
          it should "allow me to push" in {}
        }
      }
    }
    val a = new MySpec
    a.execute()
  }

  test("A given reporter clause should be able to send info to the reporter") {

    val expectedMessage = "this is the expected message"

    class MyReporter extends Reporter {
      var infoProvidedCalled = false
      var expectedMessageReceived = false
      var lastReport: Report = null
      override def infoProvided(report: Report) {
        infoProvidedCalled = true
        if (!expectedMessageReceived) {
          expectedMessageReceived = report.message.indexOf(expectedMessage) != -1
        }
      }
    }

    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when not empty)") {
          it should "allow me to pop" given reporter in {
            reporter => {
              val report = new Report("myName", expectedMessage)
              reporter.infoProvided(report)
            }
          }
        }
        describe("(when not full)") {
          it should "allow me to push" in {}
        }
      }
    }
    val a = new MySpec
    val myRep = new MyReporter
    a.execute(None, myRep, new Stopper {}, Set(), Set(), Map(), None)
    assert(myRep.infoProvidedCalled)
    assert(myRep.expectedMessageReceived)
  }
 
  test("a shared example invoked with 'it should behave like' should get invoked") {
    class MySpec extends Spec with ImpSuite {
      var sharedExampleInvoked = false
      case class InvocationVerifier extends Behavior {
        it should "be invoked" in {
          sharedExampleInvoked = true
        }
      }
      describe("A Stack") {
        describe("(when not empty)") {
          it should "allow me to pop" in {}
          it should behave like { InvocationVerifier() }
        }
        describe("(when not full)") {
          it should "allow me to push" in {}
        }
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.sharedExampleInvoked)
  }
  
  test("two examples in a shared behavior should get invoked") {
    class MySpec extends Spec with ImpSuite {
      var sharedExampleInvoked = false
      var sharedExampleAlsoInvoked = false
      case class InvocationVerifier extends Behavior {
        it should "be invoked" in {
          sharedExampleInvoked = true
        }
        it should "also be invoked" in {
          sharedExampleAlsoInvoked = true
        }
      }
      describe("A Stack") {
        describe("(when not empty)") {
          it should "allow me to pop" in {}
          it should behave like { InvocationVerifier() }
        }
        describe("(when not full)") {
          it should "allow me to push" in {}
        }
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.sharedExampleInvoked)
  }

  test("three examples in a shared behavior should be invoked in order") {
    class MySpec extends Spec {
      var example1WasInvoked = false
      var example2WasInvokedAfterExample1 = false
      var example3WasInvokedAfterExample2 = false
      case class InvocationVerifier extends Behavior {
        it should "get invoked" in {
          example1WasInvoked = true
        }
        it should "also get invoked" in {
          if (example1WasInvoked)
            example2WasInvokedAfterExample1 = true
        }
        it should "also also get invoked" in {
          if (example2WasInvokedAfterExample1)
            example3WasInvokedAfterExample2 = true
        }
      }
      it should behave like { InvocationVerifier() }
    }
    val a = new MySpec
    a.execute()
    assert(a.example1WasInvoked)
    assert(a.example2WasInvokedAfterExample1)
    assert(a.example3WasInvokedAfterExample2)
  }
  
  test("three examples in a shared behavior should not get invoked at all if the behavior isn't used in a like clause") {
    class MySpec extends Spec {
      var example1WasInvoked = false
      var example2WasInvokedAfterExample1 = false
      var example3WasInvokedAfterExample2 = false
      case class InvocationVerifier extends Behavior {
        it should "get invoked" in {
          example1WasInvoked = true
        }
        it should "also get invoked" in {
          if (example1WasInvoked)
            example2WasInvokedAfterExample1 = true
        }
        it should "also also get invoked" in {
          if (example2WasInvokedAfterExample1)
            example3WasInvokedAfterExample2 = true
        }
      }
      // don't use it: it should behave like { InvocationVerifier() }
    }
    val a = new MySpec
    a.execute()
    assert(!a.example1WasInvoked)
    assert(!a.example2WasInvokedAfterExample1)
    assert(!a.example3WasInvokedAfterExample2)
  }
  
  test("In a testStarting report, the example name should start with 'it should' if top level") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("it should start with proper words") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      it should "start with proper words" in {}
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }
  
  test("In a testSucceeded report, the example name should start with 'it should' if top level") {
    var testSucceededReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testSucceeded(report: Report) {
        if (report.name.indexOf("it should start with proper words") != -1) {
          testSucceededReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      it should "start with proper words" in {}
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testSucceededReportHadCorrectTestName)
  }
  
  test("In a testFailed report, the example name should start with 'it should' if top level") {
    var testFailedReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testFailed(report: Report) {
        if (report.name.indexOf("it should start with proper words") != -1) {
          testFailedReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      it should "start with proper words" in { fail() }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testFailedReportHadCorrectTestName)
  }
  
  test("In a testStarting report, the example name should start with '<description> should' if nested one level inside a describe clause") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("A Stack should push and pop properly") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        it should "push and pop properly" in {}
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }
  
  test("In a testStarting report, the example name should start with '<description> should' if nested two levels inside describe clauses") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("A Stack (when working right) should push and pop properly") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when working right)") {
          it should "push and pop properly" in {}
        }
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }
   
  test("The example name for a shared example invoked with 'it should behave like' should start with 'it should' if top level") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("it should be invoked") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec with ImpSuite {
      var sharedExampleInvoked = false
      case class InvocationVerifier extends Behavior {
        it should "be invoked" in {
          sharedExampleInvoked = true
        }
      }
      it should behave like { InvocationVerifier() }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }
  
  ignore("The example name for a shared example invoked with 'it should behave like' should start with '<description> should' if nested one level in a describe clause") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("A Stack should pop properly") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
        println("*** name was: " + report.name)
      }
    }
    class MySpec extends Spec {
      var sharedExampleInvoked = false
      case class InvocationVerifier extends Behavior {
        it should "pop properly" in {
          sharedExampleInvoked = true
        }
      }
      describe("A Stack") {
        it should behave like { InvocationVerifier() }
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }
  
  test("expectedTestCount is the number of examples if no shares") {
    class MySpec extends Spec {
      it should "one" in {}
      it should "two" in {}
      describe("behavior") {
        it should "three" in {}  
        it should "four" in {}
      }
      it should "five" in {}
    }
    val a = new MySpec
    assert(a.expectedTestCount(Set(), Set()) === 5)
  }
  
  test("expectedTestCount should not include tests in shares if never called") {
    class MySpec extends Spec {
      class Misbehavior extends Spec {
        it should "six" in {}
        it should "seven" in {}
      }
      it should "one" in {}
      it should "two" in {}
      describe("behavior") {
        it should "three" in {}  
        it should "four" in {}
      }
      it should "five" in {}
    }
    val a = new MySpec
    assert(a.expectedTestCount(Set(), Set()) === 5)
  }

  test("expectedTestCount should include tests in a share that is called") {
    class MySpec extends Spec {
      case class Misbehavior extends Behavior {
        it should "six" in {}
        it should "seven" in {}
      }
      it should "one" in {}
      it should "two" in {}
      describe("behavior") {
        it should "three" in {} 
        it should behave like { Misbehavior() }
        it should "four" in {}
      }
      it should "five" in {}
    }
    val a = new MySpec
    assert(a.expectedTestCount(Set(), Set()) === 7)
  }

  test("expectedTestCount should include tests in a share that is called twice") {
    class MySpec extends Spec {
      case class Misbehavior extends Behavior {
        it should "six" in {}
        it should "seven" in {}
      }
      it should "one" in {}
      it should "two" in {}
      describe("behavior") {
        it should "three" in {} 
        it should behave like { Misbehavior() }
        it should "four" in {}
      }
      it should "five" in {}
      it should behave like { Misbehavior() }
    }
    val a = new MySpec
    assert(a.expectedTestCount(Set(), Set()) === 9)
  }
  
  test("should be able to say it should \"bla\" given[Stack]") {
    class MySpec extends Spec {
      import java.util.Date
      def runWithNowDate(testFunction: Date => Unit) = {
        testFunction(new Date)
      }
      def createNow = new Date
      it should "do something long-winded" in {
        runWithNowDate {
          date => ()
        }
      }
      it should "do something long-winded again" given reporter in {
        reporter => {
          runWithNowDate {
            date => ()
          }
        }
      }
      it should "do something long-winded again 2" in {
        val now = createNow
        // use it
        ()
      }
      it should "do something long-winded again 3" given reporter in {
        reporter => {
          val now = createNow
          // use it
          ()
        }
      }
    }
    ()
  }
  test("should be able to say 'specify' to register an example") {
    class MySpec extends Spec {
      var exampleWasExecuted = false
      specify("this is a spec") {
        exampleWasExecuted = true
      }
    }
    var a = new MySpec
    assert(!a.exampleWasExecuted)
    a.execute()
    assert(a.exampleWasExecuted)
  }
  
  test("In a testStarting report, the example name should be verbatim if top level if example registered with specify") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("this thing must start with proper words") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      specify("this thing must start with proper words") {}
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }

  test("In a testSucceeded report, the example name should be verbatim if top level if example registered with specify") {
    var testSucceededReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testSucceeded(report: Report) {
        if (report.name.indexOf("this thing must start with proper words") != -1) {
          testSucceededReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      specify("this thing must start with proper words") {}
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testSucceededReportHadCorrectTestName)
  }

  test("In a testFailed report, the example name should be verbatim if top level if example registered with specify") {
    var testFailedReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testFailed(report: Report) {
        if (report.name.indexOf("this thing must start with proper words") != -1) {
          testFailedReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      specify("this thing must start with proper words") { fail() }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testFailedReportHadCorrectTestName)
  }
  
  test("In a testStarting report, the example name should start with '<description> should' if nested one level " +
        "inside a describe clause and registered with specify") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("A Stack needs to push and pop properly") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
        println("*******##### " + report.name)
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        specify("needs to push and pop properly") {}
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }
    
  test("Specs should send SpecReports") {
    class MyReporter extends Reporter {
      var gotANonSpecReport = false
      var lastNonSpecReport: Option[Report] = None
  	  override def testStarting(report: Report) {
        ensureSpecReport(report)
  	  }

      private def ensureSpecReport(report: Report) {
	    report match {
          case sr: SpecReport => 
          case r: Report => {
            gotANonSpecReport = true
            lastNonSpecReport = Some(report)
          }
        }
      }
	  override def testSucceeded(report: Report) {
        ensureSpecReport(report)
	  }
	    
	  override def testIgnored(report: Report) {
        ensureSpecReport(report)
	  }
	
	  override def testFailed(report: Report) {
        ensureSpecReport(report)
	  }
	
	  override def infoProvided(report: Report) {
        ensureSpecReport(report)
	  }
	
	  override def suiteStarting(report: Report) {
        ensureSpecReport(report)
	  }
	
	  override def suiteCompleted(report: Report) {
        ensureSpecReport(report)
	  }
	
	  override def suiteAborted(report: Report) {
        ensureSpecReport(report)
	  }
	
	  override def runAborted(report: Report) {
        ensureSpecReport(report)
	  }
    }
    class MySpec extends Spec {
      it should "send SpecReports" in {
        assert(true)
      }
      it should "send SpecReports" in {
        assert(false)
      }
    }
    val a = new MySpec
    val myRep = new MyReporter
    a.execute(None, myRep, new Stopper {}, Set(), Set(), Map(), None)
    assert(!myRep.gotANonSpecReport)
  }
  
  test("Shortname should come through correctly in a SpecReport when registering with it should") {
    var testStartingReportHadCorrectShortName = false
    var lastShortName: Option[String] = None
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        report match {
          case specReport: SpecReport =>
            if (specReport.shortName == "it should start with proper words")
              testStartingReportHadCorrectShortName = true
            else
              lastShortName = Some(specReport.shortName)
          case _ => throw new RuntimeException("Got a non-SpecReport")
        }
      }
    }
    class MySpec extends Spec {
      it should "start with proper words" in {}
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectShortName, lastShortName match { case Some(s) => s; case None => "No report"})
  }

  test("Shortname should come through correctly in a SpecReport when registering with specify") {
    var testStartingReportHadCorrectShortName = false
    var lastShortName: Option[String] = None
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        report match {
          case specReport: SpecReport =>
            if (specReport.shortName == "My short name must have the proper words")
              testStartingReportHadCorrectShortName = true
            else
              lastShortName = Some(specReport.shortName)
          case _ => throw new RuntimeException("Got a non-SpecReport")
        }
      }
    }
    class MySpec extends Spec {
      specify("My short name must have the proper words") {}
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectShortName, lastShortName match { case Some(s) => s; case None => "No report"})
  }
   
  test("Shortname should come through correctly in a SpecReport when registering with it should when nested in one describe") {
    var testStartingReportHadCorrectShortName = false
    var lastShortName: Option[String] = None
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        report match {
          case specReport: SpecReport =>
            if (specReport.shortName == "should start with proper words")
              testStartingReportHadCorrectShortName = true
            else
              lastShortName = Some(specReport.shortName)
          case _ => throw new RuntimeException("Got a non-SpecReport")
        }
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        it should "start with proper words" in {}
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectShortName, lastShortName match { case Some(s) => s; case None => "No report"})
  }

  test("Shortname should come through correctly in a SpecReport when registering with specify when nested in one describe") {
    var testStartingReportHadCorrectShortName = false
    var lastShortName: Option[String] = None
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        report match {
          case specReport: SpecReport =>
            if (specReport.shortName == "My short name must have the proper words")
              testStartingReportHadCorrectShortName = true
            else
              lastShortName = Some(specReport.shortName)
          case _ => throw new RuntimeException("Got a non-SpecReport")
        }
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        specify("My short name must have the proper words") {}
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectShortName, lastShortName match { case Some(s) => s; case None => "No report"})
  }
   
  test("Shortname should come through correctly in a SpecReport when registering with it should when nested in two describes") {
    var testStartingReportHadCorrectShortName = false
    var lastShortName: Option[String] = None
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        report match {
          case specReport: SpecReport =>
            if (specReport.shortName == "should start with proper words")
              testStartingReportHadCorrectShortName = true
            else
              lastShortName = Some(specReport.shortName)
          case _ => throw new RuntimeException("Got a non-SpecReport")
        }
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when empty)") {
          it should "start with proper words" in {}
        }
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectShortName, lastShortName match { case Some(s) => s; case None => "No report"})
  }

  test("Shortname should come through correctly in a SpecReport when registering with specify when nested in two describes") {
    var testStartingReportHadCorrectShortName = false
    var lastShortName: Option[String] = None
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        report match {
          case specReport: SpecReport =>
            if (specReport.shortName == "My short name must have the proper words")
              testStartingReportHadCorrectShortName = true
            else
              lastShortName = Some(specReport.shortName)
          case _ => throw new RuntimeException("Got a non-SpecReport")
        }
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when empty)") {
          specify("My short name must have the proper words") {}
        }
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectShortName, lastShortName match { case Some(s) => s; case None => "No report"})
  }

  test("A specifyGivenReporter clause should be able to send info to the reporter") {

    val expectedMessage = "this is the expected message"

    class MyReporter extends Reporter {
      var infoProvidedCalled = false
      var expectedMessageReceived = false
      var lastReport: Report = null
      override def infoProvided(report: Report) {
        infoProvidedCalled = true
        if (!expectedMessageReceived) {
          expectedMessageReceived = report.message.indexOf(expectedMessage) != -1
        }
      }
    }

    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when not empty)") {
          specifyGivenReporter("might allow me to pop") {
            reporter => {
              val report = new Report("myName", expectedMessage)
              reporter.infoProvided(report)
            }
          }
        }
        describe("(when not full)") {
          specify("allow me to push") {}
        }
      }
    }
    
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
  }

  test("Should get infoProvided with description if one and only one describe clause") {

    val expectedShortName = "A Stack"

    class MyReporter extends Reporter {
      var infoProvidedCalled = false
      var expectedMessageReceived = false
      var lastReport: Report = null
      override def infoProvided(report: Report) {
        report match {
          case specReport: SpecReport => {
            infoProvidedCalled = true
            if (!expectedMessageReceived) {
              expectedMessageReceived = (specReport.shortName == expectedShortName)
            }
          }
          case _ =>
        }
      }
    }

    class MySpec extends Spec {
      describe("A Stack") {
        specify("should allow me to push") {}
      }
    }
    
    val a = new MySpec
    val myRep = new MyReporter
    a.execute(None, myRep, new Stopper {}, Set(), Set(), Map(), None)
    assert(myRep.infoProvidedCalled)
    assert(myRep.expectedMessageReceived)
  }
  
  test("That level gets sent correctly if no describe clauses.") {

    class MyReporter extends Reporter {

      val ExpectedLevel = 0
      var testSucceededCalled = false
      var testFailedCalled = false
      var expectedLevelReceivedByTestSucceeded = false
      var expectedLevelReceivedByTestFailed = false
 
      override def testSucceeded(report: Report) {
        report match {
          case specReport: SpecReport => {
            testSucceededCalled = true
            if (!expectedLevelReceivedByTestSucceeded) {
              expectedLevelReceivedByTestSucceeded = (specReport.level == ExpectedLevel)
            }
          }
          case _ =>
        }
      }
 
      override def testFailed(report: Report) {
        report match {
          case specReport: SpecReport => {
            testFailedCalled = true
            if (!expectedLevelReceivedByTestFailed) {
              expectedLevelReceivedByTestFailed = (specReport.level == ExpectedLevel)
            }
          }
          case _ =>
        }
      }
    }

    class MySpec extends Spec {
      it should "be at level 0" in {}
      it should "also be at level 0" in { fail() }
    }
    val a = new MySpec
    val myRep = new MyReporter
    a.execute(None, myRep, new Stopper {}, Set(), Set(), Map(), None)
    assert(myRep.testSucceededCalled)
    assert(myRep.testFailedCalled)
    assert(myRep.expectedLevelReceivedByTestSucceeded)
    assert(myRep.expectedLevelReceivedByTestFailed)
  }
  
  test("That level gets sent correctly if one and only one describe clause.") {

    class MyReporter extends Reporter {

      val ExpectedLevelForDescribe = 0
      val ExpectedLevelForExamples = 1
      var testSucceededCalled = false
      var testFailedCalled = false
      var infoProvidedCalled = false
      var expectedLevelReceivedByTestSucceeded = false
      var expectedLevelReceivedByTestFailed = false
      var expectedLevelReceivedByInfoProvided = false
 
      override def testSucceeded(report: Report) {
        report match {
          case specReport: SpecReport => {
            testSucceededCalled = true
            if (!expectedLevelReceivedByTestSucceeded) {
              expectedLevelReceivedByTestSucceeded = (specReport.level == ExpectedLevelForExamples)
            }
          }
          case _ =>
        }
      }
 
      override def testFailed(report: Report) {
        report match {
          case specReport: SpecReport => {
            testFailedCalled = true
            if (!expectedLevelReceivedByTestFailed) {
              expectedLevelReceivedByTestFailed = (specReport.level == ExpectedLevelForExamples)
            }
          }
          case _ =>
        }
      }
 
      override def infoProvided(report: Report) {
        report match {
          case specReport: SpecReport => {
            infoProvidedCalled = true
            if (!expectedLevelReceivedByInfoProvided) {
              expectedLevelReceivedByInfoProvided = (specReport.level == ExpectedLevelForDescribe)
            }
          }
          case _ =>
        }
      }
    }

    class MySpec extends Spec {
      describe("my describe clause") {
        it should "be at level 1" in {}
        it should "also be at level 1" in { fail() }
      }
    }
    val a = new MySpec
    val myRep = new MyReporter
    a.execute(None, myRep, new Stopper {}, Set(), Set(), Map(), None)
    assert(myRep.testSucceededCalled)
    assert(myRep.testFailedCalled)
    assert(myRep.infoProvidedCalled)
    assert(myRep.expectedLevelReceivedByTestSucceeded)
    assert(myRep.expectedLevelReceivedByTestFailed)
    assert(myRep.expectedLevelReceivedByInfoProvided)
  }
  /*
  test("In a testStarting report, the example name should start with '<description> should' if nested two levels inside describe clauses") {
    var testStartingReportHadCorrectTestName = false
    class MyReporter extends Reporter {
      override def testStarting(report: Report) {
        if (report.name.indexOf("A Stack (when working right) should push and pop properly") != -1) {
          testStartingReportHadCorrectTestName = true
        }  
      }
    }
    class MySpec extends Spec {
      describe("A Stack") {
        describe("(when working right)") {
          it should "push and pop properly" in {}
        }
      }
    }
    val a = new MySpec
    a.execute(None, new MyReporter, new Stopper {}, Set(), Set(), Map(), None)
    assert(testStartingReportHadCorrectTestName)
  }
*/
}


  /* 
  
  test("should throw an exception if they attempt to invoke a non-existent shared behavior") {
    class MySpec extends Spec {
      it should behave like "well-mannered children"
    }
    intercept(classOf[NoSuchElementException]) {
      new MySpec
    }
  }
  
  
  test("should throw an exception if they attempt to invoke a shared behavior with a typo") {
    class MySpec extends Spec {
      share("will-mannered children") {}
      it should behave like "well-mannered children"
    }
    intercept(classOf[NoSuchElementException]) {
      new MySpec
    }
  }

  test("should throw an exception if they attempt to invoke a shared behavior that's defined later") {
    class MySpec1 extends Spec {
      share("nice people") {}
      it should behave like "nice people" // this should work
    }
    class MySpec2 extends Spec {
      it should behave like "well-mannered children" // this should throw an exception
      share("well-mannered children") {}
    }
    new MySpec1
    intercept(classOf[NoSuchElementException]) {
      new MySpec2
    }
  }
  
  test("Should find and invoke shared behavior that's inside a describe and invoked inside a nested describe") {
    class MySpec extends Spec with ImpSuite {
      var sharedExampleInvoked = false
      describe("A Stack") {
        share("shared example") {
          it should "be invoked" in {
            sharedExampleInvoked = true
          }
        }
        before each {
          // set up fixture
        }
        describe("(when not empty)") {
          it should "allow me to pop" in {}
          it should behave like "shared example"
        }
        describe("(when not full)") {
          it should "allow me to push" in {}
        }
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.sharedExampleInvoked)
  }
  

  test("expectedTestCount should work when shares are nested") {
    class MySpec extends Spec {
      share("this") {
        it should "six" in {}
        it should "seven" in {}
        share("that") {
          it should "eight" in {}
          it should "nine" in {}
          it should "ten" in {}
        }
        it should behave like "that"
      }
      it should "one" in {}
      it should "two" in {}
      describe("behavior") {
        it should "three" in {} 
        it should behave like "this"
        it should "four" in {}
      }
      it should "five" in {}
      it should behave like "this"
    }
    val a = new MySpec
    assert(a.expectedTestCount(Set(), Set()) === 15)
  }
  
  test("Before each, after each, before all, and after all should all nest nicely") {
    class MySpec extends Spec {
      before all {}
      share("this") {
        before each {}
        it should "six" in {}
        after each {}
        it should "seven" in {}
        share("that") {
          it should "eight" in {}
          it should "nine" in {}
          it should "ten" in {}
          after each {}
          before each {}
        }
        it should behave like "that"
      }
      it should "one" in {}
      before each{}
      it should "two" in {}
      describe("behavior") {
        before each {}
        it should "three" in {} 
        it should behave like "this"
        it should "four" in {}
      }
      it should "five" in {}
      it should behave like "this"
      after each {}
      after all {}
    }
    new MySpec
  }
  
  test("a before each should run before an example") {
    class MySpec extends Spec {
      var exampleRan = false
      var beforeEachRanBeforeExample = false
      before each {
        if (!exampleRan)
          beforeEachRanBeforeExample = true
      }
      it should "run after example" in {
        exampleRan = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.beforeEachRanBeforeExample)
  }
  
  test("an 'after each' should run after an example") {
    class MySpec extends Spec {
      var exampleRan = false
      var afterEachRanAfterExample = false
      after each {
        if (exampleRan)
          afterEachRanAfterExample = true
      }
      it should "run after example" in {
        exampleRan = true
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.afterEachRanAfterExample)
  }

  test("If a test function throws an exception, after each should get invoked anyway") {
    class MySpec extends Spec {
      var afterEachRanAfterExample = false
      after each {
          afterEachRanAfterExample = true
      }
      it should "run after example" in {
        throw new RuntimeException
      }
    }
    val a = new MySpec
    a.execute()
    assert(a.afterEachRanAfterExample)
  }
  */

  /*
  test("I think I finally figured it out") {
    class MySpec extends Spec {

      // Fixtures
      def stackWithOneItem = {
        // ...
      }

      // Shared behavior
      case class NonEmptyStack(stack: Stack) extends Spec {
        it should "not be empty" in {
          assert(stack.size != 0)
        }
        it should "return the top item on a peek" in {
          // ...
        }
      }
      
      case class NonFullStack(stack: Stack) extends Spec {
        it should "not be full" in {
          assert(stack.size != 0)
        }
        it should "should add to the top on a push" in {
          // ...
        }
      }
      
      // The spec
      describe("A Stack") {
        describe("(with one item)") {}
          it should behave like { NonEmptyStack(stackWithOneItem) }
          it should behave like { NonFullStack(stackWithOneItem) }
        }
      }
    }
  }
  */

