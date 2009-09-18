/*
 * Copyright 2001-2009 Artima, Inc.
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
package org.scalatest.tools

import org.scalatest._
import events.{SuiteCompleted, Event}

class SuiteCompletedStatusReporter extends Reporter {
  var count = 0
  val max =
    try {
      System.getProperty("org.scalatest.tools.SuiteCompletedStatusReporter.max", "10").toInt
    }
    catch {
      case _: NumberFormatException => 10
    }
  var startTime = System.currentTimeMillis
  override def apply(event: Event) {
    event match {
      case e: SuiteCompleted =>
        count += 1
        if (count > max) {
          val duration = System.currentTimeMillis - startTime
          println(max.toString + " more SuiteCompleted events received in: " + duration + " ms")
          count = 0
          startTime = System.currentTimeMillis
        }
      case _ =>
    }
  }
}