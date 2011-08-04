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
//import org.scalatest.ResourcefulReporter
import org.scalatest.Reporter
import org.scalatest.events.MotionToSuppress
import java.io.PrintWriter
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.File
import scala.collection.mutable.Stack

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
    // if (!event.formatter.exists(f => f == MotionToSuppress))
    events += event
    event match {
      case _: RunCompleted => writeFiles()
      case _: RunStopped => writeFiles()
      case _: RunAborted => writeFiles()
      case _ =>
    }
  }

  //
  // Writes a file for each 
  def writeFiles() {

    //
    // Escapes any curly braces in specified string.
    //
    def escape(s: String): String =
      scala.xml.Utility.escape(s).
        replaceAll("""\{""", """\\{""").
        replaceAll("""\}""", """\\}""")

    //
    // Determines indentation level of specified formatter.
    //
    def getLevel(formatter: Option[Formatter]): Option[Int] = {
      formatter flatMap { f =>
        f match {                  
          case MotionToSuppress => None
          case IndentedText(_, _, level) => Some(level)
        }                                      
      } 
    }

    val sortedEvents = events.toList.sortWith((a, b) => a.ordinal < b.ordinal)
    val pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File(directory, "index.html")), BufferSize))
    val stack = new Stack[Int]

    for (event <- sortedEvents) {
      if (!stack.isEmpty) {
        event.formatter match {
          case Some(IndentedText(_, _, level)) =>
            if (level == stack.head) {
              stack.pop()
              pw.println("</info>")
            }
          case _ =>
        }
      }

      def testMessage(testName: String, formatter: Option[Formatter]): String =
        formatter match {
          case Some(IndentedText(_, rawText, _)) => rawText
          case _ => testName
        }

      event match {
        //gcbx// case MarkupProvided()
        //gcbx// case TestCanceled()

        case RunStarting(ordinal, testCount, configMap, formatter, location, payload, threadName, timeStamp) =>

          if (testCount < 0)
            throw new IllegalArgumentException

          pw.println("<run/>")

        case RunCompleted(ordinal, duration, summary, formatter, location, payload, threadName, timeStamp) =>

          pw.println("<run/>")
 
        case RunStopped(ordinal, duration, summary, formatter, location, payload, threadName, timeStamp) =>
 
          pw.println("<run/>")
 
        case RunAborted(ordinal, message, throwable, duration, summary, formatter, location, payload, threadName, timeStamp) =>

          pw.println("<run/>")

        case SuiteStarting(ordinal, suiteName, suiteClassName, formatter, rerunnable, location, payload, threadName, timeStamp) =>

          pw.println("<suite label=\"" + escape(suiteName) + ":\">")

        case SuiteCompleted(ordinal, suiteName, suiteClassName, duration, formatter, rerunnable, location, payload, threadName, timeStamp) =>

           while (!stack.isEmpty) {
            stack.pop()
            pw.println("</info>")
          }
          pw.println("</suite>")

        case SuiteAborted(ordinal, message, suiteName, suiteClassName, throwable, duration, formatter, rerunnable, location, payload, threadName, timeStamp) =>

          pw.println("</suite>")
 
        case TestStarting(ordinal, suiteName, suiteClassName, testName, formatter, rerunnable, location, payload, threadName, timeStamp) =>
       
        case TestSucceeded(ordinal, suiteName, suiteClassName, testName, duration, formatter, rerunnable, location, payload, threadName, timeStamp) =>
        
          // Tests are always closed right away. It is infos that I close when level goes up then back. No, it is as soon as I see another one at
          // the exact same level.
          pw.println("<test label=\"" + escape(testMessage(testName, formatter)) + "\"/>")

        case TestIgnored(ordinal, suiteName, suiteClassName, testName, formatter, location, payload, threadName, timeStamp) =>

          pw.println("<test label=\"" + escape(testMessage(testName, formatter)) + "\"/>")

        case TestFailed(ordinal, message, suiteName, suiteClassName, testName, throwable, duration, formatter, rerunnable, location, payload, threadName, timeStamp) =>

          pw.println("<test label=\"" + escape(testMessage(testName, formatter)) + "\"/>")

        case TestPending(ordinal, suiteName, suiteClassName, testName, formatter, location, payload, threadName, timeStamp) =>

          pw.println("<test label=\"" + escape(testMessage(testName, formatter)) + "\"/>")
 
        case InfoProvided(ordinal, message, nameInfo, aboutAPendingTest, throwable, formatter, location, payload, threadName, timeStamp) =>
      
          getLevel(formatter) match {
            case Some(level) => stack.push(level)
            case None =>
          }
          pw.println("<info label=\"" + escape(message) + "\">")
      }
    }
    pw.flush()
    pw.close()
  }
}

