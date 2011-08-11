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
import java.util.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.Stack
import scala.collection.mutable.ListBuffer

/**
 * A <code>Reporter</code> that writes test status information in Flex format.
 */
private[scalatest] class FlexReporter(directory: String) extends Reporter {

  final val BufferSize = 4096

  private val events = ListBuffer[Event]()
  private var index = 0

  //
  // Records events as they are received.  Initiates processing once
  // a run-termination event comes in.
  //
  def apply(event: Event) {
    event match {
      case _: RunStarting  =>
      case _: RunCompleted => writeFiles(event)
      case _: RunStopped   => writeFiles(event)
      case _: RunAborted   => writeFiles(event)
      case _ => events += event
    }
  }

  //
  // Provides sequential index values for xml entries.
  //
  def nextIndex(): Int = {
    index += 1
    index
  }

  def unexpectedEvent(e: Event) {
    throw new RuntimeException("unexpected event [" + e + "]")
  }

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

  //
  // Formats a timestamp for use in generating output filename,
  // e.g. 110815063748 for Aug 15, 2011 06:37:48.
  //
  def formatTimestamp(timestamp: Long): String = {
    val df = new SimpleDateFormat("yyMMddHHmmss")
    df.format(new Date(timestamp))
  }

  //
  // Formats date for inclusion in as 'date' attribute in xml.
  //
  // E.g.: "Mon May 30 10:29:58 PDT 2011"
  //
  def formatDate(timeStamp: Long): String = {
    val df = new SimpleDateFormat("EEE MMM d kk:mm:ss zzz yyyy")
    df.format(new Date(timeStamp))
  }

  //
  // Writes output file suitedata-[timestamp].xml to specified directory.
  //
  def writeFiles(event: Event) {
    index = 0
    var suiteRecord: SuiteRecord = null

    //
    // Formats <summary> element of output xml.
    //
    def formatSummary(event: Event): String = {
      val (summaryOption, durationOption) =
        event match {
          case e: RunCompleted => (e.summary, e.duration)
          case e: RunAborted   => (e.summary, e.duration)
          case e: RunStopped   => (e.summary, e.duration)
          case _ => unexpectedEvent(event); (None, None)
        }

      val summary  = summaryOption.getOrElse(Summary(0, 0, 0, 0, 0, 0, 0))
      val duration = durationOption.getOrElse(0)

      "<summary index=\"" + nextIndex() + "\" text=\"\" " +
      "duration=\""             + duration                     + "\" " +
      "testsSucceededCount=\""  + summary.testsSucceededCount  + "\" " +
      "testsFailedCount=\""     + summary.testsFailedCount     + "\" " +
      "testsIgnoredCount=\""    + summary.testsIgnoredCount    + "\" " +
      "testsPendingCount=\""    + summary.testsPendingCount    + "\" " +
      "testsCancelledCount=\""  + summary.testsCanceledCount   + "\" " +
      "suitesCompletedCount=\"" + summary.suitesCompletedCount + "\" " +
      "suitesAbortedCount=\""   + summary.suitesAbortedCount   + "\" " +
      "date=\""                 + formatDate(event.timeStamp)  + "\" " +
      "thread=\""               + event.threadName             + "\"/>"
    }

    //
    // writeFiles main
    //
    val timestampStr = formatTimestamp(event.timeStamp)
    val pw =
      new PrintWriter(
        new BufferedOutputStream(
          new FileOutputStream(
            new File(directory, "suitedata-" + timestampStr + ".xml")),
                     BufferSize))

    pw.println("<doc>")
    pw.println(formatSummary(event))

    val sortedEvents = events.toList.sortWith((a, b) => a.ordinal < b.ordinal)

    for (event <- sortedEvents) {
      event match {
        case e: SuiteStarting =>
          suiteRecord = new SuiteRecord(e)
          
        case e: InfoProvided   => suiteRecord.addNestedEvent(e)
        case e: MarkupProvided => suiteRecord.addNestedEvent(e)
        case e: TestStarting   => suiteRecord.addNestedEvent(e)
        case e: TestSucceeded  => suiteRecord.addNestedEvent(e)
        case e: TestIgnored    => suiteRecord.addNestedEvent(e)
        case e: TestFailed     => suiteRecord.addNestedEvent(e)
        case e: TestPending    => suiteRecord.addNestedEvent(e)
        case e: TestCanceled   => suiteRecord.addNestedEvent(e)

        case e: SuiteCompleted =>
          suiteRecord.addEndEvent(e)
          pw.println(suiteRecord.toXml)

        case e: SuiteAborted =>
          suiteRecord.addEndEvent(e)
          pw.println(suiteRecord.toXml)

        case e: RunStarting  => unexpectedEvent(e)
        case e: RunCompleted => unexpectedEvent(e)
        case e: RunStopped   => unexpectedEvent(e)
        case e: RunAborted   => unexpectedEvent(e)
      }
    }
    pw.println("</doc>")
    pw.flush()
    pw.close()
  }

//    val stack = new Stack[Int]
//      if (!stack.isEmpty) {
//       event.formatter match {
//          case Some(IndentedText(_, _, level)) =>
//            if (level == stack.head) {
//              stack.pop()
//              pw.println("</info>")
//            }
//          case _ =>
//        }
//      }

//        case e: SuiteCompleted =>
//          while (!stack.isEmpty) {
//            stack.pop()
//            pw.println("</info>")
//          }
//
//        case e: SuiteAborted =>
//          while (!stack.isEmpty) {
//            stack.pop()
//            pw.println("</info>")
//          }
// 
//        case InfoProvided(ordinal, message, nameInfo, aboutAPendingTest,
//                          throwable, formatter, location, payload, threadName,
//                          timeStamp)
//        =>
//          getLevel(formatter) match {
//            case Some(level) => stack.push(level)
//            case None =>
//          }
//          pw.println("<info label=\"" + escape(message) + "\">")
//
//        case MarkupProvided(ordinal, text, nameInfo, aboutAPendingTest,
//                            throwable, formatter, location, payload,
//                            threadName, timeStamp)
//        =>
//          pw.println("<markup date=\"" + timeStamp + "\"" +
//                     " thread=\"" + threadName + "\">")
//          pw.println("  <data><![CDATA[" + text + "]]></data>")
//          pw.println("</markup>")

  def formatInfoProvided(event: InfoProvided): String = {
    "<info index=\"" + nextIndex()                 + "\" " +
    "text=\""        + escape(event.message)       + "\" " +
    "date=\""        + formatDate(event.timeStamp) + "\" " +
    "thread=\""      + event.threadName            + "\"/>"
  }

  def formatMarkupProvided(event: MarkupProvided): String = {
    "<markup index=\"" + nextIndex()                 + "\" "   +
    "date=\""          + formatDate(event.timeStamp) + "\" "   +
    "thread=\""        + event.threadName            + "\">\n" +
    "<data><![CDATA["  + event.text                  + "]]></data>\n" +
    "</markup>\n"
  }

  //
  // Aggregates events that make up a suite.
  //
  // A <suite> element can't be written until its end event has been
  // processed, so this holds all the events encountered from SuiteStarting
  // through its corresponding end event (e.g. SuiteCompleted).  Once the
  // end event is received, this class's toXml method can be called to
  // generate the complete xml string for the <suite> element.
  //
  class SuiteRecord(startEvent: SuiteStarting) {
    var nestedEvents = List[Event]()
    var endEvent: Event = null

    def addNestedEvent(event: Event) {
      def isNestedEvent(e: Event): Boolean = {
        e match {
          case _: TestStarting   => true
          case _: TestSucceeded  => true
          case _: TestIgnored    => true
          case _: TestFailed     => true
          case _: TestPending    => true
          case _: TestCanceled   => true
          case _: InfoProvided   => true
          case _: MarkupProvided => true
          case _ => false
        }
      }

      require(isNestedEvent(event))

      nestedEvents ::= event
    }

    def addEndEvent(event: Event) {
      def isEndEvent(e: Event): Boolean = {
        e match {
          case _: SuiteCompleted => true
          case _: SuiteAborted   => true
          case _ => false
        }
      }

      require(endEvent == null)
      require(isEndEvent(event))

      endEvent = event
    }

    def result: String = {
      endEvent match {
        case _: SuiteCompleted => "completed"
        case _: SuiteAborted   => "aborted"
        case _ => unexpectedEvent(endEvent); ""
      }
    }

    def toXml: String = {
      val buf = new StringBuilder
      var testRecord: TestRecord = null

      def inATest: Boolean =
        (testRecord != null) && (testRecord.endEvent == null)

      buf.append(
        "<suite index=\"" + nextIndex()                      + "\" " +
        "result=\""       + result                           + "\" " +
        "name=\""         + escape(startEvent.suiteName)     + "\" " +
        "date=\""         + formatDate(startEvent.timeStamp) + "\" " +
        "thread=\""       + startEvent.threadName            + "\">\n")

      for (event <- nestedEvents.reverse) {
        if (inATest) {
          testRecord.addEvent(event)

          if (testRecord.isComplete)
            buf.append(testRecord.toXml)
        }
        else {
          event match {
            case e: InfoProvided   => buf.append(formatInfoProvided(e))
            case e: MarkupProvided => buf.append(formatMarkupProvided(e))
            case e: TestStarting   => testRecord = new TestRecord(e)
            case _ => unexpectedEvent(event)
          }
        }
      }
      buf.toString + "</suite>\n"
    }
  }

  //
  // Aggregates events that make up a test.
  //
  // A <test> element can't be written until its end event has been
  // processed, so this holds all the events encountered from TestStarting
  // through its corresponding end event (e.g. TestSucceeded).  Once the
  // end event is received, this class's toXml method can be called to
  // generate the complete xml string for the <test> element.
  //
  class TestRecord(startEvent: TestStarting) {
    var nestedEvents = List[Event]()
    var endEvent: Event = null

    def addEvent(event: Event) {
      def isNestedEvent: Boolean = {
        event match {
          case _: InfoProvided => true
          case _: MarkupProvided => true
          case _ => false
        }
      }

      def isEndEvent: Boolean = {
        event match {
          case _: TestSucceeded => true
          case _: TestFailed => true
          case _: TestPending => true
          case _: TestIgnored => true
          case _: TestCanceled => true
          case _ => false
        }
      }

      if (isNestedEvent)
        nestedEvents ::= event
      else if (isEndEvent)
        endEvent = event
      else
        unexpectedEvent(event)
    }

    def isComplete: Boolean = (endEvent != null)

    def formatTestStart: String = {
      "<test index=\"" + nextIndex() + "\" " +
      "text=\"" + testMessage(startEvent.testName, startEvent.formatter) +
      "\" " +
      "name=\"" + startEvent.testName + "\" " +
      "date=\"" + formatDate(startEvent.timeStamp) + "\" " +
      "thread=\"" + startEvent.threadName + "\"" +
      ">\n"
    }

    //
    // Extracts message from specified formatter if there is one, otherwise
    // returns test name.
    //
    def testMessage(testName: String, formatter: Option[Formatter]): String =
    {
      val message =
        formatter match {
          case Some(IndentedText(_, rawText, _)) => rawText
          case _ => testName
        }
      escape(message)
    }

    def toXml: String = {
      val buf = new StringBuilder

      if (endEvent == null)
        throw new IllegalStateException("toXml called without endEvent")

      buf.append(formatTestStart)

      for (event <- nestedEvents) {
        event match {
          case e: InfoProvided   => buf.append(formatInfoProvided(e))
          case e: MarkupProvided => buf.append(formatMarkupProvided(e))
          case _ => unexpectedEvent(event)
        }
      }

      buf.append("</test>\n")
      buf.toString
    }
  }
}

