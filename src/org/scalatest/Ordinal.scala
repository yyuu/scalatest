/*
 * Ordinal.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.scalatest

// TODO: equals and hashCode
final class Ordinal private (val runStamp: Int, stamps: Array[Int]) extends Ordered[Ordinal] {

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
    val newArrayForThisSuite = new Array[Int](stamps.length)
    val zipped = stamps.zipWithIndex
    for ((num, idx) <- zipped) {
      newArrayForNewSuite(idx) = num
      newArrayForThisSuite(idx) = num
    }
    (new Ordinal(runStamp, newArrayForNewSuite), new Ordinal(runStamp, newArrayForThisSuite))
  }

  def toList: List[Int] = runStamp :: stamps.toList

  def compare(that: Ordinal) = 0
}
