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
package org.scalatest

/**
 * ScalaTest's main traits, classes, and other members, including members supporting ScalaTest's DSL for the Scala interpreter.
 */
object run {

  private val defaultShell = ShellImpl()

  def main(args: Array[String]) {
    tools.Runner.main(Array("-p", ".", "-o") ++ args.flatMap(s => Array("-s", s)))
  }

  /**
   * Run the passed suite, optionally passing in a test name and config map. 
   *
   * <p>
   * This method will invoke <code>execute</code> on the passed <code>suite</code>, passing in
   * the specified (or default) <code>testName</code> and <code>configMap</code> and the configuration values
   * passed to this <code>Shell</code>'s constructor (<code>colorPassed</code>, <code>durationsPassed</code>, <code>shortStacksPassed</code>,
   * <code>fullStacksPassed</code>, and <code>statsPassed</code>).
   * </p>
   */
  def apply(suite: Suite, testName: String = null, configMap: Map[String, Any] = Map()) {
    defaultShell.run(suite, testName, configMap)
  }
}
