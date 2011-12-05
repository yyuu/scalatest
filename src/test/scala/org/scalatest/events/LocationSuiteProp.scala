package org.scalatest.events
import org.scalatest.Suite
import org.scalatest.SuiteProp
import org.scalatest.FunSuite
import org.scalatest.Spec
import org.scalatest.StringFixtureSpec
import org.scalatest.StringFixtureFunSuite
import org.scalatest.Stopper
import org.scalatest.Filter
import org.scalatest.Tracker
import org.scalatest.Reporter
import org.scalatest.Distributor
import org.scalatest.FeatureSpec
import org.scalatest.fixture.FixtureFeatureSpec
import org.scalatest.junit.JUnit3Suite
import org.scalatest.FlatSpec
import org.scalatest.fixture.FixtureFlatSpec
import org.scalatest.FreeSpec
import org.scalatest.fixture.FixtureFreeSpec
import org.scalatest.PropSpec
import org.scalatest.fixture.FixturePropSpec
import org.scalatest.WordSpec
import org.scalatest.fixture.FixtureWordSpec

class LocationSuiteProp extends SuiteProp 
{
  test("All suite types should have correct location in SuiteStarting, SuiteCompleted, SuiteAborted and TestFailed event.") {
    forAll(examples) { suite =>
      val reporter = new EventRecordingReporter
      suite.run(None, reporter, new Stopper {}, Filter(), Map(), None, new Tracker(new Ordinal(99)))
      val eventList = reporter.eventsReceived
      eventList.foreach { event => suite.checkFun(event) }
      suite.allChecked
    }
  }
  
  type FixtureServices = TestLocationServices
  
  def suite = new TestLocationSuite
  class TestLocationSuite extends Suite with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationSuite"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("testFail"))
    
    class NestedSuite extends Suite
    class AbortNestedSuite extends Suite {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends Suite {
      def testFail() { fail }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def junit3Suite = new TestLocationJUnit3Suite
  
  def junitSuite = new TestLocationJUnitSuite
  
  def testngSuite = new TestLocationTestNGSuite
  
  def funSuite = new TestLocationFunSuite
  class TestLocationFunSuite extends FunSuite with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFunSuite"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("fail"))
    
    class NestedSuite extends FunSuite
    class AbortNestedSuite extends FunSuite {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends FunSuite {
      test("fail") { fail }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def fixtureFunSuite = new TestLocationFixtureFunSuite
  class TestLocationFixtureFunSuite extends StringFixtureFunSuite with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFixtureFunSuite"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("fail"))
    
    class NestedSuite extends StringFixtureFunSuite
    class AbortNestedSuite extends StringFixtureFunSuite {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends StringFixtureFunSuite {
      test("fail") { fail }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  } 
  
  def spec = new LocationTestSpec
  class LocationTestSpec extends Spec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$LocationTestSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("fail"))
    
    class NestedSuite extends Spec
    class AbortNestedSuite extends Spec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends Spec {
      it("fail") { fail }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def fixtureSpec = new TestLocationFixtureSpec
  class TestLocationFixtureSpec extends StringFixtureSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFixtureSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("fail"))
    
    class NestedSuite extends StringFixtureSpec
    class AbortNestedSuite extends StringFixtureSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends Spec {
      it("fail") { fail }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def featureSpec = new TestLocationFeatureSpec
  class TestLocationFeatureSpec extends FeatureSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFeatureSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("feature Scenario: fail"))
    
    class NestedSuite extends FeatureSpec
    class AbortNestedSuite extends FeatureSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends FeatureSpec {
      feature("feature") { 
        scenario("fail") {
          fail
        }
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def fixtureFeatureSpec = new TestLocationFixtureFeatureSpec
  class UnitFixtureFeatureSpec extends FixtureFeatureSpec {
    type FixtureParam = Unit 
    def withFixture(test: OneArgTest) { }
  }
  class TestLocationFixtureFeatureSpec extends UnitFixtureFeatureSpec with FixtureServices { 
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFixtureFeatureSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("feature Scenario: fail"))
    
    class NestedSuite extends UnitFixtureFeatureSpec
    class AbortNestedSuite extends UnitFixtureFeatureSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends UnitFixtureFeatureSpec {
      feature("feature") { 
        scenario("fail") {
          fail
        }
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def flatSpec = new TestLocationFlatSpec
  class TestLocationFlatSpec extends FlatSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFlatSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends FlatSpec
    class AbortNestedSuite extends FlatSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends FlatSpec {
      "Test" should "fail" in {
        fail
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def fixtureFlatSpec = new TestLocationFixtureFlatSpec
  class StringFixtureFlatSpec extends FixtureFlatSpec {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
  }
  class TestLocationFixtureFlatSpec extends StringFixtureFlatSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFixtureFlatSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends StringFixtureFlatSpec
    class AbortNestedSuite extends StringFixtureFlatSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends StringFixtureFlatSpec { 
      "Test" should "fail" in { param =>
        fail
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def freeSpec = new TestLocationFreeSpec
  class TestLocationFreeSpec extends FreeSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFreeSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends FreeSpec
    class AbortNestedSuite extends FreeSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends FreeSpec { 
      "Test" - {
        "should fail" in {
          fail
        }
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def fixtureFreeSpec = new TestLocationFixtureFreeSpec
  class StringFixtureFreeSpec extends FixtureFreeSpec {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
  }
  class TestLocationFixtureFreeSpec extends StringFixtureFreeSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFixtureFreeSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends StringFixtureFreeSpec
    class AbortNestedSuite extends StringFixtureFreeSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends StringFixtureFreeSpec { 
      "Test" - {
        "should fail" in { param =>
          fail
        }
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def propSpec = new TestLocationPropSpec
  class TestLocationPropSpec extends PropSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationPropSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends PropSpec
    class AbortNestedSuite extends PropSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends PropSpec { 
      property("Test should fail") {
        fail
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def fixturePropSpec = new TestLocationFixturePropSpec
  class StringFixturePropSpec extends FixturePropSpec {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
  }
  class TestLocationFixturePropSpec extends StringFixturePropSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFixturePropSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends StringFixturePropSpec
    class AbortNestedSuite extends StringFixturePropSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends StringFixturePropSpec { 
      property("Test should fail") { param =>
        fail
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def wordSpec = new TestLocationWordSpec
  class TestLocationWordSpec extends WordSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationWordSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends WordSpec
    class AbortNestedSuite extends WordSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends WordSpec { 
      "Test" should {
        "fail" in {
          fail
        }
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
  
  def fixtureWordSpec = new TestLocationFixtureWordSpec
  class StringFixtureWordSpec extends FixtureWordSpec {
    type FixtureParam = String
    def withFixture(test: OneArgTest) { test("") }
  }
  class TestLocationFixtureWordSpec extends StringFixtureWordSpec with FixtureServices {
    val suiteTypeName = "org.scalatest.events.LocationSuiteProp$TestLocationFixtureWordSpec"
    val expectedSuiteStartingList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"), 
                                         TopOfClassPair(suiteTypeName + "$AbortNestedSuite"),
                                         TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteCompletedList = List(TopOfClassPair(suiteTypeName + "$NestedSuite"),
                                          TopOfClassPair(suiteTypeName + "$FailNestedSuite"))
    val expectedSuiteAbortedList = List(SeeStackDepthExceptionPair(suiteTypeName + "$AbortNestedSuite"))
    val expectedTestFailedList = List(SeeStackDepthExceptionPair("Test should fail"))
    
    class NestedSuite extends StringFixtureWordSpec
    class AbortNestedSuite extends StringFixtureWordSpec {
      override protected def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
        throw new RuntimeException
      }
    }
    class FailNestedSuite extends StringFixtureWordSpec { 
      "Test" should {
        "fail" in { param =>
          fail
        }
      }
    }
    override def nestedSuites = List(new NestedSuite, new AbortNestedSuite, new FailNestedSuite)
  }
}