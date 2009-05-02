/*
 * Ordinal.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.scalatest

// TODO: equals and hashCode
final class Ordinal private (val runStamp: Int, private val stamps: Array[Int]) extends Ordered[Ordinal] {

  def this(runStamp: Int) = this(runStamp, Array(0))

  def next: Ordinal = {
    val newArray = new Array[Int](stamps.length) // Can't seem to clone
    val zipped = stamps.zipWithIndex
    for ((num, idx) <- zipped)
      newArray(idx) = num
    newArray(stamps.length - 1) += 1
    new Ordinal(runStamp, newArray)
  }

  /*
the first one is the ordinal for the new suite, the next one
is the ordinal for this suite
  */
  def nextForNewSuite: (Ordinal, Ordinal) = {
    val newArrayForNewSuite = new Array[Int](stamps.length + 1)
    val newArrayForOldSuite = new Array[Int](stamps.length)
    val zipped = stamps.zipWithIndex
    for ((num, idx) <- zipped) {
      newArrayForNewSuite(idx) = num
      newArrayForOldSuite(idx) = num
    }
    (new Ordinal(runStamp, newArrayForOldSuite), new Ordinal(runStamp, newArrayForNewSuite))
  }

  def toList: List[Int] = runStamp :: stamps.toList

  def compare(that: Ordinal) = {
    val runStampDiff = this.runStamp - that.runStamp
    if (runStampDiff == 0) {
      val shorterLength =
        if (this.stamps.length < that.stamps.length)
          this.stamps.length
        else
          that.stamps.length
      var i = 0
      var diff = 0
      while (diff == 0 && i < shorterLength) {
        diff = this.stamps(i) - that.stamps(i)
        i += 1
      }
      // If they were equal all the way to the shorterLength, the longest array
      // one is the greater ordinal.
      if (diff != 0) diff
      else this.stamps.length - that.stamps.length
    }
    else runStampDiff
  }
}
