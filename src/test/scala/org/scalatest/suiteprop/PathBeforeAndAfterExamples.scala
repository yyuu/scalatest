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
import prop.TableDrivenPropertyChecks

class PathBeforeAndAfterExamples extends PathSuiteExamples {

  case class Counts(
    var before0: Int = 0,
    var before00: Int = 0,
    var before000: Int = 0,
    var middle: Int = 0,
    var after000: Int = 0,
    var after00: Int = 0,
    var after0: Int = 0
  )
  
  trait Services {
    val counts: Counts
    var firstTestCounts: Counts = Counts()
    var secondTestCounts: Counts = Counts()
    val expectedFirstTestCounts: Counts
    val expectedSecondTestCounts: Counts
    val expectedCounts: Counts
  }

  type FixtureServices = Services

  class PathFunSpecExample(val counts: Counts, initialInstance: Option[Services] = None) extends path.FunSpec with Services {
    import counts._
    before0 += 1
    it("first test") { firstTestCounts = counts.copy() }
    middle += 1
    it("second test") { initialInstance.get.secondTestCounts = counts.copy() }
    after0 += 1
    override def newInstance = new PathFunSpecExample(counts, Some(this))
    val expectedFirstTestCounts = Counts(before0 = 1)
    val expectedSecondTestCounts = Counts(before0 = 2, middle = 2, after0 = 1)
    val expectedCounts = Counts(before0 = 2, middle = 2, after0 = 2)
  }

  class NestedPathFunSpecExample(val counts: Counts, initialInstance: Option[Services] = None) extends path.FunSpec with Services {
    import counts._
    before0 += 1
    describe("A subject") {
      before00 += 1
      it("should first test") { firstTestCounts = counts.copy() }
      middle += 1
      it("should second test") { initialInstance.get.secondTestCounts = counts.copy() }
      after00 += 1
    }
    after0 += 1
    override def newInstance = new NestedPathFunSpecExample(counts, Some(this))
    val expectedFirstTestCounts = Counts(before0 = 1, before00 = 1)
    val expectedSecondTestCounts = Counts(before0 = 2, before00 = 2, middle = 2, after00 = 1, after0 = 1)
    val expectedCounts = Counts(before0 = 2, before00 = 2, middle = 2, after00 = 2, after0 = 2)
  }

  class DeeplyNestedPathFunSpecExample(val counts: Counts, initialInstance: Option[Services] = None) extends path.FunSpec with Services {
    import counts._
    before0 += 1
    describe("A subject") {
      before00 += 1
      describe("when created") {
        before000 += 1
        it("should first test") { firstTestCounts = counts.copy() }
        middle += 1
        it("should second test") { initialInstance.get.secondTestCounts = counts.copy() }
        after000 += 1
      }
      after00 += 1
    }
    after0 += 1
    override def newInstance = new DeeplyNestedPathFunSpecExample(counts, Some(this))
    val expectedFirstTestCounts = Counts(before0 = 1, before00 = 1, before000 = 1)
    val expectedSecondTestCounts = Counts(before0 = 2, before00 = 2, before000 = 2, middle = 2, after000 = 1, after00 = 1, after0 = 1)
    val expectedCounts = Counts(before0 = 2, before00 = 2, before000 = 2, middle = 2, after000 = 2, after00 = 2, after0 = 2)
  }

  def pathFunSpec = new PathFunSpecExample(Counts())
  def nestedPathFunSpec = new NestedPathFunSpecExample(Counts())
  def deeplyNestedPathFunSpec = new DeeplyNestedPathFunSpecExample(Counts())
}

