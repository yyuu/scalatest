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

import org.scalatest._

private[scalatest] class DistributedTestRunnerSuite(suiteFun: () => ParallelTestExecution, testName: String) extends Suite {
  println("DistributedTestRunnerSuite got created")
  override def run(ignoreThisTestName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
          configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
    // suite.runOneTest(testName, reporter, stopper, configMap, tracker)
    println("run was called on the DistributedTestRunnerSuite for " + testName)
    val suite = suiteFun()
    // TODO: when suiteFun() threw an exception, I saw it firing a SuiteAborted, but I never saw the Suite aborted come out to
    // the GUI reporter. This was when testing FunSpecSpec.
    suite.run(Some(testName), reporter, stopper, filter, configMap, None, tracker)
  }
}
