/*
 * OrdinalSpec.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.scalatest

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.prop.Checkers
import org.scalacheck._
import Arbitrary._
import Prop._

class OrdinalSpec extends Spec with ShouldMatchers with Checkers {

  describe("An Ordinal") {

    it("should produce a runStamp :: N on nth next") {
      check(
        (count: Byte) => {
          (count >= 0) ==> {
            var ord = new Ordinal(99)
            for (i <- 0 until count)
              ord = ord.next
            ord.toList == List(99, count)
          }
        }
      )
    }

    it("should produce a runStamp :: 0 ... n times on nth nextForNewSuite") {
      check(
        (count: Byte) => {
          (count >= 0) ==> {
            var ord = new Ordinal(99)
            for (i <- 0 until count)
              ord = ord.nextForNewSuite._1
            println(ord.toList)
            println(99 :: List.make(count, 0))
            ord.toList == 99 :: List.make(count + 1, 0)
          }
        }
      )
    }
  }
}
