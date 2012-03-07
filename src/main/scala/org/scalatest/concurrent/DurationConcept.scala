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

abstract class DurationConcept(val millis: Long, val nanos: Int = 0) {
  require(millis >= 0, "millis must be greater than or equal to zero, but was: " + millis)
  require(nanos >= 0 && nanos <= 999999, "nanos must be greater than or equal to zero and less than or equal to 999999, but was: " + nanos)
  require(millis != Long.MaxValue || nanos == 0, "millis was Long.MaxValue, so nanos must be 0, but was: " + nanos)
}
