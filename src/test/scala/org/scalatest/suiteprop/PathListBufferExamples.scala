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
import matchers.ShouldMatchers
import prop.TableDrivenPropertyChecks
import scala.collection.mutable.ListBuffer

class PathListBufferExamples extends PathSuiteExamples {

  type FixtureServices = AnyRef

  class EmptyPathFunSpecExample extends path.FunSpec with ShouldMatchers {

    describe("A ListBuffer") {
      val buf = ListBuffer.empty[Int]
      it("should be empty when created") {
        buf should be ('empty)
        buf += 99 // Mutate to make sure no other test sees this
      }
      describe("when 1 is appended") {
        buf += 1
        it("should contain 1") {
          buf should equal (Seq(1))
          buf += 99 // Mutate to make sure no other test sees this
        }
        describe("when 2 is appended") {
          buf += 2
          it("should contain 1 and 2") {
            buf should equal (Seq(1, 2))
            buf += 99 // Mutate to make sure no other test sees this
          }
          describe("when 2 is removed") {
            buf -= 2
            it("should contain only 1 again") {
              buf should equal (Seq(1))
              buf += 99 // Mutate to make sure no other test sees this
            }
          }
          describe("when 3 is appended") { // This describe should not see the removal of 2 done in earlier sibling describe
            buf += 3
            it("should contain 1, 2, and 3") {
              buf should equal (Seq(1, 2, 3))
              buf += 99 // Mutate to make sure no other test sees this
            }
          }
        }
        describe("when 88 is appended") {
          buf += 88
          it("should contain 1 and 88") {
            buf should equal (Seq(1, 88))
            buf += 99 // Mutate to make sure no other test sees this
          }
        }
      }
      // At end of previous describe, buf equaled List(1). Now doing it again to make
      // sure that it is empty
      it("should again be empty") {
        buf should be ('empty)
      }
    }

    override def newInstance = new EmptyPathFunSpecExample
  }

  class EmptyNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new EmptyNestedPathFunSpecExample
  }

  class SiblingEmptyNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new SiblingEmptyNestedPathFunSpecExample
  }

  class OneTestSiblingEmptyNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new OneTestSiblingEmptyNestedPathFunSpecExample
  }
  
  class OneTestSiblingEmptyDeeplyNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new OneTestSiblingEmptyDeeplyNestedPathFunSpecExample
  }

  class PathFunSpecExample extends path.FunSpec {
    override def newInstance = new PathFunSpecExample
  }

  class NestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new NestedPathFunSpecExample
  }

  class SiblingNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new SiblingNestedPathFunSpecExample
  }

  class DeeplyNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new DeeplyNestedPathFunSpecExample
  }

  class SiblingDeeplyNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new SiblingDeeplyNestedPathFunSpecExample
  }

  class AsymetricalDeeplyNestedPathFunSpecExample extends path.FunSpec {
    override def newInstance = new AsymetricalDeeplyNestedPathFunSpecExample
  }

  class EmptyPathFreeSpecExample extends path.FreeSpec with ShouldMatchers {
    "A ListBuffer" - {
      val buf = ListBuffer.empty[Int]
      "should be empty when created" in {
        buf should be ('empty)
        buf += 99 // Mutate to make sure no other test sees this
      }
      "when 1 is appended" - {
        buf += 1
        "should contain 1" in {
          buf should equal (Seq(1))
          buf += 99 // Mutate to make sure no other test sees this
        }
        "when 2 is appended" - {
          buf += 2
          "should contain 1 and 2" in {
            buf should equal (Seq(1, 2))
            buf += 99 // Mutate to make sure no other test sees this
          }
          "when 2 is removed" - {
            buf -= 2
            "should contain only 1 again" in {
              buf should equal (Seq(1))
              buf += 99 // Mutate to make sure no other test sees this
            }
          }
          "when 3 is appended" - { // This describe should not see the removal of 2 done in earlier sibling describe
            buf += 3
            "should contain 1, 2, and 3" in {
              buf should equal (Seq(1, 2, 3))
              buf += 99 // Mutate to make sure no other test sees this
            }
          }
        }
        "when 88 is appended" - {
          buf += 88
          "should contain 1 and 88" in {
            buf should equal (Seq(1, 88))
            buf += 99 // Mutate to make sure no other test sees this
          }
        }
      }
      // At end of previous describe, buf equaled List(1). Now doing it again to make
      // sure that it is empty
      "should again be empty" in {
        buf should be ('empty)
      }
    }

    override def newInstance = new EmptyPathFreeSpecExample
  }

  class EmptyNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new EmptyNestedPathFreeSpecExample
  }

  class SiblingEmptyNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new SiblingEmptyNestedPathFreeSpecExample
  }

  class OneTestSiblingEmptyNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new OneTestSiblingEmptyNestedPathFreeSpecExample
  }
  
  class OneTestSiblingEmptyDeeplyNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new OneTestSiblingEmptyDeeplyNestedPathFreeSpecExample
  }

  class PathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new PathFreeSpecExample
  }

  class NestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new NestedPathFreeSpecExample
  }

  class SiblingNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new SiblingNestedPathFreeSpecExample
  }

  class DeeplyNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new DeeplyNestedPathFreeSpecExample
  }

  class SiblingDeeplyNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new SiblingDeeplyNestedPathFreeSpecExample
  }

  class AsymetricalDeeplyNestedPathFreeSpecExample extends path.FreeSpec {
    override def newInstance = new AsymetricalDeeplyNestedPathFreeSpecExample
  }

  def emptyPathFunSpec = new EmptyPathFunSpecExample
  def emptyNestedPathFunSpec = new EmptyNestedPathFunSpecExample
  def siblingEmptyNestedPathFunSpec = new SiblingEmptyNestedPathFunSpecExample
  def oneTestSiblingEmptyNestedPathFunSpec = new OneTestSiblingEmptyNestedPathFunSpecExample
  def oneTestSiblingEmptyDeeplyNestedPathFunSpec = new OneTestSiblingEmptyDeeplyNestedPathFunSpecExample
  def pathFunSpec = new PathFunSpecExample
  def nestedPathFunSpec = new NestedPathFunSpecExample
  def siblingNestedPathFunSpec = new SiblingNestedPathFunSpecExample
  def deeplyNestedPathFunSpec = new DeeplyNestedPathFunSpecExample
  def siblingDeeplyNestedPathFunSpec = new SiblingDeeplyNestedPathFunSpecExample
  def asymetricalDeeplyNestedPathFunSpec = new AsymetricalDeeplyNestedPathFunSpecExample
  def emptyPathFreeSpec = new EmptyPathFreeSpecExample
  def emptyNestedPathFreeSpec = new EmptyNestedPathFreeSpecExample
  def siblingEmptyNestedPathFreeSpec = new SiblingEmptyNestedPathFreeSpecExample
  def oneTestSiblingEmptyNestedPathFreeSpec = new OneTestSiblingEmptyNestedPathFreeSpecExample
  def oneTestSiblingEmptyDeeplyNestedPathFreeSpec = new OneTestSiblingEmptyDeeplyNestedPathFreeSpecExample
  def pathFreeSpec = new PathFreeSpecExample
  def nestedPathFreeSpec = new NestedPathFreeSpecExample
  def siblingNestedPathFreeSpec = new SiblingNestedPathFreeSpecExample
  def deeplyNestedPathFreeSpec = new DeeplyNestedPathFreeSpecExample
  def siblingDeeplyNestedPathFreeSpec = new SiblingDeeplyNestedPathFreeSpecExample
  def asymetricalDeeplyNestedPathFreeSpec = new AsymetricalDeeplyNestedPathFreeSpecExample
}

