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

import org.scalatest.events._

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Enumeration
import java.net.UnknownHostException
import java.net.InetAddress

import scala.collection.mutable.Set
import scala.collection.mutable.ListBuffer
import scala.xml

/**
 * A <code>Reporter</code> that prints test status information in XML format.
 *
 * A separate file is written for each test suite, named TEST-[classname].xml.
 *
 * @exception IOException if unable to open the file for writing
 *
 * @author Bill Venners
 */
private[scalatest] class XmlReporter() extends Reporter {
  val events = Set.empty[Event]

  def apply(event: Event) {
    events += event

    event match {
      case RunStarting(ordinal, testCount, configMap, formatter, payload,
                       threadName, timeStamp) => 

        System.out.println("gcbx RunStarting [" + event + "]");

      case RunCompleted(ordinal, duration, summary, formatter, payload,
                        threadName, timeStamp) => 

        System.out.println("gcbx RunCompleted [" + event + "]");
        val xml = xmlify(events)

      case RunStopped(ordinal, duration, summary, formatter, payload,
                      threadName, timeStamp) =>

        System.out.println("gcbx RunStopped [" + event + "]");

      case RunAborted(ordinal, message, throwable, duration, summary,
                      formatter, payload, threadName, timeStamp) => 

        System.out.println("gcbx RunAborted [" + event + "]");

      case SuiteStarting(ordinal, suiteName, suiteClassName, formatter,
                         rerunnable, payload, threadName, timeStamp) =>

        System.out.println("gcbx SuiteStarting [" + event + "]");

      case SuiteCompleted(ordinal, suiteName, suiteClassName, duration,
                          formatter, rerunnable, payload, threadName,
                          timeStamp) => 

        System.out.println("gcbx SuiteCompleted [" + event + "]");

      case SuiteAborted(ordinal, message, suiteName, suiteClassName, throwable,
                        duration, formatter, rerunnable, payload, threadName,
                        timeStamp) => 

        System.out.println("gcbx SuiteAborted [" + event + "]");

      case TestStarting(ordinal, suiteName, suiteClassName, testName, formatter,
                        rerunnable, payload, threadName, timeStamp) =>

        System.out.println("gcbx TestStarting [" + event + "]");

      case TestSucceeded(ordinal, suiteName, suiteClassName, testName, duration,
                         formatter, rerunnable, payload, threadName,
                         timeStamp) => 

        System.out.println("gcbx TestSucceeded [" + event + "]");
    
      case TestIgnored(ordinal, suiteName, suiteClassName, testName, formatter,
                       payload, threadName, timeStamp) => 

        System.out.println("gcbx TestIgnored [" + event + "]");

      case TestFailed(ordinal, message, suiteName, suiteClassName, testName,
                      throwable, duration, formatter, rerunnable, payload,
                      threadName, timeStamp) => 

        System.out.println("gcbx TestFailed [" + event + "]");

      case InfoProvided(ordinal, message, nameInfo, aboutAPendingTest,
                        throwable, formatter, payload, threadName, timeStamp) =>

        System.out.println("gcbx InfoProvided [" + event + "]");

      case TestPending(ordinal, suiteName, suiteClassName, testName, formatter,
                       payload, threadName, timeStamp) =>

        System.out.println("gcbx TestPending [" + event + "]");

      case _ => 

        System.out.println("gcbx _ [" + event + "]");
    }
  }

  def xmlify(events: Set[Event]): String = {
    val testsuites    = collateEvents(events)
    val propertiesXml = genPropertiesXml

    val xmlVal =
      <testsuites>
      {
        for (testsuite <- testsuites) yield {
          val time = testsuite.time / 1000.0

          <testsuite
            errors    = { "" + testsuite.errors   }
            failures  = { "" + testsuite.failures }
            hostname  = { "" + findHostname       }
            name      = { "" + testsuite.name     }
            tests     = { "" + testsuite.tests    }
            time      = { "" + time               }
            timestamp = { "" + formatTimeStamp(testsuite.timeStamp) }>
          { propertiesXml }
          {
            for (testcase <- testsuite.testcases) yield {
              <testcase
                name      = { "" + testcase.name              }
                classname = { "" + strVal(testcase.className) }
                time      = { "" + testcase.time / 1000.0     }
              >
              {
                if (testcase.failure != None) {
                  val failure =
                    (testcase.failure: @unchecked) match { case Some(x) => x }

                  val (throwableType, throwableText) =
                    if (failure.throwable != None) {
                      val throwable =
                        (failure.throwable: @unchecked) match
                        { case Some(x) => x }

                      val throwableType = "" + throwable.getClass
                      val throwableText =
                        "" + throwable + "\nat " +
                        Array.concat(throwable.getStackTrace).mkString("", "\n",
                                                                       "\n")
                      (throwableType, throwableText)
                    }
                    else ("", "")
                  
                  <failure message = { failure.message }
                           type    = { throwableType   } >
                    { throwableText }
                  </failure>
                }
                else
                  xml.NodeSeq.Empty
              }
              </testcase>
            }
          }
            <system-out><![CDATA[]]></system-out>
            <system-err><![CDATA[]]></system-err>
          </testsuite>
        }
      }
      </testsuites>

    val prettified = (new xml.PrettyPrinter(76, 2)).format(xmlVal)
    System.err.println("gcbx prettified [" + prettified + "]");

    "<?xml version=\"1.0\" encoding=]\"UTF-8\" ?>\n" + prettified
  }

  private def strVal(option: Option[Any]): String = {
    option match {
      case Some(x) => "" + x
      case None    => ""
    }
  }

  private def findHostname: String = {
    val localMachine =
      try {
        InetAddress.getLocalHost();
      } catch {
      case e: UnknownHostException =>
        throw new RuntimeException("unexpected unknown host")
      }
    println("gcbx Hostname [" + localMachine.getHostName() + "]");
    localMachine.getHostName
  }

  private def genPropertiesXml: xml.Elem = {
    val sysprops = System.getProperties

    <properties>
    {
      for (name <- asList(sysprops.propertyNames.asInstanceOf[Enumeration[String]]))
        yield <property name={ name } value = { sysprops.getProperty(name) }/>
    }
    </properties>
  }

  private def asList(enumeration: Enumeration[String]): List[String] = {
    val listBuf = new ListBuffer[String]

    while (enumeration.hasMoreElements)
      listBuf += enumeration.nextElement.asInstanceOf[String]

    listBuf.toList
  }

  private def formatTimeStamp(timeStamp: Long): String = {
    val dateFmt = new SimpleDateFormat("yyyy-MM-DD")
    val timeFmt = new SimpleDateFormat("HH:mm:ss")
    dateFmt.format(timeStamp) + "T" + timeFmt.format(timeStamp)
  }

  //
  // Processes set of events to generate a corresponding list of
  // Testsuite objects.
  //
  private def collateEvents(events: Set[Event]): List[Testsuite] = {
    val orderedEvents = events.toList.sort((a,b)=>a<b).toArray
    val testsuites = new ListBuffer[Testsuite]

    //
    // Constructs Testsuite objects from events in orderedEvents array.
    //
    // Accepts a SuiteStarting event and its index within orderedEvents
    // and returns an index to the corresponding SuiteCompleted event.
    // Stores its Testsuite object plus any nested ones into testsuites
    // ListBuffer.
    //
    def processSuite(startEvent: SuiteStarting, startIndex: Int): Int =
    {
      val name =
        startEvent.suiteClassName match {
          case Some(className) => className
          case None            => startEvent.suiteName
        }
      val testsuite = Testsuite(name, startEvent.timeStamp)
      testsuites += testsuite

      var endIndex = 0

      var idx = startIndex + 1
      while ((idx < orderedEvents.size) && (endIndex == 0)) {
        val event = orderedEvents(idx)

        event match {
          case e: SuiteStarting  =>
            val nestedEndIndex = processSuite(e, idx)
            idx = nestedEndIndex + 1

          case e: TestStarting   =>
            val (testEndIndex, testcase) = processTest(e, idx)
            testsuite.testcases += testcase
            idx = testEndIndex + 1
            testsuite.tests += 1
            if (testcase.failure != None) testsuite.failures += 1

          case e: SuiteAborted   =>
            testsuite.errors += 1
            idx += 1

          case e: SuiteCompleted =>
            testsuite.time = e.timeStamp - testsuite.timeStamp
            endIndex = idx

          case e: TestIgnored    => idx += 1
          case e: TestPending    => idx += 1
          case e: InfoProvided   => idx += 1
          case e: RunStarting    => unexpected(e)
          case e: RunCompleted   => unexpected(e)
          case e: RunStopped     => unexpected(e)
          case e: RunAborted     => unexpected(e)
          case e: TestSucceeded  => unexpected(e)
          case e: TestFailed     => unexpected(e)
        }
      }
      endIndex
    }

    //
    // Constructs a Testcase object from events in orderedEvents array.
    //
    // Accepts a TestStarting event and its index within orderedEvents.
    // Returns a Testcase object plus the index to the corresponding
    // test completion event.
    //
    def processTest(startEvent: TestStarting, startIndex: Int):
    (Int, Testcase) =
    {
      val testcase = Testcase(startEvent.testName, startEvent.suiteClassName,
                              startEvent.timeStamp)

      var endIndex = 0
      var idx = startIndex + 1

      while ((idx < orderedEvents.size) && (endIndex == 0)) {
        val event = orderedEvents(idx)

        event match {
          case e: TestSucceeded  =>
            endIndex = idx
            testcase.time = e.timeStamp - testcase.timeStamp

          case e: TestFailed     =>
            endIndex = idx
            testcase.failure = Some(e)
            testcase.time = testcase.timeStamp - e.timeStamp

          case e: SuiteCompleted => unexpected(e)
          case e: TestStarting   => unexpected(e)
          case e: TestIgnored    => unexpected(e)
          case e: TestPending    => unexpected(e)
          case e: InfoProvided   => unexpected(e)
          case e: SuiteStarting  => unexpected(e)
          case e: RunStarting    => unexpected(e)
          case e: RunCompleted   => unexpected(e)
          case e: RunStopped     => unexpected(e)
          case e: RunAborted     => unexpected(e)
          case e: SuiteAborted   => unexpected(e)
        }
      }
      (endIndex, testcase)
    }

    //
    // Throws an exception if an unexpected Event is encountered.
    //
    def unexpected(event: Event) {
      throw new RuntimeException("unexpected event [" + event + "]")
    }

    var idx = 0
    while (idx < orderedEvents.size) {
      val event = orderedEvents(idx)

      event match {
        case e: SuiteStarting  =>
          val endIdx = processSuite(e, idx)
          idx = endIdx + 1

        case e: TestStarting   => unexpected(e)
        case e: TestSucceeded  => unexpected(e)
        case e: TestFailed     => unexpected(e)
        case e: TestIgnored    => unexpected(e)
        case e: TestPending    => unexpected(e)
        case e: SuiteCompleted => unexpected(e)
        case e: SuiteAborted   => unexpected(e)
        case e: RunStarting    => idx += 1
        case e: RunCompleted   => idx += 1
        case e: RunStopped     => idx += 1
        case e: RunAborted     => idx += 1
        case e: InfoProvided   => idx += 1
      }
    }
    testsuites.toList
  }

  private val xmlHeader =
    """|<?xml version="1.0" encoding="UTF-8" ?>
       |<testsuites>
       |""".stripMargin
    
  private val xmlFooter =
    """|</testsuites>
       |""".stripMargin

  private case class Testsuite(name: String, timeStamp: Long) {
    var errors   = 0
    var failures = 0
    var tests    = 0
    var time     = 0L
    val testcases = new ListBuffer[Testcase]
  }

  private case class Testcase(name: String, className: Option[String],
                              timeStamp: Long) {
    var time = 0L
    var failure: Option[TestFailed] = None
  }
}
