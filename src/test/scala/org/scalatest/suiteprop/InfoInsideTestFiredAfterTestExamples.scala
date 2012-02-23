/*
 * Copyright 2001-2011 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.suiteprop

import org.scalatest._
import prop.TableDrivenPropertyChecks

class InfoInsideTestFiredAfterTestExamples extends SuiteExamples {

  trait Services {
    val msg = "hi there, dude"
    val theTestName = "test name"
  }

  trait NestedTestName extends Services {
    override val theTestName = "A subject should test name"
  }

  trait DeeplyNestedTestName extends Services {
    override val theTestName = "A subject when created should test name"
  }

  type FixtureServices = Services

  class SuiteExample extends Suite with Services  {
    def testMethod(info: Informer) {
      info(msg)
    }
    override val theTestName = "testMethod(Informer)"
  }
  
  class FixtureSuiteExample extends StringFixtureSuite with Services {
    def testMethod(s: String, info: Informer) {
      info(msg)
    }
    override val theTestName = "testMethod(FixtureParam, Informer)"
  }

  class FunSuiteExample extends FunSuite with Services {
    test(theTestName) {
      info(msg)
    }
  }

  class FixtureFunSuiteExample extends StringFixtureFunSuite with Services {
    test(theTestName) { s =>
      info(msg)
    }
  }

  class FunSpecExample extends FunSpec with Services {
    it(theTestName) {
      info(msg)
    }
  }

  class NestedFunSpecExample extends FunSpec with NestedTestName {
    describe("A subject") {
      it("should test name") {
        info(msg)
      }
    }
  }

  class DeeplyNestedFunSpecExample extends FunSpec with DeeplyNestedTestName {
    describe("A subject") {
      describe("when created") {
        it("should test name") {
          info(msg)
        }
      }
    }
  }

  class FixtureFunSpecExample extends StringFixtureFunSpec with Services {
    it(theTestName) { s =>
      info(msg)
    }
  }
  
  class NestedFixtureFunSpecExample extends StringFixtureFunSpec with NestedTestName {
    describe("A subject") {
      it("should test name") { s =>
        info(msg)
      }
    }
  }

  class DeeplyNestedFixtureFunSpecExample extends StringFixtureFunSpec with DeeplyNestedTestName {
    describe("A subject") {
      describe("when created") {
        it("should test name") { s =>
          info(msg)
        }
      }
    }
  }

  class PathFunSpecExample extends path.FunSpec with Services {
    it(theTestName) {
      info(msg)
    }
  }
    
  class NestedPathFunSpecExample extends path.FunSpec with NestedTestName {
    describe("A subject") {
      it("should test name") {
        info(msg)
      }
    }
  }

  class DeeplyNestedPathFunSpecExample extends path.FunSpec with DeeplyNestedTestName {
    describe("A subject") {
      describe("when created") {
        it("should test name") {
          info(msg)
        }
      }
    }
  }

  class WordSpecExample extends WordSpec with Services {
    theTestName in {
      info(msg)
    }
  }

  class NestedWordSpecExample extends WordSpec with NestedTestName {
    "A subject" should {
      "test name" in {
        info(msg)
      }
    }
  }

  class DeeplyNestedWordSpecExample extends WordSpec with DeeplyNestedTestName {
    "A subject" when {
      "created" should {
        "test name" in {
          info(msg)
        }
      }
    }
  }

  class FixtureWordSpecExample extends StringFixtureWordSpec with Services {
    theTestName in { s =>
      info(msg)
    }
  }

  class NestedFixtureWordSpecExample extends StringFixtureWordSpec with NestedTestName {
    "A subject" should {
      "test name" in { s =>
        info(msg)
      }
    }
  }

  class DeeplyNestedFixtureWordSpecExample extends StringFixtureWordSpec with DeeplyNestedTestName {
    "A subject" when {
      "created" should {
        "test name" in { s =>
          info(msg)
        }
      }
    }
  }

  class FlatSpecExample extends FlatSpec with Services {
    it should "test name" in {
      info(msg)
    }
    override val theTestName = "should test name"
  }

  class SubjectFlatSpecExample extends FlatSpec with NestedTestName {
    behavior of "A subject"
    it should "test name" in {
      info(msg)
    }
  }
  class ShorthandSubjectFlatSpecExample extends FlatSpec with NestedTestName {
    "A subject" should "test name" in {
      info(msg)
    }
  }

  class FixtureFlatSpecExample extends StringFixtureFlatSpec with Services {
    it should "test name" in { s =>
      info(msg)
    }
    override val theTestName = "should test name"
  }

  class SubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestName {
    behavior of "A subject"
    it should "test name" in { s =>
      info(msg)
    }
  }
  
  class ShorthandSubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestName {
    "A subject" should "test name" in { s =>
      info(msg)
    }
  }

  class FreeSpecExample extends FreeSpec with Services {
    "test name" in {
      info(msg)
    }
  }

  class NestedFreeSpecExample extends FreeSpec with NestedTestName {
    "A subject" - {
      "should test name" in {
        info(msg)
      }
    }
  }

  class DeeplyNestedFreeSpecExample extends FreeSpec with DeeplyNestedTestName {
    "A subject" - {
      "when created" - {
        "should test name" in {
          info(msg)
        }
      }
    }
  }

  class FixtureFreeSpecExample extends StringFixtureFreeSpec with Services {
    "test name" in { s =>
      info(msg)
    }
  }

  class NestedFixtureFreeSpecExample extends StringFixtureFreeSpec with NestedTestName {
    "A subject" - {
      "should test name" in { s =>
        info(msg)
      }
    }
  }

  class DeeplyNestedFixtureFreeSpecExample extends StringFixtureFreeSpec with DeeplyNestedTestName {
    "A subject" - {
      "when created" - {
        "should test name" in { s =>
          info(msg)
        }
      }
    }
  }

  class PathFreeSpecExample extends path.FreeSpec with Services {
    "test name" in {
      info(msg)
    }
  }
    
  class NestedPathFreeSpecExample extends path.FreeSpec with NestedTestName {
    "A subject" - {
      "should test name" in {
        info(msg)
      }
    }
  }

  class DeeplyNestedPathFreeSpecExample extends path.FreeSpec with DeeplyNestedTestName {
    "A subject" - {
      "when created" - {
        "should test name" in {
          info(msg)
        }
      }
    }
  }

  class FeatureSpecExample extends FeatureSpec with Services {
    scenario("test name") {
      info(msg)
    }
    override val theTestName = "Scenario: test name"
  }

  class NestedFeatureSpecExample extends FeatureSpec with Services {
    feature("A feature") {
      scenario("test name") {
        info(msg)
      }
    }
    override val theTestName = "A feature Scenario: test name"
  }

  class FixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    scenario("test name") { s =>
      info(msg)
    }
    override val theTestName = "Scenario: test name"
  }

  class NestedFixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    feature("A feature") {
      scenario("test name") { s =>
        info(msg)
      }
    }
    override val theTestName = "A feature Scenario: test name"
  }

  class PropSpecExample extends PropSpec with Services {
    property(theTestName) {
      info(msg)
    }
  }

  class FixturePropSpecExample extends StringFixturePropSpec with Services {
    property(theTestName) { s =>
      info(msg)
    }
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
}

/* TODO: This gave me a "null" in the output. string passed in to should was null for some reason
   class FlatSpecExample extends FlatSpec with Services {
    it should theTestName in {
      info(msg)
    }
    override val theTestName = "should test name"
  }

Got the same thing here:
  class NestedWordSpecExample extends WordSpec with Services {
    "A subject" should {
      theTestName in {
        info(msg)
      }
    }
    override val theTestName = "A subject should test name"
  }
Has to do with initialization order of an overridden val I think. Curious.
 */

