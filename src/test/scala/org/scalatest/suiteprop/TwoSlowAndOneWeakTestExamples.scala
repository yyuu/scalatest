package org.scalatest.suiteprop

import org.scalatest._

class TwoSlowAndOneWeakTestExamples extends SuiteExamples {

  trait Services {
    val theTestNames = Vector("first test", "second test")
  }

  trait NestedTestNames extends Services {
    override val theTestNames = Vector("A subject should first test", "A subject should second test")
  }

  trait DeeplyNestedTestNames extends Services {
    override val theTestNames = Vector("A subject when created should first test", "A subject when created should second test")
  }

  type FixtureServices = Services

  class SuiteExample extends Suite with Services {
    @SlowAsMolasses @WeakAsAKitten def testFirst {}
    @SlowAsMolasses def testSecond {}
    override val theTestNames = Vector("testFirst", "testSecond")
  }

  class FixtureSuiteExample extends StringFixtureSuite with Services {
    @SlowAsMolasses @WeakAsAKitten def testFirst(s: String) {}
    @SlowAsMolasses def testSecond(s: String) {}
    override val theTestNames = Vector("testFirst(FixtureParam)", "testSecond(FixtureParam)")
  }

  class FunSuiteExample extends FunSuite with Services {
    test("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
    test("second test", mytags.SlowAsMolasses) {}
  }

  class FixtureFunSuiteExample extends StringFixtureFunSuite with Services {
    test("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { s => }
    test("second test", mytags.SlowAsMolasses) { s => }
  }

  class FunSpecExample extends FunSpec with Services {
    it("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
    it("second test", mytags.SlowAsMolasses) {}
  }

  class NestedFunSpecExample extends FunSpec with NestedTestNames {
    describe("A subject") {
      it("should first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
      it("should second test", mytags.SlowAsMolasses) {}
    }
  }

  class DeeplyNestedFunSpecExample extends FunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        it("should first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
        it("should second test", mytags.SlowAsMolasses) {}
      }
    }
  }

  class FixtureFunSpecExample extends StringFixtureFunSpec with Services {
      it("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { s => }
      it("second test", mytags.SlowAsMolasses) { s => }
  }
  
  class NestedFixtureFunSpecExample extends StringFixtureFunSpec with NestedTestNames {
    describe("A subject") {
      it("should first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { s => }
      it("should second test", mytags.SlowAsMolasses) { s => }
    }
  }

  class DeeplyNestedFixtureFunSpecExample extends StringFixtureFunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        it("should first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { s => }
        it("should second test", mytags.SlowAsMolasses) { s => }
      }
    }
  }

  class PathFunSpecExample extends path.FunSpec with Services {
    it("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
    it("second test", mytags.SlowAsMolasses) {}
    override def newInstance = new PathFunSpecExample
  }

  class NestedPathFunSpecExample extends path.FunSpec with NestedTestNames {
    describe("A subject") {
      it("should first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
      it("should second test", mytags.SlowAsMolasses) {}
    }
    override def newInstance = new NestedPathFunSpecExample
  }

  class DeeplyNestedPathFunSpecExample extends path.FunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        it("should first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
        it("should second test", mytags.SlowAsMolasses) {}
      }
    }
    override def newInstance = new DeeplyNestedPathFunSpecExample
  }

  class WordSpecExample extends WordSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
    "second test" taggedAs (mytags.SlowAsMolasses) in {}
  }

  class NestedWordSpecExample extends WordSpec with NestedTestNames {
    "A subject" should {
      "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
      "second test" taggedAs (mytags.SlowAsMolasses) in {}
    }
  }

  class DeeplyNestedWordSpecExample extends WordSpec with DeeplyNestedTestNames {
    "A subject" when {
      "created" should {
        "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
        "second test" taggedAs (mytags.SlowAsMolasses) in {}
      }
    }
  }

  class FixtureWordSpecExample extends StringFixtureWordSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
    "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
  }

  class NestedFixtureWordSpecExample extends StringFixtureWordSpec with NestedTestNames {
    "A subject" should {
      "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
      "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
    }
  }

  class DeeplyNestedFixtureWordSpecExample extends StringFixtureWordSpec with DeeplyNestedTestNames {
    "A subject" when {
      "created" should {
        "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
        "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
      }
    }
  }

  class FlatSpecExample extends FlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
    it should "second test" taggedAs (mytags.SlowAsMolasses) in {}
    override val theTestNames = Vector("should first test", "should second test")
   }

  class SubjectFlatSpecExample extends FlatSpec with NestedTestNames {
    behavior of "A subject"
    it should "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
    it should "second test" taggedAs (mytags.SlowAsMolasses) in {}
   }

  class ShorthandSubjectFlatSpecExample extends FlatSpec with NestedTestNames {
    "A subject" should "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
    it should "second test" taggedAs (mytags.SlowAsMolasses) in {}
   }

  class FixtureFlatSpecExample extends StringFixtureFlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
    it should "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
    override val theTestNames = Vector("should first test", "should second test")
  }

  class SubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestNames {
    behavior of "A subject"
    it should "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
    it should "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
   }

  class ShorthandSubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestNames {
    "A subject" should "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
    it should "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
   }

  class FreeSpecExample extends FreeSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
    "second test" taggedAs (mytags.SlowAsMolasses) in {}
  }

  class NestedFreeSpecExample extends FreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
      "should second test" taggedAs (mytags.SlowAsMolasses) in {}
    }
  }

  class DeeplyNestedFreeSpecExample extends FreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
        "should second test" taggedAs (mytags.SlowAsMolasses) in {}
      }
    }
  }

  class FixtureFreeSpecExample extends StringFixtureFreeSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
    "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
  }

  class NestedFixtureFreeSpecExample extends StringFixtureFreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
      "should second test" taggedAs (mytags.SlowAsMolasses) in { s => }
    }
  }

  class DeeplyNestedFixtureFreeSpecExample extends StringFixtureFreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { s => }
        "should second test" taggedAs (mytags.SlowAsMolasses) in { s => }
      }
    }
  }

  class PathFreeSpecExample extends path.FreeSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
    "second test" taggedAs (mytags.SlowAsMolasses) in {}
    override def newInstance = new PathFreeSpecExample
  }

  class NestedPathFreeSpecExample extends path.FreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
      "should second test" taggedAs (mytags.SlowAsMolasses) in {}
    }
    override def newInstance = new NestedPathFreeSpecExample
  }

  class DeeplyNestedPathFreeSpecExample extends path.FreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" taggedAs (mytags.SlowAsMolasses, mytags.WeakAsAKitten) in {}
        "should second test" taggedAs (mytags.SlowAsMolasses) in {}
      }
    }
    override def newInstance = new DeeplyNestedPathFreeSpecExample
  }

  class FeatureSpecExample extends FeatureSpec with Services {
    scenario("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
    scenario("second test", mytags.SlowAsMolasses) {}
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  class NestedFeatureSpecExample extends FeatureSpec with Services {
    feature("A feature") {
      scenario("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
      scenario("second test", mytags.SlowAsMolasses) {}
    }
    override val theTestNames = Vector("A feature Scenario: first test", "A feature Scenario: second test")
  }

  class FixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    scenario("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { s => }
    scenario("second test", mytags.SlowAsMolasses) { s => }
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  class NestedFixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    feature("A feature") {
      scenario("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { s => }
      scenario("second test", mytags.SlowAsMolasses) { s => }
    }
    override val theTestNames = Vector("A feature Scenario: first test", "A feature Scenario: second test")
  }

  class PropSpecExample extends PropSpec with Services {
    property("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) {}
    property("second test", mytags.SlowAsMolasses) {}
  }

  class FixturePropSpecExample extends StringFixturePropSpec with Services {
    property("first test", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { s => }
    property("second test", mytags.SlowAsMolasses) { s => }
  }

  lazy val suite = new SuiteExample
  lazy val fixtureSuite = new FixtureSuiteExample
  lazy val funSuite = new FunSuiteExample
  lazy val fixtureFunSuite = new FixtureFunSuiteExample
  lazy val funSpec = new FunSpecExample
  lazy val nestedFunSpec = new NestedFunSpecExample
  lazy val deeplyNestedFunSpec = new DeeplyNestedFunSpecExample
  lazy val fixtureFunSpec = new FixtureFunSpecExample
  lazy val nestedFixtureFunSpec = new NestedFixtureFunSpecExample
  lazy val deeplyNestedFixtureFunSpec = new DeeplyNestedFixtureFunSpecExample
  lazy val pathFunSpec = new PathFunSpecExample
  lazy val nestedPathFunSpec = new NestedPathFunSpecExample
  lazy val deeplyNestedPathFunSpec = new DeeplyNestedPathFunSpecExample
  lazy val wordSpec = new WordSpecExample
  lazy val nestedWordSpec = new NestedWordSpecExample
  lazy val deeplyNestedWordSpec = new DeeplyNestedWordSpecExample
  lazy val fixtureWordSpec = new FixtureWordSpecExample
  lazy val nestedFixtureWordSpec = new NestedFixtureWordSpecExample
  lazy val deeplyNestedFixtureWordSpec = new DeeplyNestedFixtureWordSpecExample
  lazy val flatSpec = new FlatSpecExample
  lazy val subjectFlatSpec = new SubjectFlatSpecExample
  lazy val shorthandSubjectFlatSpec = new ShorthandSubjectFlatSpecExample
  lazy val fixtureFlatSpec = new FixtureFlatSpecExample
  lazy val subjectFixtureFlatSpec = new SubjectFixtureFlatSpecExample
  lazy val shorthandSubjectFixtureFlatSpec = new ShorthandSubjectFixtureFlatSpecExample
  lazy val freeSpec = new FreeSpecExample
  lazy val nestedFreeSpec = new NestedFreeSpecExample
  lazy val deeplyNestedFreeSpec = new DeeplyNestedFreeSpecExample
  lazy val fixtureFreeSpec = new FixtureFreeSpecExample
  lazy val nestedFixtureFreeSpec = new NestedFixtureFreeSpecExample
  lazy val deeplyNestedFixtureFreeSpec = new DeeplyNestedFixtureFreeSpecExample
  lazy val pathFreeSpec = new PathFreeSpecExample
  lazy val nestedPathFreeSpec = new NestedPathFreeSpecExample
  lazy val deeplyNestedPathFreeSpec = new DeeplyNestedPathFreeSpecExample
  lazy val featureSpec = new FeatureSpecExample
  lazy val nestedFeatureSpec = new NestedFeatureSpecExample
  lazy val fixtureFeatureSpec = new FixtureFeatureSpecExample
  lazy val nestedFixtureFeatureSpec = new NestedFixtureFeatureSpecExample
  lazy val propSpec = new PropSpecExample
  lazy val fixturePropSpec = new FixturePropSpecExample
 /*  
  // Two ways to ignore in a flat spec, so add two more examples
  override def examples = super.examples ++ List(new FlatSpecExample2, new FixtureFlatSpecExample2)

  class FlatSpecExample2 extends FlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it should "second test" taggedAs (mytags.SlowAsMolasses) in {}
    override val theTestNames = Vector("should first test", "should second test")
   }

  class FixtureFlatSpecExample2 extends StringFixtureFlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it should "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
    override val theTestNames = Vector("should first test", "should second test")
  }*/
}