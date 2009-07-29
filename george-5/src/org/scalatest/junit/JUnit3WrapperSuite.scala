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

/**
 * A wrapper to allow JUnit 3 tests to be run by the ScalaTest
 * runner.
 *
 * <p>
 * Instances of this trait are not thread safe.
 * </p>
 *
 * @author Bill Venners
 * @author Daniel Watson
 * @author Joel Neely
 */
class JUnit3WrapperSuite(junitClassName: String, loader: ClassLoader) extends Suite {

  // TODO: This may need to be made thread safe, because who
  // knows what Thread JUnit will fire through this
  private var theTracker = new Tracker

  override def run(testName: Option[String],
                   report: Reporter, 
                   stopper: Stopper,
                   filter: Filter,
                   config: Map[String, Any],
                   distributor: Option[Distributor], 
                   tracker: Tracker) {

    theTracker = tracker

    val jUnitCore = new JUnitCore

    jUnitCore.addListener(new MyRunListener(report, config, tracker, this))

    val myClass = Class.forName(junitClassName, false, loader)

    jUnitCore.run(myClass)
  }
}
