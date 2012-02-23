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
  def nestedFunSpec: FunSpec with FixtureServices
  def deeplyNestedFunSpec: FunSpec with FixtureServices
  def fixtureFunSpec: fixture.FunSpec with FixtureServices
  def nestedFixtureFunSpec: fixture.FunSpec with FixtureServices
  def deeplyNestedFixtureFunSpec: fixture.FunSpec with FixtureServices
  def pathFunSpec: path.FunSpec with FixtureServices
  def nestedPathFunSpec: path.FunSpec with FixtureServices
  def deeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  def wordSpec: WordSpec with FixtureServices
  def nestedWordSpec: WordSpec with FixtureServices
  def deeplyNestedWordSpec: WordSpec with FixtureServices
  def fixtureWordSpec: fixture.WordSpec with FixtureServices
  def nestedFixtureWordSpec: fixture.WordSpec with FixtureServices
  def deeplyNestedFixtureWordSpec: fixture.WordSpec with FixtureServices
  def flatSpec: FlatSpec with FixtureServices
  def subjectFlatSpec: FlatSpec with FixtureServices
  def shorthandSubjectFlatSpec: FlatSpec with FixtureServices
  def fixtureFlatSpec: fixture.FlatSpec with FixtureServices
  def subjectFixtureFlatSpec: fixture.FlatSpec with FixtureServices
  def shorthandSubjectFixtureFlatSpec: fixture.FlatSpec with FixtureServices
  def freeSpec: FreeSpec with FixtureServices
  def nestedFreeSpec: FreeSpec with FixtureServices
  def deeplyNestedFreeSpec: FreeSpec with FixtureServices
  def fixtureFreeSpec: fixture.FreeSpec with FixtureServices
  def nestedFixtureFreeSpec: fixture.FreeSpec with FixtureServices
  def deeplyNestedFixtureFreeSpec: fixture.FreeSpec with FixtureServices
  def pathFreeSpec: path.FreeSpec with FixtureServices
  def nestedPathFreeSpec: path.FreeSpec with FixtureServices
  def deeplyNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def featureSpec: FeatureSpec with FixtureServices
  def nestedFeatureSpec: FeatureSpec with FixtureServices
  def fixtureFeatureSpec: fixture.FeatureSpec with FixtureServices
  def nestedFixtureFeatureSpec: fixture.FeatureSpec with FixtureServices
  def propSpec: PropSpec with FixtureServices
  def fixturePropSpec: fixture.PropSpec with FixtureServices

  def examples =
    Table(
      "suite",
      suite,
      fixtureSuite,
      funSuite,
      fixtureFunSuite,
      funSpec,
      nestedFunSpec,
      deeplyNestedFunSpec,
      fixtureFunSpec,
      nestedFixtureFunSpec,
      deeplyNestedFixtureFunSpec,
      pathFunSpec,
      nestedPathFunSpec,
      deeplyNestedPathFunSpec,
      wordSpec,
      nestedWordSpec,
      deeplyNestedWordSpec,
      fixtureWordSpec,
      nestedFixtureWordSpec,
      deeplyNestedFixtureWordSpec,
      flatSpec,
      subjectFlatSpec,
      shorthandSubjectFlatSpec,
      fixtureFlatSpec,
      subjectFixtureFlatSpec,
      shorthandSubjectFixtureFlatSpec,
      freeSpec,
      nestedFreeSpec,
      deeplyNestedFreeSpec,
      fixtureFreeSpec,
      nestedFixtureFreeSpec,
      deeplyNestedFixtureFreeSpec,
      pathFreeSpec,
      nestedPathFreeSpec,
      deeplyNestedPathFreeSpec,
      featureSpec,
      nestedFeatureSpec,
      fixtureFeatureSpec,
      nestedFixtureFeatureSpec,
      propSpec,
      fixturePropSpec
    )
}