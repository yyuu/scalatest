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

class FixtureWordSpecSpec extends org.scalatest.Spec with PrivateMethodTester with SharedHelpers {

  describe("A FixtureWordSpec") {

    it("should return the test names in order of registration from testNames") {
      val a = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "Something" should {
          "do that" in { fixture =>
          }
          "do this" in { fixture =>
          }
        }
      }

      expect(List("Something should do that", "Something should do this")) {
        a.testNames.elements.toList
      }

      val b = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
      }

      expect(List[String]()) {
        b.testNames.elements.toList
      }

      val c = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "Something" should {
          "do this" in { fixture =>
          }
          "do that" in { fixture =>
          }
        }
      }

      expect(List("Something should do this", "Something should do that")) {
        c.testNames.elements.toList
      }
    }

    it("should throw DuplicateTestNameException if a duplicate test name registration is attempted") {

      intercept[DuplicateTestNameException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "should test this" in { fixture => }
          "should test this" in { fixture => }
        }
      }
      intercept[DuplicateTestNameException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "should test this" in { fixture => }
          "should test this" ignore { fixture => }
        }
      }
      intercept[DuplicateTestNameException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "should test this" ignore { fixture => }
          "should test this" ignore { fixture => }
        }
      }
      intercept[DuplicateTestNameException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "should test this" ignore { fixture => }
          "should test this" in { fixture => }
        }
      }
    }

    it("should pass in the fixture to every test method") {
      val a = new FixtureWordSpec {
        type Fixture = String
        val hello = "Hello, world!"
        def withFixture(fun: TestFunction) {
          fun(hello)
        }
        "Something" should {
          "do this" in { fixture =>
            assert(fixture === hello)
          }
          "do that" in { fixture =>
            assert(fixture === hello)
          }
        }
      }
      val rep = new EventRecordingReporter
      a.run(None, rep, new Stopper {}, Filter(), Map(), None, new Tracker())
      assert(!rep.eventsReceived.exists(_.isInstanceOf[TestFailed]))
    }
    it("should throw NullPointerException if a null test tag is provided") {
      // it
      intercept[NullPointerException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "hi" taggedAs(null) in { fixture => }
        }
      }
      val caught = intercept[NullPointerException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "hi" taggedAs(mytags.SlowAsMolasses, null) in { fixture => }
        }
      }
      assert(caught.getMessage === "a test tag was null")
      intercept[NullPointerException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "hi" taggedAs(mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) in { fixture => }
        }
      }
      // ignore
      intercept[NullPointerException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "hi" taggedAs(null) ignore { fixture => }
        }
      }
      val caught2 = intercept[NullPointerException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "hi" taggedAs(mytags.SlowAsMolasses, null) ignore { fixture => }
        }
      }
      assert(caught2.getMessage === "a test tag was null")
      intercept[NullPointerException] {
        new FixtureWordSpec {
          type Fixture = String
          def withFixture(fun: TestFunction) {}
          "hi" taggedAs(mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) ignore { fixture => }
        }
      }
    }
    it("should return a correct tags map from the tags method") {

      val a = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" ignore { fixture => }
        "test that" in { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.Ignore"))) {
        a.tags
      }

      val b = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" in { fixture => }
        "test that" ignore { fixture => }
      }
      expect(Map("test that" -> Set("org.scalatest.Ignore"))) {
        b.tags
      }

      val c = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" ignore { fixture => }
        "test that" ignore { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.Ignore"), "test that" -> Set("org.scalatest.Ignore"))) {
        c.tags
      }

      val d = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" taggedAs(mytags.SlowAsMolasses) in { fixture => }
        "test that" taggedAs(mytags.SlowAsMolasses) ignore { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses"), "test that" -> Set("org.scalatest.Ignore", "org.scalatest.SlowAsMolasses"))) {
        d.tags
      }

      val e = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" in { fixture => }
        "test that" in { fixture => }
      }
      expect(Map()) {
        e.tags
      }

      val f = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { fixture => }
        "test that" taggedAs(mytags.SlowAsMolasses) in  { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses", "org.scalatest.WeakAsAKitten"), "test that" -> Set("org.scalatest.SlowAsMolasses"))) {
        f.tags
      }

      val g = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.WeakAsAKitten) in { fixture => }
        "test that" taggedAs(mytags.SlowAsMolasses) in  { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses", "org.scalatest.WeakAsAKitten"), "test that" -> Set("org.scalatest.SlowAsMolasses"))) {
        g.tags
      }
    }
    it("should return a correct tags map from the tags method using is (pending)") {

      val a = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" ignore { fixture => }
        "test that" is (pending)
      }
      expect(Map("test this" -> Set("org.scalatest.Ignore"))) {
        a.tags
      }

      val b = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" is (pending)
        "test that" ignore { fixture => }
      }
      expect(Map("test that" -> Set("org.scalatest.Ignore"))) {
        b.tags
      }

      val c = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" ignore { fixture => }
        "test that" ignore { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.Ignore"), "test that" -> Set("org.scalatest.Ignore"))) {
        c.tags
      }

      val d = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" taggedAs(mytags.SlowAsMolasses) is (pending)
        "test that" taggedAs(mytags.SlowAsMolasses) ignore { fixture => }
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses"), "test that" -> Set("org.scalatest.Ignore", "org.scalatest.SlowAsMolasses"))) {
        d.tags
      }

      val e = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" is (pending)
        "test that" is (pending)
      }
      expect(Map()) {
        e.tags
      }

      val f = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.WeakAsAKitten) is (pending)
        "test that" taggedAs(mytags.SlowAsMolasses) is (pending)
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses", "org.scalatest.WeakAsAKitten"), "test that" -> Set("org.scalatest.SlowAsMolasses"))) {
        f.tags
      }

      val g = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) {}
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.WeakAsAKitten) is (pending)
        "test that" taggedAs(mytags.SlowAsMolasses) is (pending)
      }
      expect(Map("test this" -> Set("org.scalatest.SlowAsMolasses", "org.scalatest.WeakAsAKitten"), "test that" -> Set("org.scalatest.SlowAsMolasses"))) {
        g.tags
      }
    }
    class TestWasCalledSuite extends FixtureWordSpec {
      type Fixture = String
      def withFixture(fun: TestFunction) { fun("hi") }
      var theTestThisCalled = false
      var theTestThatCalled = false
      "run this" in { fixture => theTestThisCalled = true }
      "run that, maybe" in { fixture => theTestThatCalled = true }
    }

    it("should execute all tests when run is called with testName None") {

      val b = new TestWasCalledSuite
      b.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(b.theTestThisCalled)
      assert(b.theTestThatCalled)
    }

    it("should execute one test when run is called with a defined testName") {

      val a = new TestWasCalledSuite
      a.run(Some("run this"), SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(a.theTestThisCalled)
      assert(!a.theTestThatCalled)
    }

    it("should report as ignored, and not run, tests marked ignored") {

      val a = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" in { fixture => theTestThisCalled = true }
        "test that" in { fixture => theTestThatCalled = true }
      }

      val repA = new TestIgnoredTrackingReporter
      a.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(!repA.testIgnoredReceived)
      assert(a.theTestThisCalled)
      assert(a.theTestThatCalled)

      val b = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" ignore { fixture => theTestThisCalled = true }
        "test that" in { fixture => theTestThatCalled = true }
      }

      val repB = new TestIgnoredTrackingReporter
      b.run(None, repB, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(repB.testIgnoredReceived)
      assert(repB.lastEvent.isDefined)
      assert(repB.lastEvent.get.testName endsWith "test this")
      assert(!b.theTestThisCalled)
      assert(b.theTestThatCalled)

      val c = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" in { fixture => theTestThisCalled = true }
        "test that" ignore { fixture => theTestThatCalled = true }
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
      val d = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" ignore { fixture => theTestThisCalled = true }
        "test that" ignore { fixture => theTestThatCalled = true }
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
      val e = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" ignore { fixture => theTestThisCalled = true }
        "test that" in { fixture => theTestThatCalled = true }
      }

      val repE = new TestIgnoredTrackingReporter
      e.run(Some("test this"), repE, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(!repE.testIgnoredReceived)
      assert(e.theTestThisCalled)
      assert(!e.theTestThatCalled)
    }

    it("should run only those tests selected by the tags to include and exclude sets") {

      // Nothing is excluded
      val a = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThisCalled = true }
        "test that" in { fixture => theTestThatCalled = true }
      }
      val repA = new TestIgnoredTrackingReporter
      a.run(None, repA, new Stopper {}, Filter(), Map(), None, new Tracker)
      assert(!repA.testIgnoredReceived)
      assert(a.theTestThisCalled)
      assert(a.theTestThatCalled)

      // SlowAsMolasses is included, one test should be excluded
      val b = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThisCalled = true }
        "test that" in { fixture => theTestThatCalled = true }
      }
      val repB = new TestIgnoredTrackingReporter
      b.run(None, repB, new Stopper {}, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set()), Map(), None, new Tracker)
      assert(!repB.testIgnoredReceived)
      assert(b.theTestThisCalled)
      assert(!b.theTestThatCalled)

      // SlowAsMolasses is included, and both tests should be included
      val c = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThatCalled = true }
      }
      val repC = new TestIgnoredTrackingReporter
      c.run(None, repB, new Stopper {}, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set()), Map(), None, new Tracker)
      assert(!repC.testIgnoredReceived)
      assert(c.theTestThisCalled)
      assert(c.theTestThatCalled)

      // SlowAsMolasses is included. both tests should be included but one ignored
      val d = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses) ignore { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThatCalled = true }
      }
      val repD = new TestIgnoredTrackingReporter
      d.run(None, repD, new Stopper {}, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.Ignore")), Map(), None, new Tracker)
      assert(repD.testIgnoredReceived)
      assert(!d.theTestThisCalled)
      assert(d.theTestThatCalled)

      // SlowAsMolasses included, FastAsLight excluded
      val e = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.FastAsLight) in { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThatCalled = true }
        "test the other" in { fixture => theTestTheOtherCalled = true }
      }
      val repE = new TestIgnoredTrackingReporter
      e.run(None, repE, new Stopper {}, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
                Map(), None, new Tracker)
      assert(!repE.testIgnoredReceived)
      assert(!e.theTestThisCalled)
      assert(e.theTestThatCalled)
      assert(!e.theTestTheOtherCalled)

      // An Ignored test that was both included and excluded should not generate a TestIgnored event
      val f = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.FastAsLight) ignore { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThatCalled = true }
        "test the other" in { fixture => theTestTheOtherCalled = true }
      }
      val repF = new TestIgnoredTrackingReporter
      f.run(None, repF, new Stopper {}, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
                Map(), None, new Tracker)
      assert(!repF.testIgnoredReceived)
      assert(!f.theTestThisCalled)
      assert(f.theTestThatCalled)
      assert(!f.theTestTheOtherCalled)

      // An Ignored test that was not included should not generate a TestIgnored event
      val g = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.FastAsLight) in { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThatCalled = true }
        "test the other" ignore { fixture => theTestTheOtherCalled = true }
      }
      val repG = new TestIgnoredTrackingReporter
      g.run(None, repG, new Stopper {}, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
                Map(), None, new Tracker)
      assert(!repG.testIgnoredReceived)
      assert(!g.theTestThisCalled)
      assert(g.theTestThatCalled)
      assert(!g.theTestTheOtherCalled)

      // No tagsToInclude set, FastAsLight excluded
      val h = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.FastAsLight) in { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThatCalled = true }
        "test the other" in { fixture => theTestTheOtherCalled = true }
      }
      val repH = new TestIgnoredTrackingReporter
      h.run(None, repH, new Stopper {}, Filter(None, Set("org.scalatest.FastAsLight")), Map(), None, new Tracker)
      assert(!repH.testIgnoredReceived)
      assert(!h.theTestThisCalled)
      assert(h.theTestThatCalled)
      assert(h.theTestTheOtherCalled)

      // No tagsToInclude set, SlowAsMolasses excluded
      val i = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.FastAsLight) in { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => theTestThatCalled = true }
        "test the other" in { fixture => theTestTheOtherCalled = true }
      }
      val repI = new TestIgnoredTrackingReporter
      i.run(None, repI, new Stopper {}, Filter(None, Set("org.scalatest.SlowAsMolasses")), Map(), None, new Tracker)
      assert(!repI.testIgnoredReceived)
      assert(!i.theTestThisCalled)
      assert(!i.theTestThatCalled)
      assert(i.theTestTheOtherCalled)

      // No tagsToInclude set, SlowAsMolasses excluded, TestIgnored should not be received on excluded ones
      val j = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.FastAsLight) ignore { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) ignore { fixture => theTestThatCalled = true }
        "test the other" in { fixture => theTestTheOtherCalled = true }
      }
      val repJ = new TestIgnoredTrackingReporter
      j.run(None, repJ, new Stopper {}, Filter(None, Set("org.scalatest.SlowAsMolasses")), Map(), None, new Tracker)
      assert(!repI.testIgnoredReceived)
      assert(!j.theTestThisCalled)
      assert(!j.theTestThatCalled)
      assert(j.theTestTheOtherCalled)

      // Same as previous, except Ignore specifically mentioned in excludes set
      val k = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        "test this" taggedAs(mytags.SlowAsMolasses, mytags.FastAsLight) ignore { fixture => theTestThisCalled = true }
        "test that" taggedAs(mytags.SlowAsMolasses) ignore { fixture => theTestThatCalled = true }
        "test the other" ignore { fixture => theTestTheOtherCalled = true }
      }
      val repK = new TestIgnoredTrackingReporter
      k.run(None, repK, new Stopper {}, Filter(None, Set("org.scalatest.SlowAsMolasses", "org.scalatest.Ignore")), Map(), None, new Tracker)
      assert(repK.testIgnoredReceived)
      assert(!k.theTestThisCalled)
      assert(!k.theTestThatCalled)
      assert(!k.theTestTheOtherCalled)
    }

    it("should return the correct test count from its expectedTestCount method") {

      val a = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        "test this" in { fixture => }
        "test that" in { fixture => }
      }
      assert(a.expectedTestCount(Filter()) === 2)

      val b = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        "test this" ignore { fixture => }
        "test that" in { fixture => }
      }
      assert(b.expectedTestCount(Filter()) === 1)

      val c = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        "test this" taggedAs(mytags.FastAsLight) in { fixture => }
        "test that" in { fixture => }
      }
      assert(c.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) === 1)
      assert(c.expectedTestCount(Filter(None, Set("org.scalatest.FastAsLight"))) === 1)

      val d = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        "test this" taggedAs(mytags.FastAsLight, mytags.SlowAsMolasses) in { fixture => }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => }
        "test the other thing" in { fixture => }
      }
      assert(d.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) === 1)
      assert(d.expectedTestCount(Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight"))) === 1)
      assert(d.expectedTestCount(Filter(None, Set("org.scalatest.SlowAsMolasses"))) === 1)
      assert(d.expectedTestCount(Filter()) === 3)

      val e = new FixtureWordSpec {
        type Fixture = String
        def withFixture(fun: TestFunction) { fun("hi") }
        "test this" taggedAs(mytags.FastAsLight, mytags.SlowAsMolasses) in { fixture => }
        "test that" taggedAs(mytags.SlowAsMolasses) in { fixture => }
        "test the other thing" ignore { fixture => }
      }
      assert(e.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) === 1)
      assert(e.expectedTestCount(Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight"))) === 1)
      assert(e.expectedTestCount(Filter(None, Set("org.scalatest.SlowAsMolasses"))) === 0)
      assert(e.expectedTestCount(Filter()) === 2)

      val f = new SuperSuite(List(a, b, c, d, e))
      assert(f.expectedTestCount(Filter()) === 10)
    }

    it("should generate a TestPending message when the test body is (pending)") {
      val a = new FixtureWordSpec {
        type Fixture = String
        val hello = "Hello, world!"
        def withFixture(fun: TestFunction) {
          fun(hello)
        }

        "should do this" is (pending)

        "should do that" in { fixture =>
          assert(fixture === hello)
        }
        "should do something else" in { fixture =>
          assert(fixture === hello)
          pending
        }
      }
      val rep = new EventRecordingReporter
      a.run(None, rep, new Stopper {}, Filter(), Map(), None, new Tracker())
      val tp = rep.testPendingEventsReceived
      assert(tp.size === 2)
    }
    it("should generate a test failure if a Throwable, or an Error other than direct Error subtypes " +
            "known in JDK 1.5, excluding AssertionError") {
      val a = new FixtureWordSpec {
        type Fixture = String
        val hello = "Hello, world!"
        def withFixture(fun: TestFunction) {
          fun(hello)
        }
        "This WordSpec" should {
          "throw AssertionError" in { s => throw new AssertionError }
          "throw plain old Error" in { s => throw new Error }
          "throw Throwable" in { s => throw new Throwable }
        }
      }
      val rep = new EventRecordingReporter
      a.run(None, rep, new Stopper {}, Filter(), Map(), None, new Tracker())
      val tf = rep.testFailedEventsReceived
      assert(tf.size === 3)
    }
    it("should propagate out Errors that are direct subtypes of Error in JDK 1.5, other than " +
            "AssertionError, causing Suites and Runs to abort.") {
      val a = new FixtureWordSpec {
        type Fixture = String
        val hello = "Hello, world!"
        def withFixture(fun: TestFunction) {
          fun(hello)
        }
        "This WordSpec" should {
          "throw AssertionError" in { s => throw new OutOfMemoryError }
        }
      }
      intercept[OutOfMemoryError] {
        a.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker())
      }
    }
    it("should send InfoProvided events with aboutAPendingTest set to true for info " +
            "calls made from a test that is pending") {
      val a = new FixtureWordSpec with GivenWhenThen {
        type Fixture = String
        val hello = "Hello, world!"
        def withFixture(fun: TestFunction) {
          fun(hello)
        }
        "A WordSpec" should {
          "do something" in { s =>
            given("two integers")
            when("one is subracted from the other")
            then("the result is the difference between the two numbers")
            pending
          }
        }
      }
      val rep = new EventRecordingReporter
      a.run(None, rep, new Stopper {}, Filter(), Map(), None, new Tracker())
      val ip = rep.infoProvidedEventsReceived
      assert(ip.size === 4)
      for (event <- ip) {
        assert(event.message == "A WordSpec" || event.aboutAPendingTest.isDefined && event.aboutAPendingTest.get)
      }
    }
    it("should send InfoProvided events with aboutAPendingTest set to false for info " +
            "calls made from a test that is not pending") {
      val a = new FixtureWordSpec with GivenWhenThen {
        type Fixture = String
        val hello = "Hello, world!"
        def withFixture(fun: TestFunction) {
          fun(hello)
        }
        "A WordSpec" should {
          "do something" in { s =>
            given("two integers")
            when("one is subracted from the other")
            then("the result is the difference between the two numbers")
            assert(1 + 1 === 2)
          }
        }
      }
      val rep = new EventRecordingReporter
      a.run(None, rep, new Stopper {}, Filter(), Map(), None, new Tracker())
      val ip = rep.infoProvidedEventsReceived
      assert(ip.size === 4)
      for (event <- ip) {
        assert(event.message == "A WordSpec" || event.aboutAPendingTest.isDefined && !event.aboutAPendingTest.get)
      }
    }
  }
}