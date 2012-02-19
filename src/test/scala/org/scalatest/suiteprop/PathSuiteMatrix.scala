package org.scalatest.suiteprop

import org.scalatest._
import prop.TableDrivenPropertyChecks
import matchers.ShouldMatchers

class PathSuiteMatrix extends PropSpec with ShouldMatchers with TableDrivenPropertyChecks with SharedHelpers {
  
  property("A path trait should execute the first test, and only the first test, on initial instance creation") {

    new OnlyFirstTestExecutedOnCreationExamples {
      forAll (examples) { suite =>
        suite.counts.firstTestCount should be (1)
        suite.counts.secondTestCount should be (0)
      }
    }
  }

  property("A path trait should run each test in its own instance") {
  }
}
