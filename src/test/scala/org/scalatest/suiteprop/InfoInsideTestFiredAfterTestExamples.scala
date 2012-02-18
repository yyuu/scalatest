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

  class FixtureFlatSpecExample extends StringFixtureFlatSpec with Services {
    it should "test name" in { s =>
      info(msg)
    }
    override val theTestName = "should test name"
  }

  class FreeSpecExample extends FreeSpec with Services {
    theTestName in {
      info(msg)
    }
  }

  class FixtureFreeSpecExample extends StringFixtureFreeSpec with Services {
    theTestName in { s =>
      info(msg)
    }
  }

  class FeatureSpecExample extends FeatureSpec with Services {
    scenario("test name") {
      info(msg)
    }
    override val theTestName = "Scenario: test name"
  }

  class FixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    scenario("test name") { s =>
      info(msg)
    }
    override val theTestName = "Scenario: test name"
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
  def fixtureFlatSpec = new FixtureFlatSpecExample
  def freeSpec = new FreeSpecExample
  def fixtureFreeSpec = new FixtureFreeSpecExample
  def featureSpec = new FeatureSpecExample
  def fixtureFeatureSpec = new FixtureFeatureSpecExample
  def propSpec = new PropSpecExample
  def fixturePropSpec = new FixturePropSpecExample
}

/* TODO: This gave me a "null" in the output. string passed in to should was null for some reason
   class FlatSpecExample extends FlatSpec with Services {
    it should theTestName in {
      info(msg)
    }
    override val theTestName = "should test name"
  }

Got the same thing ehre:
  class NestedWordSpecExample extends WordSpec with Services {
    "A subject" should {
      theTestName in {
        info(msg)
      }
    }
    override val theTestName = "A subject should test name"
  }

 */

