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
import matchers.ShouldMatchers

class SuiteProperties extends PropSpec with ShouldMatchers with TableDrivenPropertyChecks with SharedHelpers {

  property("When info appears in the code of a successful test, it should be reported after the TestSucceeded.") {
    new InfoInsideTestFiredAfterTestExamples {
      forAll (examples) { suite =>
        val (infoProvidedIndex, testStartingIndex, testSucceededIndex) =
          getIndexesForInformerEventOrderTests(suite, suite.theTestName, suite.msg)
        testSucceededIndex should be < infoProvidedIndex
      }
    }
  }
  
  property("should, if the first test is marked as ignored, return a tags map from the tags method that says the first test is ignored") {
    new FirstTestIgnoredExamples {
      forAll (examples) { suite =>
        val firstTestName = suite.theTestNames(0)
        suite.tags should be (Map(firstTestName -> Set("org.scalatest.Ignore")))
      }
    }
  }

  property("should, if the second test is marked as ignored, return a tags map from the tags method that says the second test is ignored") {
    new SecondTestIgnoredExamples {
      forAll (examples) { suite =>
        val secondTestName = suite.theTestNames(1)
        suite.tags should be (Map(secondTestName -> Set("org.scalatest.Ignore")))
      }
    }
  }

  property("should, if two tests is marked as ignored, return a tags map from the tags method that says that both tests are ignored") {
    new TwoTestsIgnoredExamples {
      forAll (examples) { suite =>
        val firstTestName = suite.theTestNames(0)
        val secondTestName = suite.theTestNames(1)
        suite.tags should be (Map(firstTestName -> Set("org.scalatest.Ignore"), secondTestName -> Set("org.scalatest.Ignore")))
      }
    }
  }
}