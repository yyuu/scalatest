/*
 * Copyright 2001-2008 Artima, Inc.
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
package org.scalatest.junit {

  import org.scalatest.events._

  // Put fixture suites in a subpackage, so they won't be discovered by
  // -m org.scalatest.junit when running the test target for this project.
  package helpers {

    import _root_.org.junit.Test
    import _root_.org.junit.Ignore

    class HappySuite extends JUnitSuite {

      @Test def verifySomething() = () // Don't do nothin'
    }

    class BitterSuite extends JUnitSuite {

      @Test def verifySomething() {
        assert(1 === 2) // This will fail
      }
    }

    class IgnoredSuite extends JUnitSuite {

      @Ignore @Test def verifySomething() {
        assert(1 === 2) // This would fail if it were not ignored
      }
    }

    // Used to make sure TestStarting gets fired twice
    class ManySuite extends JUnitSuite {

      @Test def verifySomething() = ()
      @Test def verifySomethingElse() = ()
    }
  }

  import helpers._

  class JUnitSuiteSuite extends FunSuite {

    class MyReporter extends Reporter {

      def apply(event: Event) {
        event match {
          case event: TestStarting =>
            testStartingEvent = Some(event)
        System.out.println("gcbx testStartingEvent.get.testName [" + testStartingEvent.get.testName + "][" + testStartingEvent.get.suiteName + "]");
            testStartingCount += 1
          case event: TestIgnored =>
            testIgnoredEvent = Some(event)
          case event: TestSucceeded =>
            testSucceededEvent = Some(event)
        System.out.println("gcbx testSucceededEvent.get.testName [" + testSucceededEvent.get.testName + "][" + testSucceededEvent.get.suiteName + "]");
            testSucceededCount += 1
          case event: TestFailed =>
            testFailedEvent = Some(event)
        System.out.println("gcbx testFailedEvent.get.testName [" + testFailedEvent.get.testName + "][" + testFailedEvent.get.suiteName + "]");
          case _ => 
        }
      }

      var testStartingCount = 0
      var testStartingEvent: Option[TestStarting] = None

      var testSucceededCount = 0
      var testSucceededEvent: Option[TestSucceeded] = None

      var testFailedEvent: Option[TestFailed] = None

      var testIgnoredEvent: Option[TestIgnored] = None
    }

    test("A JUnitSuite with a JUnit 4 Test annotation will cause TestStarting event to be fired") {

      val happy = new HappySuite
      val repA = new MyReporter
      happy.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repA.testStartingEvent.isDefined)
      assert(repA.testStartingEvent.get.testName === "verifySomething")
      assert(repA.testStartingEvent.get.suiteName === "HappySuite")
      assert(repA.testStartingEvent.get.suiteClassName.get === "org.scalatest.junit.helpers.HappySuite")
    }

    test("A JUnitSuite with a JUnit 4 Test annotation will cause TestSucceeded to be fired") {

      val happy = new HappySuite
      val repA = new MyReporter
      happy.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repA.testSucceededEvent.isDefined)
      assert(repA.testSucceededEvent.get.testName === "verifySomething")
      assert(repA.testSucceededEvent.get.suiteName === "HappySuite")
      assert(repA.testSucceededEvent.get.suiteClassName.get === "org.scalatest.junit.helpers.HappySuite")
    }

    test("A JUnitSuite with a JUnit 4 Test annotation on a bad test will cause testFailed to be invoked") {

      val bitter = new BitterSuite
      val repA = new MyReporter
      bitter.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repA.testFailedEvent.isDefined)
      assert(repA.testFailedEvent.get.testName === "verifySomething")
      assert(repA.testFailedEvent.get.suiteName === "BitterSuite")
      assert(repA.testFailedEvent.get.suiteClassName.get === "org.scalatest.junit.helpers.BitterSuite")
    }

    test("A JUnitSuite with JUnit 4 Ignore and Test annotations will cause TestIgnored to be fired") {

      val ignored = new IgnoredSuite
      val repA = new MyReporter
      ignored.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repA.testIgnoredEvent.isDefined)
      assert(repA.testIgnoredEvent.get.testName === "verifySomething")
      assert(repA.testIgnoredEvent.get.suiteName === "IgnoredSuite")
      assert(repA.testIgnoredEvent.get.suiteClassName.get === "org.scalatest.junit.helpers.IgnoredSuite")
    }

    test("A JUnitSuite with two JUnit 4 Test annotations will cause TestStarting and TestSucceeded events to be fired twice each") {

      val many = new ManySuite
      val repA = new MyReporter
      many.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)

      assert(repA.testStartingEvent.isDefined)
      assert(repA.testStartingEvent.get.testName === "verifySomethingElse")
      assert(repA.testStartingEvent.get.suiteName === "ManySuite")
      assert(repA.testStartingEvent.get.suiteClassName.get === "org.scalatest.junit.helpers.ManySuite")
      assert(repA.testStartingCount === 2)

      assert(repA.testSucceededEvent.isDefined)
      assert(repA.testSucceededEvent.get.testName === "verifySomethingElse")
      assert(repA.testSucceededEvent.get.suiteName === "ManySuite")
      assert(repA.testSucceededEvent.get.suiteClassName.get === "org.scalatest.junit.helpers.ManySuite")
      assert(repA.testSucceededCount === 2)
    }

    test("A JUnitWrapperSuite for a JUnit3 TestCase class") {
      val jRap = new JUnitWrapperSuite("org.scalatest.junit.JUnit3TestCase", this.getClass.getClassLoader)
      val repA = new MyReporter

      jRap.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)

      assert(repA.testStartingEvent.isDefined)
      assert(repA.testStartingEvent.get.suiteName === "JUnitWrapperSuite")
      assert(repA.testStartingEvent.get.suiteClassName.get ===
             "org.scalatest.junit.JUnitWrapperSuite")
      assert(repA.testStartingCount === 3)

      assert(repA.testSucceededEvent.isDefined)
      assert(repA.testSucceededEvent.get.testName === "testC")
      assert(repA.testSucceededEvent.get.suiteName === "JUnitWrapperSuite")
      assert(repA.testSucceededEvent.get.suiteClassName.get ===
             "org.scalatest.junit.JUnitWrapperSuite")
      assert(repA.testSucceededCount === 2)
    }

    test("A JUnitWrapperSuite for a JUnit3 TestSuite class") {
      val jRap = new JUnitWrapperSuite("org.scalatest.junit.JUnit3TestSuite", this.getClass.getClassLoader)
      val repA = new MyReporter

      jRap.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)

      assert(repA.testStartingEvent.isDefined)
      assert(repA.testStartingEvent.get.testName === "testB")
      assert(repA.testStartingEvent.get.suiteName === "JUnitWrapperSuite")
      assert(repA.testStartingEvent.get.suiteClassName.get ===
             "org.scalatest.junit.JUnitWrapperSuite")
      assert(repA.testStartingCount === 2)

      assert(repA.testSucceededEvent.isDefined)
      assert(repA.testSucceededEvent.get.testName === "testB")
      assert(repA.testSucceededEvent.get.suiteName === "JUnitWrapperSuite")
      assert(repA.testSucceededEvent.get.suiteClassName.get ===
             "org.scalatest.junit.JUnitWrapperSuite")
      assert(repA.testSucceededCount === 2)
    }

  }
}
