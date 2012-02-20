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

  property("A path trait should run each test once, in its own instance") {
    new OnlyFirstTestExecutedOnCreationExamples {
      forAll (examples) { suite =>
        suite.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker())
        suite.counts.firstTestCount should be (1)
        suite.counts.secondTestCount should be (1)
        suite.counts.instanceCount should be (2)
      }
    }
  }

  property("A path trait should run only the path to and from each test") {
    new PathBeforeAndAfterExamples {
      forAll (examples) { suite =>
        suite.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker())
        suite.firstTestCounts should be (suite.expectedFirstTestCounts)
        suite.secondTestCounts should be (suite.expectedSecondTestCounts)
        suite.counts should be (suite.expectedCounts)
      }
    }
  }
}
