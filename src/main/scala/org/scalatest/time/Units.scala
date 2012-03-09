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

sealed abstract class Units {
  val singularResourceName: String
  val pluralResourceName: String
}

case object Nanosecond extends Units {
  val singularResourceName: String = "singularNanosecondUnits"
  val pluralResourceName: String = "pluralNanosecondUnits"
}
case object Nanoseconds extends Units {
  val singularResourceName: String = "singularNanosecondUnits"
  val pluralResourceName: String = "pluralNanosecondUnits"
}
case object Microsecond extends Units {
  val singularResourceName: String = "singularMicrosecondUnits"
  val pluralResourceName: String = "pluralMicrosecondUnits"
}
case object Microseconds extends Units {
  val singularResourceName: String = "singularMicrosecondUnits"
  val pluralResourceName: String = "pluralMicrosecondUnits"
}
case object Millisecond extends Units {
  val singularResourceName: String = "singularMillisecondUnits"
  val pluralResourceName: String = "pluralMillisecondUnits"
}
case object Milliseconds extends Units {
  val singularResourceName: String = "singularMillisecondUnits"
  val pluralResourceName: String = "pluralMillisecondUnits"
}
case object Millis extends Units {
  val singularResourceName: String = "singularMillisecondUnits"
  val pluralResourceName: String = "pluralMillisecondUnits"
}
case object Second extends Units {
  val singularResourceName: String = "singularSecondUnits"
  val pluralResourceName: String = "pluralSecondUnits"
}
case object Seconds extends Units {
  val singularResourceName: String = "singularSecondUnits"
  val pluralResourceName: String = "pluralSecondUnits"
}
case object Minute extends Units {
  val singularResourceName: String = "singularMinuteUnits"
  val pluralResourceName: String = "pluralMinuteUnits"
}
case object Minutes extends Units {
  val singularResourceName: String = "singularMinuteUnits"
  val pluralResourceName: String = "pluralMinuteUnits"
}
case object Hour extends Units {
  val singularResourceName: String = "singularHourUnits"
  val pluralResourceName: String = "pluralHourUnits"
}
case object Hours extends Units {
  val singularResourceName: String = "singularHourUnits"
  val pluralResourceName: String = "pluralHourUnits"
}
case object Day extends Units {
  val singularResourceName: String = "singularDayUnits"
  val pluralResourceName: String = "pluralDayUnits"
}
case object Days extends Units {
  val singularResourceName: String = "singularDayUnits"
  val pluralResourceName: String = "pluralDayUnits"
}

