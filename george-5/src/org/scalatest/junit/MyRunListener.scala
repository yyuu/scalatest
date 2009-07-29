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
package org.scalatest.junit;

import org.scalatest.Suite
import org.junit.runner.JUnitCore
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.Failure
import org.junit.runner.Description
import org.junit.runner.Result
import org.scalatest.events._

  private[junit] class MyRunListener(report: Reporter,
                                     config: Map[String, Any],
                                     theTracker: Tracker,
                                     thisSuite: Suite)
  extends RunListener {

    override def testFailure(failure: Failure) {
      val testName = getTestNameFromDescription(failure.getDescription)
      val throwableOrNull = failure.getException
      val throwable = if (throwableOrNull != null) Some(throwableOrNull) else None
      val message = if (throwableOrNull != null) throwableOrNull.getMessage else Resources("jUnitTestFailed")
      report(TestFailed(theTracker.nextOrdinal(), message, thisSuite.suiteName, Some(thisSuite.getClass.getName), testName, throwable)) // TODO: can I add a duration?
    }

    override def testFinished(description: Description) {
      val testName = getTestNameFromDescription(description)
      report(TestSucceeded(theTracker.nextOrdinal(), thisSuite.suiteName, Some(thisSuite.getClass.getName), testName)) // TODO: can I add a duration?
    }

    override def testIgnored(description: Description) {
      val testName = getTestNameFromDescription(description)
      report(TestIgnored(theTracker.nextOrdinal(), thisSuite.suiteName, Some(thisSuite.getClass.getName), testName))
    }

    override def testRunFinished(result: Result) {
      report(RunCompleted(theTracker.nextOrdinal()))
    }

    override def testRunStarted(description: Description) {
      report(RunStarting(theTracker.nextOrdinal(), description.testCount, config))
    }

    override def testStarted(description: Description) {
      val testName = getTestNameFromDescription(description)
      report(TestStarting(theTracker.nextOrdinal(), thisSuite.suiteName, Some(thisSuite.getClass.getName), testName))
    }

    private def getTestNameFromDescription(description: Description): String = {
      val displayName = description.getDisplayName
      val index = displayName.indexOf('(')
      if (index >= 0) displayName.substring(0, index) else displayName
    }
  }
