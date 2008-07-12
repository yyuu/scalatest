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

import java.lang.reflect.Constructor
import java.lang.reflect.Modifier

/**
 * @author Bill Venners
 * @author Josh Cough
 */
private[scalatest] class SuiteRunner(suite: Suite, dispatchReporter: DispatchReporter, stopper: Stopper, includes: Set[String],
    excludes: Set[String], propertiesMap: Map[String, Any], distributor: Option[Distributor]) extends Runnable {

  def run(): Unit = {
    if( stopper.stopRequested ) return
    
    this.dispatchSuiteStarting
  
    try {
      suite.execute(None, dispatchReporter, stopper, includes, excludes, propertiesMap, distributor)
      this.dispatchSuiteCompleted
    }
    catch {
      case e: RuntimeException => dispatchSuiteAborted(e)
    }
  }
    
  def rerunnable: Option[Rerunnable] = {
    if (hasPublicNoArgConstructor)
      Some(new SuiteRerunner(suite.getClass.getName))
    else
      None
  }
  
  // Create a Rerunnable if the Suite has a no-arg constructor
  def hasPublicNoArgConstructor: Boolean = {
    try {
      val constructor: Constructor[_ <: AnyRef] = suite.getClass.getConstructor(Array[java.lang.Class[_]]())
      Modifier.isPublic(constructor.getModifiers())
    }
    catch {
      case nsme: NoSuchMethodException => false
    }
  }
  
  def dispatchSuiteStarting = {
    dispatchReporter.suiteStarting(buildReport("suiteExecutionStarting", None))
  }
  
  def dispatchSuiteCompleted = {
    dispatchReporter.suiteCompleted(buildReport("suiteCompletedNormally", None))
  }

  def dispatchSuiteAborted(e: RuntimeException) = {
      dispatchReporter.suiteAborted(buildReport("executeException", Some(e)))
  }
  
  def buildReport( resourceName: String, o: Option[Throwable] ) : Report = {
    if (hasPublicNoArgConstructor)
      new Report(suite.suiteName, Resources(resourceName), o, rerunnable)
    else
      new Report(suite.suiteName, Resources(resourceName), o, None)
  }
}
