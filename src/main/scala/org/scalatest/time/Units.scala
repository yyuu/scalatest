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

sealed abstract class Units

case object Nanosecond extends Units
case object Nanoseconds extends Units
case object Microsecond extends Units
case object Microseconds extends Units
case object Millisecond extends Units
case object Milliseconds extends Units
case object Millis extends Units
case object Second extends Units
case object Seconds extends Units
case object Minute extends Units
case object Minutes extends Units
case object Hour extends Units
case object Hours extends Units
case object Day extends Units
case object Days extends Units

