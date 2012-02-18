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

    class FixtureFunSpecExample extends StringFixtureFunSpec with Services {
      it(theTestName) { s =>
        info(msg)
      }
    }
  
    class PathFunSpecExample extends path.FunSpec with Services {
      it(theTestName) {
        info(msg)
      }
    }
    
  class WordSpecExample extends WordSpec with Services {
    theTestName in {
      info(msg)
    }
  }

  class FixtureWordSpecExample extends StringFixtureWordSpec with Services {
    theTestName in { s =>
      info(msg)
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
}
