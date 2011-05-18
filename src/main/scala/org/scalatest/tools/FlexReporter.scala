/*
 * Copyright 2001-2011 Artima, Inc.
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

import org.scalatest.events._
import org.scalatest.Reporter
import org.scalatest.events.MotionToSuppress
import java.io.PrintWriter
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.File

import scala.collection.mutable.ListBuffer

/**
 * A <code>Reporter</code> that writes test status information in Flex format.
 */
private[scalatest] class FlexReporter(directory: String) extends Reporter {

  final val BufferSize = 4096

  private val events = ListBuffer[Event]()

  //
  // Records events in 'events' set, except for events that
  // have a MotionToSuppress formatter.
  //
  def apply(event: Event) {
    if (!event.formatter.exists(f => f == MotionToSuppress))
      events += event
    event match {
      case _: RunCompleted => writeFiles()
      case _: RunAborted => writeFiles()
      case _ =>
    }
  }

  def writeFiles() {
    val sortedEvents = events.toList.sortWith((a, b) => a.ordinal > b.ordinal)
    val pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File(directory, "index.html")), BufferSize))
    for (eve <- sortedEvents) {
      pw.println(eve)
    }
  }
}

