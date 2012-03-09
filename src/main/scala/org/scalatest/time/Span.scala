/*
 * Copyright 2001-2012 Artima, Inc.
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
package org.scalatest.time

import Span.totalNanosForLongLength
import Span.totalNanosForDoubleLength
import org.scalatest.Resources

// TODO: Maybe make this not a case class, but make an extractor. Could have
// an identical extractor for both Span and SpanConcept.
// case class Span private (m: Long, n: Int = 0) extends SpanConcept(m, n)
final class Span private (val totalNanos: Long, lengthString: String, unitsResource: String, unitsName: String) {

  private def this(length: Long, units: Units) {
    this(
      totalNanosForLongLength(length, units),
      length.toString,
      if (length == 1) units.singularResourceName else units.pluralResourceName,
      units.toString
    )
  }

  private def this(length: Double, units: Units) {
    this(
      totalNanosForDoubleLength(length, units),
      length.toString,
      if (length == 1.0) units.singularResourceName else units.pluralResourceName,
      units.toString
    )
  }

  lazy val prettyString: String = Resources(unitsResource, lengthString)

  override def toString = "Span(" + lengthString + ", " + unitsName + ")"
  
  override def equals(other: Any): Boolean = {
    other match {
      case that: Span => totalNanos == that.totalNanos
      case _ => false
    }
  }

  override def hashCode: Int = totalNanos.hashCode
}

object Span {

  def apply(length: Long, units: Units): Span = new Span(length, units)

  def apply(length: Double, units: Units): Span = new Span(length, units)

  private def totalNanosForLongLength(length: Long, units: Units): Long = {

    // TODO: Need others here
    val MaxSeconds = Long.MaxValue / 1000 / 1000 / 1000
    val MaxMinutes = Long.MaxValue / 1000 / 1000 / 1000 / 60
    val MaxHours = Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60
    val MaxDays = Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60 / 24

    require(length >= 0, "length must be greater than or equal to zero, but was: " + length)

    require(units != Nanosecond || length == 1, singularErrorMsg("Nanosecond"))
    require(units != Microsecond || length == 1, singularErrorMsg("Microsecond"))
    require(units != Millisecond || length == 1, singularErrorMsg("Millisecond"))
    require(units != Second || length == 1, singularErrorMsg("Second"))
    require(units != Minute || length == 1, singularErrorMsg("Minute"))
    require(units != Hour || length == 1, singularErrorMsg("Hour"))
    require(units != Day || length == 1, singularErrorMsg("Day"))

    require(units != Seconds || length <= MaxSeconds, "Passed length, " + length + ", is larger than the largest expressible number of seconds: Long.MaxValue / 1000")
    require(units != Minutes || length <= MaxMinutes, "Passed length, " + length + ", is larger than the largest expressible number of minutes: Long.MaxValue / 1000 / 60")
    require(units != Hours || length <= MaxHours, "Passed length, " + length + ", is larger than the largest expressible number of hours: Long.MaxValue / 1000 / 60 / 60")
    require(units != Days || length <= MaxDays, "Passed length, " + length + ", is larger than the largest expressible number of days: Long.MaxValue / 1000 / 60 / 60 / 24")

    units match {
      case Nanosecond | Nanoseconds =>
        length
      case Microsecond | Microseconds =>
        length * 1000
      case Millisecond | Milliseconds | Millis =>
        length * 1000 * 1000
      case Second | Seconds =>
        length * 1000 * 1000 * 1000
      case Minute | Minutes =>
        length * 1000 * 1000 * 1000 * 60
      case Hour | Hours =>
        length * 1000 * 1000 * 1000 * 60 * 60
      case Day | Days =>
        length * 1000 * 1000 * 1000 * 60 * 60 * 24
    }
  }

  private def totalNanosForDoubleLength(length: Double, units: Units): Long = {

    val MaxNanoseconds = (Long.MaxValue).toDouble
    // TODO: Need others here
    val MaxSeconds = (Long.MaxValue / 1000 / 1000 / 1000).toDouble
    val MaxMinutes = (Long.MaxValue / 1000 / 1000 / 1000 / 60).toDouble
    val MaxHours = (Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60).toDouble
    val MaxDays = (Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60 / 24).toDouble

    require(length >= 0, "length must be greater than or equal to zero, but was: " + length)

    require(units != Nanosecond || length == 1.0, singularErrorMsg("Nanosecond"))
    require(units != Microsecond || length == 1.0, singularErrorMsg("Microsecond"))
    require(units != Millisecond || length == 1.0, singularErrorMsg("Millisecond"))
    require(units != Second || length == 1.0, singularErrorMsg("Second"))
    require(units != Minute || length == 1.0, singularErrorMsg("Minute"))
    require(units != Hour || length == 1.0, singularErrorMsg("Hour"))
    require(units != Day || length == 1.0, singularErrorMsg("Day"))

    require(units != Nanoseconds || length <= MaxNanoseconds, "Passed length, " + length + ", is larger than the largest expressible number of nanoseconds: Long.MaxValue")
    // TODO: Am I missing some here? Think so.
    require(units != Seconds || length <= MaxSeconds, "Passed length, " + length + ", is larger than the largest expressible number of seconds: Long.MaxValue / 1000")
    require(units != Minutes || length <= MaxMinutes, "Passed length, " + length + ", is larger than the largest expressible number of minutes: Long.MaxValue / 1000 / 60")
    require(units != Hours || length <= MaxHours, "Passed length, " + length + ", is larger than the largest expressible number of hours: Long.MaxValue / 1000 / 60 / 60")
    require(units != Days || length <= MaxDays, "Passed length, " + length + ", is larger than the largest expressible number of days: Long.MaxValue / 1000 / 60 / 60 / 24")

    units match {
      case Nanosecond | Nanoseconds =>
        length.toLong
      case Microsecond | Microseconds =>
        (length * 1000).toLong
      case Millisecond | Milliseconds | Millis =>
        (length * 1000 * 1000).toLong
      case Second | Seconds =>
        (length * 1000 * 1000 * 1000).toLong
      case Minute | Minutes =>
        (length * 1000 * 1000 * 1000 * 60).toLong
      case Hour | Hours =>
        (length * 1000 * 1000 * 1000 * 60 * 60).toLong
      case Day | Days =>
        (length * 1000 * 1000 * 1000 * 60 * 60 * 24).toLong
    }
  }

  private def singularErrorMsg(unitsString: String) = {
    "Singular form of " + unitsString +
      " (i.e., without the trailing s) can only be used with the value 1. Use " +
      unitsString + "s (i.e., with an s) instead."
  }

   /*
  // TODO: write test for: Can't pass anything but zero for nanos if Long.MaxInt is passed for millis.
  def apply(length: Long, units: Units): Span = {
     // TODO: Need others here
    val MaxSeconds = Long.MaxValue / 1000 / 1000 / 1000
    val MaxMinutes = Long.MaxValue / 1000 / 1000 / 1000 / 60
    val MaxHours = Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60
    val MaxDays = Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60 / 24

    require(length >= 0, "length must be greater than or equal to zero, but was: " + length)

    require(units != Nanosecond || length == 1, singularErrorMsg("Nanosecond"))
    require(units != Microsecond || length == 1, singularErrorMsg("Microsecond"))
    require(units != Millisecond || length == 1, singularErrorMsg("Millisecond"))
    require(units != Second || length == 1, singularErrorMsg("Second"))
    require(units != Minute || length == 1, singularErrorMsg("Minute"))
    require(units != Hour || length == 1, singularErrorMsg("Hour"))
    require(units != Day || length == 1, singularErrorMsg("Day"))

    require(units != Seconds || length <= MaxSeconds, "Passed length, " + length + ", is larger than the largest expressible number of seconds: Long.MaxValue / 1000")
    require(units != Minutes || length <= MaxMinutes, "Passed length, " + length + ", is larger than the largest expressible number of minutes: Long.MaxValue / 1000 / 60")
    require(units != Hours || length <= MaxHours, "Passed length, " + length + ", is larger than the largest expressible number of hours: Long.MaxValue / 1000 / 60 / 60")
    require(units != Days || length <= MaxDays, "Passed length, " + length + ", is larger than the largest expressible number of days: Long.MaxValue / 1000 / 60 / 60 / 24")

    units match {
      case Nanosecond | Nanoseconds =>
        new Span(length)
      case Microsecond | Microseconds =>
        new Span(length * 1000)
      case Millisecond | Milliseconds | Millis =>
        new Span(length * 1000 * 1000)
      case Second | Seconds =>
        new Span(length * 1000 * 1000 * 1000)
      case Minute | Minutes =>
        new Span(length * 1000 * 1000 * 1000 * 60)
      case Hour | Hours =>
        new Span(length * 1000 * 1000 * 1000 * 60 * 60)
      case Day | Days =>
        new Span(length * 1000 * 1000 * 1000 * 60 * 60 * 24)
    }
  }

  def apply(length: Double, units: Units): Span = {

    val MaxNanoseconds = (Long.MaxValue).toDouble
    // TODO: Need others here
    val MaxSeconds = (Long.MaxValue / 1000 / 1000 / 1000).toDouble
    val MaxMinutes = (Long.MaxValue / 1000 / 1000 / 1000 / 60).toDouble
    val MaxHours = (Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60).toDouble
    val MaxDays = (Long.MaxValue / 1000 / 1000 / 1000 / 60 / 60 / 24).toDouble

    require(length >= 0, "length must be greater than or equal to zero, but was: " + length)

    require(units != Nanosecond || length == 1.0, singularErrorMsg("Nanosecond"))
    require(units != Microsecond || length == 1.0, singularErrorMsg("Microsecond"))
    require(units != Millisecond || length == 1.0, singularErrorMsg("Millisecond"))
    require(units != Second || length == 1.0, singularErrorMsg("Second"))
    require(units != Minute || length == 1.0, singularErrorMsg("Minute"))
    require(units != Hour || length == 1.0, singularErrorMsg("Hour"))
    require(units != Day || length == 1.0, singularErrorMsg("Day"))

    require(units != Nanoseconds || length <= MaxNanoseconds, "Passed length, " + length + ", is larger than the largest expressible number of nanoseconds: Long.MaxValue")
    // TODO: Am I missing some here? Think so.
    require(units != Seconds || length <= MaxSeconds, "Passed length, " + length + ", is larger than the largest expressible number of seconds: Long.MaxValue / 1000")
    require(units != Minutes || length <= MaxMinutes, "Passed length, " + length + ", is larger than the largest expressible number of minutes: Long.MaxValue / 1000 / 60")
    require(units != Hours || length <= MaxHours, "Passed length, " + length + ", is larger than the largest expressible number of hours: Long.MaxValue / 1000 / 60 / 60")
    require(units != Days || length <= MaxDays, "Passed length, " + length + ", is larger than the largest expressible number of days: Long.MaxValue / 1000 / 60 / 60 / 24")

    units match {
      case Nanosecond | Nanoseconds =>
        new Span(length.toLong)
      case Microsecond | Microseconds =>
        new Span((length * 1000).toLong)
      case Millisecond | Milliseconds | Millis =>
        new Span((length * 1000 * 1000).toLong)
      case Second | Seconds =>
        new Span((length * 1000 * 1000 * 1000).toLong)
      case Minute | Minutes =>
        new Span((length * 1000 * 1000 * 1000 * 60).toLong)
      case Hour | Hours =>
        new Span((length * 1000 * 1000 * 1000 * 60 * 60).toLong)
      case Day | Days =>
        new Span((length * 1000 * 1000 * 1000 * 60 * 60 * 24).toLong)
      case _ => new Span(0)
    }
  } */
}        // TODO: elim duplica

