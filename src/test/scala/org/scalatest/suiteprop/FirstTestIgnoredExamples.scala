package org.scalatest.suiteprop

import org.scalatest._

class FirstTestIgnoredExamples extends SuiteExamples {

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
    @Ignore def testFirst {}
    def testSecond {}
    override val theTestNames = Vector("testFirst", "testSecond")
  }

  class FixtureSuiteExample extends StringFixtureSuite with Services {
    @Ignore def testFirst(s: String) {}
    def testSecond(s: String) {}
    override val theTestNames = Vector("testFirst(FixtureParam)", "testSecond(FixtureParam)")
  }

  class FunSuiteExample extends FunSuite with Services {
    ignore("first test") {}
    test("second test") {}
  }

  class FixtureFunSuiteExample extends StringFixtureFunSuite with Services {
    ignore("first test") { s => }
    test("second test") { s => }
  }

  class FunSpecExample extends FunSpec with Services {
    ignore("first test") {}
    it("second test") {}
  }

  class NestedFunSpecExample extends FunSpec with NestedTestNames {
    describe("A subject") {
      ignore("should first test") {}
      it("should second test") {}
    }
  }

  class DeeplyNestedFunSpecExample extends FunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        ignore("should first test") {}
        it("should second test") {}
      }
    }
  }

  class FixtureFunSpecExample extends StringFixtureFunSpec with Services {
      ignore("first test") { s => }
      it("second test") { s => }
  }
  
  class NestedFixtureFunSpecExample extends StringFixtureFunSpec with NestedTestNames {
    describe("A subject") {
      ignore("should first test") { s => }
      it("should second test") { s => }
    }
  }

  class DeeplyNestedFixtureFunSpecExample extends StringFixtureFunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        ignore("should first test") { s => }
        it("should second test") { s => }
      }
    }
  }

  class PathFunSpecExample extends path.FunSpec with Services {
    ignore("first test") {}
    it("second test") {}
  }

  class NestedPathFunSpecExample extends path.FunSpec with NestedTestNames {
    describe("A subject") {
      ignore("should first test") {}
      it("should second test") {}
    }
  }

  class DeeplyNestedPathFunSpecExample extends path.FunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        ignore("should first test") {}
        it("should second test") {}
      }
    }
  }

  class WordSpecExample extends WordSpec with Services {
    "first test" ignore {}
    "second test" in {}
  }

  class NestedWordSpecExample extends WordSpec with NestedTestNames {
    "A subject" should {
      "first test" ignore {}
      "second test" in {}
    }
  }

  class DeeplyNestedWordSpecExample extends WordSpec with DeeplyNestedTestNames {
    "A subject" when {
      "created" should {
        "first test" ignore {}
        "second test" in {}
      }
    }
  }

  class FixtureWordSpecExample extends StringFixtureWordSpec with Services {
    "first test" ignore { s => }
    "second test" in { s => }
  }

  class NestedFixtureWordSpecExample extends StringFixtureWordSpec with NestedTestNames {
    "A subject" should {
      "first test" ignore { s => }
      "second test" in { s => }
    }
  }

  class DeeplyNestedFixtureWordSpecExample extends StringFixtureWordSpec with DeeplyNestedTestNames {
    "A subject" when {
      "created" should {
        "first test" ignore { s => }
        "second test" in { s => }
      }
    }
  }

  class FlatSpecExample extends FlatSpec with Services {
    it should "first test" ignore {}
    it should "second test" in {}
    override val theTestNames = Vector("should first test", "should second test")
   }

  class SubjectFlatSpecExample extends FlatSpec with NestedTestNames {
    behavior of "A subject"
    it should "first test" ignore {}
    it should "second test" in {}
   }

  class ShorthandSubjectFlatSpecExample extends FlatSpec with NestedTestNames {
    "A subject" should "first test" ignore {}
    it should "second test" in {}
   }

  class FixtureFlatSpecExample extends StringFixtureFlatSpec with Services {
    it should "first test" ignore { s => }
    it should "second test" in { s => }
    override val theTestNames = Vector("should first test", "should second test")
  }

  class SubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestNames {
    behavior of "A subject"
    it should "first test" ignore { s => }
    it should "second test" in { s => }
   }

  class ShorthandSubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestNames {
    "A subject" should "first test" ignore { s => }
    it should "second test" in { s => }
   }

  class FreeSpecExample extends FreeSpec with Services {
    "first test" ignore {}
    "second test" in {}
  }

  class NestedFreeSpecExample extends FreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" ignore {}
      "should second test" in {}
    }
  }

  class DeeplyNestedFreeSpecExample extends FreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" ignore {}
        "should second test" in {}
      }
    }
  }

  class FixtureFreeSpecExample extends StringFixtureFreeSpec with Services {
    "first test" ignore { s => }
    "second test" in { s => }
  }

  class NestedFixtureFreeSpecExample extends StringFixtureFreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" ignore { s => }
      "should second test" in { s => }
    }
  }

  class DeeplyNestedFixtureFreeSpecExample extends StringFixtureFreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" ignore { s => }
        "should second test" in { s => }
      }
    }
  }

  class FeatureSpecExample extends FeatureSpec with Services {
    ignore("first test") {}
    scenario("second test") {}
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  class NestedFeatureSpecExample extends FeatureSpec with Services {
    feature("A feature") {
      ignore("first test") {}
      scenario("second test") {}
    }
    override val theTestNames = Vector("A feature Scenario: first test", "A feature Scenario: second test")
  }

  class FixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    ignore("first test") { s => }
    scenario("second test") { s => }
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  class NestedFixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    feature("A feature") {
      ignore("first test") { s => }
      scenario("second test") { s => }
    }
    override val theTestNames = Vector("A feature Scenario: first test", "A feature Scenario: second test")
  }

  class PropSpecExample extends PropSpec with Services {
    ignore("first test") {}
    property("second test") {}
  }

  class FixturePropSpecExample extends StringFixturePropSpec with Services {
    ignore("first test") { s => }
    property("second test") { s => }
  }

  def suite = new SuiteExample
  def fixtureSuite = new FixtureSuiteExample
  def funSuite = new FunSuiteExample
  def fixtureFunSuite = new FixtureFunSuiteExample
  def funSpec = new FunSpecExample
  def nestedFunSpec = new NestedFunSpecExample
  def deeplyNestedFunSpec = new DeeplyNestedFunSpecExample
  def fixtureFunSpec = new FixtureFunSpecExample
  def nestedFixtureFunSpec = new NestedFixtureFunSpecExample
  def deeplyNestedFixtureFunSpec = new DeeplyNestedFixtureFunSpecExample
  def pathFunSpec = new PathFunSpecExample
  def nestedPathFunSpec = new NestedPathFunSpecExample
  def deeplyNestedPathFunSpec = new DeeplyNestedPathFunSpecExample
  def wordSpec = new WordSpecExample
  def nestedWordSpec = new NestedWordSpecExample
  def deeplyNestedWordSpec = new DeeplyNestedWordSpecExample
  def fixtureWordSpec = new FixtureWordSpecExample
  def nestedFixtureWordSpec = new NestedFixtureWordSpecExample
  def deeplyNestedFixtureWordSpec = new DeeplyNestedFixtureWordSpecExample
  def flatSpec = new FlatSpecExample
  def subjectFlatSpec = new SubjectFlatSpecExample
  def shorthandSubjectFlatSpec = new ShorthandSubjectFlatSpecExample
  def fixtureFlatSpec = new FixtureFlatSpecExample
  def subjectFixtureFlatSpec = new SubjectFixtureFlatSpecExample
  def shorthandSubjectFixtureFlatSpec = new ShorthandSubjectFixtureFlatSpecExample
  def freeSpec = new FreeSpecExample
  def nestedFreeSpec = new NestedFreeSpecExample
  def deeplyNestedFreeSpec = new DeeplyNestedFreeSpecExample
  def fixtureFreeSpec = new FixtureFreeSpecExample
  def nestedFixtureFreeSpec = new NestedFixtureFreeSpecExample
  def deeplyNestedFixtureFreeSpec = new DeeplyNestedFixtureFreeSpecExample
  def featureSpec = new FeatureSpecExample
  def nestedFeatureSpec = new NestedFeatureSpecExample
  def fixtureFeatureSpec = new FixtureFeatureSpecExample
  def nestedFixtureFeatureSpec = new NestedFixtureFeatureSpecExample
  def propSpec = new PropSpecExample
  def fixturePropSpec = new FixturePropSpecExample
   
  // Two ways to ignore in a flat spec, so add two more examples
  override def examples = super.examples ++ List(new FlatSpecExample2, new FixtureFlatSpecExample2)

  class FlatSpecExample2 extends FlatSpec with Services {
    ignore should "first test" in {}
    it should "second test" in {}
    override val theTestNames = Vector("should first test", "should second test")
   }

  class FixtureFlatSpecExample2 extends StringFixtureFlatSpec with Services {
    ignore should "first test" in { s => }
    it should "second test" in { s => }
    override val theTestNames = Vector("should first test", "should second test")
  }
}