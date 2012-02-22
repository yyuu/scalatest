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

  case class Counts(
    var instanceCount: Int
  )
  
  trait Services {
    val expectedInstanceCount: Int
    val counts: Counts
  }

  type FixtureServices = Services

  class EmptyPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

   counts.instanceCount += 1
   val expectedInstanceCount = 7
   
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

    override def newInstance = new EmptyPathFunSpecExample(counts)
  }

  class EmptyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 8
   
    describe("A subject") {
    }

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

    override def newInstance = new EmptyNestedPathFunSpecExample(counts)
  }

  class SiblingEmptyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

   counts.instanceCount += 1
   val expectedInstanceCount = 9
   
   describe("A subject") {
   }
   describe("Another subject") {
   }

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

    override def newInstance = new SiblingEmptyNestedPathFunSpecExample(counts)
  }

  class OneTestSiblingEmptyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

   counts.instanceCount += 1
   val expectedInstanceCount = 9
   
   describe("A subject") {
   }
   describe("Another subject") {
   }

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

    override def newInstance = new OneTestSiblingEmptyNestedPathFunSpecExample(counts)
  }
  
  class OneTestSiblingEmptyDeeplyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new OneTestSiblingEmptyDeeplyNestedPathFunSpecExample(counts)
  }

  class PathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 7
   
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

    override def newInstance = new PathFunSpecExample(counts)
  }

  class NestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new NestedPathFunSpecExample(counts)
  }

  class SiblingNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new SiblingNestedPathFunSpecExample(counts)
  }

  class DeeplyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new DeeplyNestedPathFunSpecExample(counts)
  }

  class SiblingDeeplyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new SiblingDeeplyNestedPathFunSpecExample(counts)
  }

  class AsymetricalDeeplyNestedPathFunSpecExample(val counts: Counts) extends path.FunSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new AsymetricalDeeplyNestedPathFunSpecExample(counts)
  }

  class EmptyPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 7
   
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

    override def newInstance = new EmptyPathFreeSpecExample(counts)
  }

  class EmptyNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 8
   
    "A subject" - {
    }

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

    override def newInstance = new EmptyNestedPathFreeSpecExample(counts)
  }

  class SiblingEmptyNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 9
   
    "A subject" - {
    }
    "Another subject" - {
    }

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

    override def newInstance = new SiblingEmptyNestedPathFreeSpecExample(counts)
  }

  class OneTestSiblingEmptyNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new OneTestSiblingEmptyNestedPathFreeSpecExample(counts)
  }
  
  class OneTestSiblingEmptyDeeplyNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new OneTestSiblingEmptyDeeplyNestedPathFreeSpecExample(counts)
  }

  class PathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 7
   
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

    override def newInstance = new PathFreeSpecExample(counts)
  }

  class NestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new NestedPathFreeSpecExample(counts)
  }

  class SiblingNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new SiblingNestedPathFreeSpecExample(counts)
  }

  class DeeplyNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new DeeplyNestedPathFreeSpecExample(counts)
  }

  class SiblingDeeplyNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new SiblingDeeplyNestedPathFreeSpecExample(counts)
  }

  class AsymetricalDeeplyNestedPathFreeSpecExample(val counts: Counts) extends path.FreeSpec with Services with ShouldMatchers {

    counts.instanceCount += 1
    val expectedInstanceCount = 1
   
    override def newInstance = new AsymetricalDeeplyNestedPathFreeSpecExample(counts)
  }

  def emptyPathFunSpec = new EmptyPathFunSpecExample(Counts(0))
  def emptyNestedPathFunSpec = new EmptyNestedPathFunSpecExample(Counts(0))
  def siblingEmptyNestedPathFunSpec = new SiblingEmptyNestedPathFunSpecExample(Counts(0))
  def oneTestSiblingEmptyNestedPathFunSpec = new OneTestSiblingEmptyNestedPathFunSpecExample(Counts(0))
  def oneTestSiblingEmptyDeeplyNestedPathFunSpec = new OneTestSiblingEmptyDeeplyNestedPathFunSpecExample(Counts(0))
  def pathFunSpec = new PathFunSpecExample(Counts(0))
  def nestedPathFunSpec = new NestedPathFunSpecExample(Counts(0))
  def siblingNestedPathFunSpec = new SiblingNestedPathFunSpecExample(Counts(0))
  def deeplyNestedPathFunSpec = new DeeplyNestedPathFunSpecExample(Counts(0))
  def siblingDeeplyNestedPathFunSpec = new SiblingDeeplyNestedPathFunSpecExample(Counts(0))
  def asymetricalDeeplyNestedPathFunSpec = new AsymetricalDeeplyNestedPathFunSpecExample(Counts(0))
  def emptyPathFreeSpec = new EmptyPathFreeSpecExample(Counts(0))
  def emptyNestedPathFreeSpec = new EmptyNestedPathFreeSpecExample(Counts(0))
  def siblingEmptyNestedPathFreeSpec = new SiblingEmptyNestedPathFreeSpecExample(Counts(0))
  def oneTestSiblingEmptyNestedPathFreeSpec = new OneTestSiblingEmptyNestedPathFreeSpecExample(Counts(0))
  def oneTestSiblingEmptyDeeplyNestedPathFreeSpec = new OneTestSiblingEmptyDeeplyNestedPathFreeSpecExample(Counts(0))
  def pathFreeSpec = new PathFreeSpecExample(Counts(0))
  def nestedPathFreeSpec = new NestedPathFreeSpecExample(Counts(0))
  def siblingNestedPathFreeSpec = new SiblingNestedPathFreeSpecExample(Counts(0))
  def deeplyNestedPathFreeSpec = new DeeplyNestedPathFreeSpecExample(Counts(0))
  def siblingDeeplyNestedPathFreeSpec = new SiblingDeeplyNestedPathFreeSpecExample(Counts(0))
  def asymetricalDeeplyNestedPathFreeSpec = new AsymetricalDeeplyNestedPathFreeSpecExample(Counts(0))
}

