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
package org.scalatest.tools

import org.scalatest.events.Event
import org.scalatest.Reporter
import org.scalatest.events.MotionToSuppress

import scala.collection.mutable.ListBuffer


/**
 * A <code>Reporter</code> that writes test status information in Flex format.
 */
private[scalatest] class FlexReporter(directory: String) extends Reporter {

  private val events = ListBuffer[Event]()

  //
  // Records events in 'events' set, except for InfoProvided events that
  // have a MotionToSuppress formatter.
  //
  def apply(event: Event) {
    if (!event.formatter.exists(f => f == MotionToSuppress))
      events += event
  }

  def getSortedEvents: List[Event] = {
    def ordinalLt(a: List[Int], b: List[Int]): Boolean = {
      if (a.size == 0)
        b.size > 0
      else if (b.size == 0)
        false
      else if (a(0) == b(0))
        ordinalLt(a.tail, b.tail)
      else
        a(0) < b(0)
    }

    events.toList.sortWith((a, b) =>
      ordinalLt(a.ordinal.toList, b.ordinal.toList))
  }
}
