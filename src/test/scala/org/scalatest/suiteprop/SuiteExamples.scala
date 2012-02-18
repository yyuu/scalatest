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
package org.scalatest.suiteprop

import org.scalatest._
import prop.Tables

trait SuiteExamples extends Tables {

  type FixtureServices

  def suite: Suite with FixtureServices
  def fixtureSuite: fixture.Suite with FixtureServices
  def funSuite: FunSuite with FixtureServices
  def fixtureFunSuite: fixture.FunSuite with FixtureServices
  def funSpec: FunSpec with FixtureServices
  def fixtureFunSpec: fixture.FunSpec with FixtureServices
  def pathFunSpec: path.FunSpec with FixtureServices
  def wordSpec: WordSpec with FixtureServices

  def examples =
    Table(
      "suite",
      suite,
      fixtureSuite,
      funSuite,
      fixtureFunSuite,
      funSpec,
      fixtureFunSpec,
      pathFunSpec,
      wordSpec
    )
}
