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
            ord.toList == 99 :: List.make(count + 1, 0)
          }
        }
      )
    }

    it("should produce a runStamp :: 0 :: 1 :: 2 :: ... :: n on nth next ad nextForNewSuite") {
      check(
        (count: Byte) => {
          (count >= 0) ==> {
            var ord = new Ordinal(99)
            for (i <- 0 until count) {
              for (j <- 0 until i) {
                ord = ord.next
                // println("INNER: " + ord.toList)
              }
              ord = ord.nextForNewSuite._1
              // println("OUTER: " + ord.toList)
            }
            for (i <- 0 until count) // Get the nth one up to be count
              ord = ord.next
            // println("COUNT: " + count + " FINAL: " + ord.toList)
            val zeroToCount = for (i <- 0 to count) yield i
            // println("ZERO2COUNT: " + zeroToCount)
            ord.toList == 99 :: zeroToCount.toList
          }
        }
      )
    }

   // it("should produce a pair of Ordinals that have the same n and n - 1 element when nextForNewSuite is invoked") {
   it("should produce a pair of Ordinals with _1.toList.length one less than _2.toList.length after nextForNewSuite is invoked") {
      check(
        (count: Byte) => {
          (count >= 0) ==> {
            var failures = List[(Ordinal, Ordinal)]()
            var ord = new Ordinal(99)
            for (i <- 0 until count) {
              for (j <- 0 until i) {
                ord = ord.next
              }
              val (forNewSuite, forOldSuite) = ord.nextForNewSuite
              if (forOldSuite.toList.length != forNewSuite.toList.length - 1)
                failures = (forOldSuite, forNewSuite) :: failures
              ord = forNewSuite
            }
            failures.isEmpty
          }
        }
      )
    }

    it("should produce a pair of Ordinals whose n - 1 and n elements are less than by 1 when nextForNewSuite is invoked") {
      check(
        (count: Byte) => {
          (count >= 0) ==> {
            var failures = List[(Ordinal, Ordinal)]()
            var ord = new Ordinal(99)
            for (i <- 0 until count) {
              for (j <- 0 until i) {
                ord = ord.next
              }
              val (forNewSuite, forOldSuite) = ord.nextForNewSuite
              val oldList = forOldSuite.toList
              val newList = forNewSuite.toList
              if (oldList(oldList.length - 1) != newList(oldList.length - 1) + 1)
                failures = (forOldSuite, forNewSuite) :: failures
              ord = forNewSuite
            }
            failures.isEmpty
          }
        }
      )
    }
/*
 Crap, the more natural progression seems to be:

[scalatest] List(99, 0, 1)      ord
[scalatest] List(99, 0, 1, 0)   ordForNewSuite
[scalatest] List(99, 0, 2)      ordForOldSuite

*/
    it("should produce an Ordinal that is greater than this when either next or nextForNewSuite is invoked") {
      check(
        (count: Byte) => {
          (count >= 0) ==> {
            var failures = List[(Ordinal, Ordinal)]()
            var ord = new Ordinal(99)
            for (i <- 0 until count) {
              for (j <- 0 until i) {
                val nextOrd = ord.next
                if (ord >= nextOrd) {
                  failures = (ord, nextOrd) :: failures
                  println("INNER: " + ord.toList + " *** " + nextOrd.toList + " ^^^ " + ord.compare(nextOrd))
                }
                ord = nextOrd
              }
              val (forNewSuite, forOldSuite) = ord.nextForNewSuite
              if (forOldSuite <= forNewSuite || forOldSuite <= ord || forNewSuite <= ord) {
                failures = (forOldSuite, forNewSuite) :: failures
                println("*** 1 " + (forOldSuite <= ord) + "*** 2 " + (forNewSuite <= ord))
                println(forOldSuite.compare(ord))
                println(forNewSuite.compare(ord))
                println(ord.compare(forOldSuite))
                println(ord.compare(forNewSuite))
                println(ord.toList)
                println(forNewSuite.toList)
                println(forOldSuite.toList)
                // println("OUTER: " + forOldSuite.toList + " *** " + forNewSuite.toList + " ^^^ " + forOldSuite.compare(forNewSuite))
              }
              ord = forNewSuite
            }
            // println("FAILURES: " + failures.map((pair) => (pair._1.toList, pair._2.toList)))
            failures.isEmpty
          }
        }
      )
    }
  }
}
