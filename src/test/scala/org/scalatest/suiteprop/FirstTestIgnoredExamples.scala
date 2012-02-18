package org.scalatest.suiteprop

import org.scalatest._

class FirstTestIgnoredExamples extends SuiteExamples {

  trait Services {
    val theTestNames = Vector("first test", "second test")
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

  class FixtureFunSpecExample extends StringFixtureFunSpec with Services {
      ignore("first test") { s => }
      it("second test") { s => }
  }
  
  class PathFunSpecExample extends path.FunSpec with Services {
    ignore("first test") {}
    it("second test") {}
  }

  class WordSpecExample extends WordSpec with Services {
    "first test" ignore {}
    "second test" in {}
  }

  class FixtureWordSpecExample extends StringFixtureWordSpec with Services {
    "first test" ignore { s => }
    "second test" in { s => }
  }

  class FlatSpecExample extends FlatSpec with Services {
    it should "first test" ignore {}
    it should "second test" in {}
    override val theTestNames = Vector("should first test", "should second test")
   }

  class FixtureFlatSpecExample extends StringFixtureFlatSpec with Services {
    it should "first test" ignore { s => }
    it should "second test" in { s => }
    override val theTestNames = Vector("should first test", "should second test")
  }

  class FreeSpecExample extends FreeSpec with Services {
    "first test" ignore {}
    "second test" in {}
  }

  class FixtureFreeSpecExample extends StringFixtureFreeSpec with Services {
    "first test" ignore { s => }
    "second test" in { s => }
  }

  class FeatureSpecExample extends FeatureSpec with Services {
    ignore("first test") {}
    scenario("second test") {}
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  class FixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    ignore("first test") { s => }
    scenario("second test") { s => }
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  def suite = new SuiteExample
  def fixtureSuite = new FixtureSuiteExample
  def funSuite = new FunSuiteExample
  def fixtureFunSuite = new FixtureFunSuiteExample
  def funSpec = new FunSpecExample
  def fixtureFunSpec = new FixtureFunSpecExample
  def pathFunSpec = new PathFunSpecExample
  def wordSpec = new WordSpecExample
  def fixtureWordSpec = new FixtureWordSpecExample
  def flatSpec = new FlatSpecExample
  def fixtureFlatSpec = new FixtureFlatSpecExample
  def freeSpec = new FreeSpecExample
  def fixtureFreeSpec = new FixtureFreeSpecExample
  def featureSpec = new FeatureSpecExample
  def fixtureFeatureSpec = new FixtureFeatureSpecExample
 
  // Two ways to ignore in a flat spec, so add two more examples
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
  
  override def examples = super.examples ++ List(new FlatSpecExample2, new FixtureFlatSpecExample2)
}