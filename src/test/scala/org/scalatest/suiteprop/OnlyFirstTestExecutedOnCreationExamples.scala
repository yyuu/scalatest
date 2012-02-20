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

class OnlyFirstTestExecutedOnCreationExamples extends PathSuiteExamples {

  case class Counts(
    var firstTestCount: Int,
    var secondTestCount: Int,
    var instanceCount: Int
  )
  
  trait Services {
    val expectedInstanceCount = 2
    val expectedTotalTestsCount = 2
    val expectFirstTestToRunInInitialInstance = true
    val counts: Counts
  }

  type FixtureServices = Services

  class EmptyPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    override def newInstance = new EmptyPathFunSpecExample(counts)
    override val expectedInstanceCount = 1
    override val expectedTotalTestsCount = 0
  }

  class EmptyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
    }
    override def newInstance = new EmptyNestedPathFunSpecExample(counts)
    override val expectedInstanceCount = 1
    override val expectedTotalTestsCount = 0
  }

  class SiblingEmptyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
    }
    describe("Another subject") {
    }
    override def newInstance = new SiblingEmptyNestedPathFunSpecExample(counts)
    override val expectedTotalTestsCount = 0
  }

  class OneTestSiblingEmptyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
    }
    describe("Another subject") {
      it("first test") { firstTestCount += 1 }
    }
    override def newInstance = new OneTestSiblingEmptyNestedPathFunSpecExample(counts)
    override val expectedTotalTestsCount = 1
    override val expectFirstTestToRunInInitialInstance = false
  }
  
  class PathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    it("first test") { firstTestCount += 1 }
    it("second test") { secondTestCount += 1 }
    override def newInstance = new PathFunSpecExample(counts)
  }

  class NestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
      it("should first test") { firstTestCount += 1 }
      it("should second test") { secondTestCount += 1 }
    }
    override def newInstance = new NestedPathFunSpecExample(counts)
  }

  class SiblingNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
      it("should first test") { firstTestCount += 1 }
    }
    describe("Another subject") {
      it("should second test") { secondTestCount += 1 }
    }
    override def newInstance = new SiblingNestedPathFunSpecExample(counts)
  }

  class DeeplyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
      describe("when created") {
        it("should first test") { firstTestCount += 1 }
        it("should second test") { secondTestCount += 1 }
      }
    }
    override def newInstance = new DeeplyNestedPathFunSpecExample(counts)
  }

  class SiblingDeeplyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
      describe("when created") {
        it("should first test") { firstTestCount += 1 }
      }
    }
    describe("Another subject") {
      describe("when created") {
        it("should second test") { secondTestCount += 1 }
      }
    }
    override def newInstance = new SiblingDeeplyNestedPathFunSpecExample(counts)
  }

  class AsymetricalDeeplyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services {
    import counts._
    instanceCount += 1
    describe("A subject") {
      describe("when created") {
        it("should first test") { firstTestCount += 1 }
      }
      it("should second test") { secondTestCount += 1 }
    }
    override def newInstance = new AsymetricalDeeplyNestedPathFunSpecExample(counts)
  }

  def emptyPathFunSpec = new EmptyPathFunSpecExample(Counts(0, 0, 0))
  def emptyNestedPathFunSpec = new EmptyNestedPathFunSpecExample(Counts(0, 0, 0))
  def siblingEmptyNestedPathFunSpec = new SiblingEmptyNestedPathFunSpecExample(Counts(0, 0, 0))
  def oneTestSiblingEmptyNestedPathFunSpec = new OneTestSiblingEmptyNestedPathFunSpecExample(Counts(0, 0, 0))
  def pathFunSpec = new PathFunSpecExample(Counts(0, 0, 0))
  def nestedPathFunSpec = new NestedPathFunSpecExample(Counts(0, 0, 0))
  def siblingNestedPathFunSpec = new SiblingNestedPathFunSpecExample(Counts(0, 0, 0))
  def deeplyNestedPathFunSpec = new DeeplyNestedPathFunSpecExample(Counts(0, 0, 0))
  def siblingDeeplyNestedPathFunSpec = new SiblingDeeplyNestedPathFunSpecExample(Counts(0, 0, 0))
  def asymetricalDeeplyNestedPathFunSpec = new AsymetricalDeeplyNestedPathFunSpecExample(Counts(0, 0, 0))
}

