/*
 * EventSpec.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.scalatest

import org.scalatest.prop.Checkers
import org.scalacheck._
import Arbitrary._
import Prop._

class EventSpec extends Spec with Checkers {

  describe("An TestStarting Event") {
    describe("(with different runStamps)") {
      it("should sort into order by runStamp") {
        check(
          (runStampA: Int, runStampB: Int, suiteStamp: List[Int], testStamp: Int, ordinal: Int) =>
            (runStampA != runStampB) ==> {
              val unsorted =
                List(
                  TestStarting("X - test", "X", Some("com.acme.X"), "test", runStampA, suiteStamp, testStamp, ordinal),
                  TestStarting("Y - test", "Y", Some("com.acme.Y"), "test", runStampB, suiteStamp, testStamp, ordinal)
                )
              val sorted = unsorted.sort(_ < _)
              sorted.head.runStamp < sorted.tail.head.runStamp
            }
        )
      }
    }
  }
}
