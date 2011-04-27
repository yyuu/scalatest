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

import org.scalatest.matchers.ShouldMatchers

trait LocationFixtures {

  val baseLineNumber = 22

  class MySpec extends Spec {
    describe("outer") {
      describe("inner") {
        it("succeeds") {
        }
        it("fails") {
          fail("oops")
        }
        it("pending") (pending)
        ignore("ignored") {
        }
      }
    }
  }
}

class LocationSpec extends Spec with LocationFixtures with ShouldMatchers with SharedHelpers {

  describe("A Spec") {
    it("should send a defined location for TestStarting messages") {
      val spec = new MySpec
      val myRep = new EventRecordingReporter
      spec.run(None, myRep, new Stopper {}, Filter(), Map(), None, new Tracker)
      val testSucceededEvents = myRep.testSucceededEventsReceived
      withClue(testSucceededEvents map (_.location)) {
        testSucceededEvents.forall(_.location.isDefined) should be (true)
      }
    }
  }
}

