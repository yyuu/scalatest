/*
 * Copyright 2001-2008 Artima, Inc.
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

trait Durations {

  sealed abstract class TimeUnit

  case object Day extends TimeUnit
  case object Days extends TimeUnit
  case object Hour extends TimeUnit
  case object Hours extends TimeUnit
  case object Millis extends TimeUnit
  case object Millisecond extends TimeUnit
  case object Milliseconds extends TimeUnit
  case object Minute extends TimeUnit
  case object Minutes extends TimeUnit
  case object Second extends TimeUnit
  case object Seconds extends TimeUnit
  case object Nanosecond extends TimeUnit
  case object Nanoseconds extends TimeUnit
  case object Microsecond extends TimeUnit
  case object Microseconds extends TimeUnit
  
  case class Duration private (m: Long, n: Int = 0) extends DurationConcept(m, n)
  
  object Duration {
    def apply(length: Long,  units: TimeUnit): Duration = new Duration(0)
  }
}

