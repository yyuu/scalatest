/*
 * Copyright 2001-2009 Artima, Inc.
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
package org.scalatest.fixture

import events.TestFailed

class FixtureSpecSpec extends org.scalatest.Spec with PrivateMethodTester with SharedHelpers {

  describe("A fixture.Spec") {

    it("should return the test names in order of registration from testNames") {
      val a = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
        it("should do that") { fixture =>
        }
        it("should do this") { fixture =>
        }
      }

      expect(List("should do that", "should do this")) {
        a.testNames.elements.toList
      }

      val b = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
      }

      expect(List[String]()) {
        b.testNames.elements.toList
      }

      val c = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
        it("should do this") { fixture =>
        }
        it("should do that") { fixture =>
        }
      }

      expect(List("should do this", "should do that")) {
        c.testNames.elements.toList
      }
    }

    it("should throw NotAllowedException if a duplicate test name registration is attempted") {

      intercept[DuplicateTestNameException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          it("test this") { fixture =>
          }
          it("test this") { fixture =>
          }
        }
      }
      intercept[DuplicateTestNameException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          it("test this") { fixture =>
          }
          ignore("test this") { fixture =>
          }
        }
      }
      intercept[DuplicateTestNameException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          ignore("test this") { fixture =>
          }
          ignore("test this") { fixture =>
          }
        }
      }
      intercept[DuplicateTestNameException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          ignore("test this") { fixture =>
          }
          it("test this") { fixture =>
          }
        }
      }
    }
    it("should pass in the fixture to every test method") {
      val a = new Spec with SimpleWithFixture {
        type Fixture = String
        val hello = "Hello, world!"
        def withFixture(fun: String => Unit) {
          fun(hello)
        }
        it("should do this") { fixture =>
          assert(fixture === hello)
        }
        it("should do that") { fixture =>
          assert(fixture === hello)
        }
      }
      val rep = new EventRecordingReporter
      a.run(None, rep, new Stopper {}, Filter(), Map(), None, new Tracker())
      assert(!rep.eventsReceived.exists(_.isInstanceOf[TestFailed]))
    }
    it("should throw NullPointerException if a null test tag is provided") {
      // it
      intercept[NullPointerException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          it("hi", null) { fixture => }
        }
      }
      val caught = intercept[NullPointerException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          it("hi", mytags.SlowAsMolasses, null) { fixture => }
        }
      }
      assert(caught.getMessage === "a test tag was null")
      intercept[NullPointerException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          it("hi", mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) { fixture => }
        }
      }
      // ignore
      intercept[NullPointerException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          ignore("hi", null) { fixture => }
        }
      }
      val caught2 = intercept[NullPointerException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          ignore("hi", mytags.SlowAsMolasses, null) { fixture => }
        }
      }
      assert(caught2.getMessage === "a test tag was null")
      intercept[NullPointerException] {
        new Spec with SimpleWithFixture {
          type Fixture = String
          def withFixture(fun: String => Unit) {}
          ignore("hi", mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) { fixture => }
        }
      }
    }
    it("should return a correct tags map from the tags method") {

      val a = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
        ignore("test this") { fixture => }
        it("test that") { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.Ignore"))) {
        a.tags
      }

      val b = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
        it("test this") { fixture => }
        ignore("test that") { fixture => }
      }
      expect(Map("test that" -> Set("org.scalatest.Ignore"))) {
        b.tags
      }

      val c = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
        ignore("test this") { fixture => }
        ignore("test that") { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.Ignore"), "test that" -> Set("org.scalatest.Ignore"))) {
        c.tags
      }

      val d = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
        it("test this", mytags.SlowAsMolasses) { fixture => }
        ignore("test that", mytags.SlowAsMolasses) { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses"), "test that" -> Set("org.scalatest.Ignore", "org.scalatest.SlowAsMolasses"))) {
        d.tags
      }

      val e = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
      }
      expect(Map()) {
        e.tags
      }

      val f = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) {}
        it("test this", mytags.SlowAsMolasses, mytags.WeakAsAKitten) { fixture => }
        it("test that", mytags.SlowAsMolasses) { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses", "org.scalatest.WeakAsAKitten"), "test that" -> Set("org.scalatest.SlowAsMolasses"))) {
        f.tags
      }
    }

    class TestWasCalledSuite extends Spec with SimpleWithFixture {
      type Fixture = String
      def withFixture(fun: String => Unit) { fun("hi") }
      var theTestThisCalled = false
      var theTestThatCalled = false
      it("should run this") { fixture => theTestThisCalled = true }
      it("should run that, maybe") { fixture => theTestThatCalled = true }
    }

    it("should execute all tests when run is called with testName None") {

      val b = new TestWasCalledSuite
      b.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(b.theTestThisCalled)
      assert(b.theTestThatCalled)
    }

    it("should execute one test when run is called with a defined testName") {

      val a = new TestWasCalledSuite
      a.run(Some("should run this"), SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(a.theTestThisCalled)
      assert(!a.theTestThatCalled)
    }

    it("should report as ignored, and not run, tests marked ignored") {

      val a = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        it("test this") { fixture => theTestThisCalled = true }
        it("test that") { fixture => theTestThatCalled = true }
      }

      val repA = new TestIgnoredTrackingReporter
      a.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(!repA.testIgnoredReceived)
      assert(a.theTestThisCalled)
      assert(a.theTestThatCalled)

      val b = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        ignore("test this") { fixture => theTestThisCalled = true }
        it("test that") { fixture => theTestThatCalled = true }
      }

      val repB = new TestIgnoredTrackingReporter
      b.run(None, repB, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repB.testIgnoredReceived)
      assert(repB.lastEvent.isDefined)
      assert(repB.lastEvent.get.testName endsWith "test this")
      assert(!b.theTestThisCalled)
      assert(b.theTestThatCalled)

      val c = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        it("test this") { fixture => theTestThisCalled = true }
        ignore("test that") { fixture => theTestThatCalled = true }
      }

      val repC = new TestIgnoredTrackingReporter
      c.run(None, repC, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repC.testIgnoredReceived)
      assert(repC.lastEvent.isDefined)
      assert(repC.lastEvent.get.testName endsWith "test that", repC.lastEvent.get.testName)
      assert(c.theTestThisCalled)
      assert(!c.theTestThatCalled)

      // The order I want is order of appearance in the file.
      // Will try and implement that tomorrow. Subtypes will be able to change the order.
      val d = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        ignore("test this") { fixture => theTestThisCalled = true }
        ignore("test that") { fixture => theTestThatCalled = true }
      }

      val repD = new TestIgnoredTrackingReporter
      d.run(None, repD, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repD.testIgnoredReceived)
      assert(repD.lastEvent.isDefined)
      assert(repD.lastEvent.get.testName endsWith "test that") // last because should be in order of appearance
      assert(!d.theTestThisCalled)
      assert(!d.theTestThatCalled)
    }

    it("should run a test marked as ignored if run is invoked with that testName") {
      // If I provide a specific testName to run, then it should ignore an Ignore on that test
      // method and actually invoke it.
      val e = new Spec with SimpleWithFixture {
        type Fixture = String
        def withFixture(fun: String => Unit) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        ignore("test this") { fixture => theTestThisCalled = true }
        it("test that") { fixture => theTestThatCalled = true }
      }

      val repE = new TestIgnoredTrackingReporter
      e.run(Some("test this"), repE, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(!repE.testIgnoredReceived)
      assert(e.theTestThisCalled)
    }
  }
}