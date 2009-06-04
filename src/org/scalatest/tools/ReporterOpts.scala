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

/**
 * An enumeration of the 12 possible confiuration options accepted
 * the Runner for Reporters.
 *
 * @author Bill Venners
 */
private[scalatest] object ReporterOpts extends Enumeration {
   
  val PresentRunStarting,
    PresentTestStarting,
    PresentTestFailed,
    PresentTestSucceeded,
    PresentTestIgnored,
    PresentSuiteStarting,
    PresentSuiteAborted,
    PresentSuiteCompleted,
    PresentInfoProvided,
    PresentRunStopped,
    PresentRunAborted,
    PresentRunCompleted = Value
       
  // allOptions contains all possible ReporterOpts
  val allOptions =
    ReporterOpts.ValueSet(
      ReporterOpts.PresentRunStarting,
      ReporterOpts.PresentTestStarting,
      ReporterOpts.PresentTestSucceeded,
      ReporterOpts.PresentTestFailed,
      ReporterOpts.PresentTestIgnored,
      ReporterOpts.PresentSuiteStarting,
      ReporterOpts.PresentSuiteCompleted,
      ReporterOpts.PresentSuiteAborted,
      ReporterOpts.PresentInfoProvided,
      ReporterOpts.PresentRunStopped,
      ReporterOpts.PresentRunCompleted,
      ReporterOpts.PresentRunAborted
    )

  def getUpperCaseName(option: ReporterOpts.Value) = option match {
    case ReporterOpts.PresentRunStarting => "REPORT_RUN_STARTING"
    case ReporterOpts.PresentTestStarting => "REPORT_TEST_STARTING"
    case ReporterOpts.PresentTestFailed => "REPORT_TEST_FAILED"
    case ReporterOpts.PresentTestSucceeded => "REPORT_TEST_SUCCEEDED"
    case ReporterOpts.PresentTestIgnored => "REPORT_TEST_IGNORED"
    case ReporterOpts.PresentSuiteStarting => "REPORT_SUITE_STARTING"
    case ReporterOpts.PresentSuiteAborted => "REPORT_SUITE_ABORTED"
    case ReporterOpts.PresentSuiteCompleted => "REPORT_SUITE_COMPLETED"
    case ReporterOpts.PresentInfoProvided => "REPORT_INFO_PROVIDED"
    case ReporterOpts.PresentRunStopped => "REPORT_RUN_STOPPED"
    case ReporterOpts.PresentRunAborted => "REPORT_RUN_ABORTED"
    case ReporterOpts.PresentRunCompleted => "REPORT_RUN_COMPLETED"
  }
}
