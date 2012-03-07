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
package org.scalatest.concurrent

import org.scalatest.concurrent.Durations.Duration

trait Durations {

  sealed abstract class TimeUnits

  case object Nanosecond extends TimeUnits
  case object Nanoseconds extends TimeUnits
  case object Microsecond extends TimeUnits
  case object Microseconds extends TimeUnits
  case object Millisecond extends TimeUnits
  case object Milliseconds extends TimeUnits
  case object Millis extends TimeUnits
  case object Second extends TimeUnits
  case object Seconds extends TimeUnits
  case object Minute extends TimeUnits
  case object Minutes extends TimeUnits
  case object Hour extends TimeUnits
  case object Hours extends TimeUnits
  case object Day extends TimeUnits
  case object Days extends TimeUnits

  case class Duration private (m: Long, n: Int = 0) extends DurationConcept(m, n)
  
  object Duration {
    private final val NanosDivisor = 1000000
    private final val MicrosDivisor = 1000
    def apply(length: Long,  units: TimeUnits): Duration = {
      require(length >= 0, "length must be greater than or equal to zero, but was: " + length)
      units match {
        case Nanosecond | Nanoseconds =>
          new Duration(length / NanosDivisor, (length % NanosDivisor).toInt)
        case Microsecond | Microseconds =>
          new Duration(length / MicrosDivisor, (length % MicrosDivisor).toInt * 1000)
        case _ => new Duration(0)
      }
    }
  }
}

object Durations extends Durations