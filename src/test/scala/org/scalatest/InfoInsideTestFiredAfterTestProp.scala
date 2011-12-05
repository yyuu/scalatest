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
package org.scalatest

import org.scalatest.fixture._
import org.scalatest.prop.TableDrivenPropertyChecks

/*class InfoInsideTestFiredAfterTestProp extends SuiteProp {

  test("When info appears in the code of a successful test, it should be reported after the TestSucceeded.") {
    forAll (examples) { suite =>
        val (infoProvidedIndex, testStartingIndex, testSucceededIndex) =
          getIndexesForInformerEventOrderTests(suite, suite.nameOfTest, suite.msg)
        testSucceededIndex should be < infoProvidedIndex
    }
  }

  trait Services {
    val msg = "hi there, dude"
    val nameOfTest = "test name"
  }

  type FixtureServices = Services
  
  def suite = new LocationTestSuite
  class LocationTestSuite extends Suite with Services {
    override val nameOfTest = "testInfo(Informer)"
    def testInfo(info: Informer) {
      info(msg)
    }
  }
  def suite = 
    new Suite with Services {
      override val nameOfTest = "testMethod"
      def testMethod(info: Informer) {
        info(msg)
      }
    }
  

  def funSuite =
    new FunSuite with Services {
      test(nameOfTest) {
        info(msg)
      }
    }

  def fixtureFunSuite =
    new StringFixtureFunSuite with Services {
      test(nameOfTest) { s =>
        info(msg)
      }
    }

  def spec =
    new Spec with Services {
      it(nameOfTest) {
        info(msg)
      }
    }

  def fixtureSpec =
    new StringFixtureSpec with Services {
      it(nameOfTest) { s =>
        info(msg)
      }
    }
  
  def featureSpec = 
    new FeatureSpec with Services {
      feature(nameOfTest) {
        scenario(nameOfTest) {
          info(msg)
        }
      }
    }
  
  def fixtureFeatureSpec = 
    new FixtureFeatureSpec with Services {
      type FixtureParam = Unit 
      def withFixture(test: OneArgTest) { }
    
      feature(nameOfTest) {
        scenario(nameOfTest) { arg =>
          info(msg)
        }
      }
  }
}*/
