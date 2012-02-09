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
package org.scalatest.concurrent

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.SharedHelpers.thisLineNumber
import org.scalatest.OptionValues
import org.scalatest.FunSpec
import java.util.concurrent.{Future => FutureOfJava}
import java.util.concurrent.TimeUnit
import org.scalatest._

class WhenReadySpec extends FunSpec with ShouldMatchers with OptionValues with WhenReady {

  describe("The whenReady construct") {

    class SuperFutureOfJava extends FutureOfJava[String] {
        def cancel(mayInterruptIfRunning: Boolean): Boolean = false
        def get: String = "hi"
        def get(timeout: Long, unit: TimeUnit): String = "hi"
        def isCancelled: Boolean = false
        def isDone: Boolean = true
      }
    // TODO: Make sure the right value is returned (the U). I think I may need an eventually test for this as well
    it("should just return if the function arg returns normally") {
      val futureIsNow = new SuperFutureOfJava
      whenReady(futureIsNow) { s =>
        s should equal ("hi")
      }
    }

    it("should query the future just once if the future is ready the first time") {
      var count = 0
      val countingFuture =
        new SuperFutureOfJava {
          override def isDone = {
            count += 1
            true
          }
      }
      whenReady(countingFuture) { s =>
        s should equal ("hi")
      }
      count should equal (1)
    }

    it("should query the future five times if the future is not ready four times before finally being ready the fifth time") {
      var count = 0
      val countingFuture =
        new SuperFutureOfJava {
          override def isDone = {
            count += 1
            count >= 5
          }
      }
      whenReady(countingFuture) { s =>
        s should equal ("hi")
      }
      count should equal (5)
    }
// TODO: tests for isDropped and isExpired
    it("should eventually blow up with a TFE if the future is never ready") {

      var count = 0
      val neverReadyFuture =
        new SuperFutureOfJava {
          override def isDone = {
            count += 1
            false
          }
        }
      val caught = evaluating {
        whenReady(neverReadyFuture) { s =>
          s should equal ("hi")
        }
      } should produce [TestFailedException]

      caught.message.value should be (Resources("wasNeverReady", count.toString, "10"))
      caught.failedCodeLineNumber.value should equal (thisLineNumber - 6)
      caught.failedCodeFileName.value should be ("WhenReadySpec.scala")
    }
    
// TODO: tests for the whole thing blowing up with the failure once a future is ready
    
    it("should provides correct stack depth") {
      pending /*
      val caught1 = evaluating {
        eventually(timeout(100), interval(1)) { 1 + 1 should equal (3) }
      } should produce [TestFailedException]
      caught1.failedCodeLineNumber.value should equal (thisLineNumber - 2)
      caught1.failedCodeFileName.value should be ("EventuallySpec.scala")
      
      val caught2 = evaluating {
        eventually(interval(1), timeout(100)) { 1 + 1 should equal (3) }
      } should produce [TestFailedException]
      caught2.failedCodeLineNumber.value should equal (thisLineNumber - 2)
      caught2.failedCodeFileName.value should be ("EventuallySpec.scala")
      
      val caught3 = evaluating {
        eventually(timeout(100)) { 1 + 1 should equal (3) }
      } should produce [TestFailedException]
      caught3.failedCodeLineNumber.value should equal (thisLineNumber - 2)
      caught3.failedCodeFileName.value should be ("EventuallySpec.scala")
      
      val caught4 = evaluating {
        eventually(interval(1)) { 1 + 1 should equal (3) }
      } should produce [TestFailedException]
      caught4.failedCodeLineNumber.value should equal (thisLineNumber - 2)
      caught4.failedCodeFileName.value should be ("EventuallySpec.scala") */
    }

    it("should by default query a never-ready future for at least 1 second") {
      pending /*
      var startTime: Option[Long] = None
      evaluating {
        eventually {
          if (startTime.isEmpty)
            startTime = Some(System.currentTimeMillis)
          1 + 1 should equal (3)
        }
      } should produce [TestFailedException]
      (System.currentTimeMillis - startTime.get).toInt should be >= (1000) */
    }

    it("should, if an alternate implicit Timeout is provided, query a never-ready by at least the specified timeout") {
      pending /*
      implicit val eventuallyConfig = EventuallyConfig(timeout = 1500)

      var startTime: Option[Long] = None
      evaluating {
        eventually {
          if (startTime.isEmpty)
            startTime = Some(System.currentTimeMillis)
          1 + 1 should equal (3)
        }
      } should produce [TestFailedException]
      (System.currentTimeMillis - startTime.get).toInt should be >= (1500) */
    }

    it("should, if an alternate explicit timeout is provided, query a never-ready future by at least the specified timeout") {
      pending /*
      var startTime: Option[Long] = None
      evaluating {
        eventually (timeout(1250)) {
          if (startTime.isEmpty)
            startTime = Some(System.currentTimeMillis)
          1 + 1 should equal (3)
        } 
      } should produce [TestFailedException]
      (System.currentTimeMillis - startTime.get).toInt should be >= (1250) */
    }

    it("should, if an alternate explicit timeout is provided along with an explicit interval, query a never-ready future by at least the specified timeout, even if a different implicit is provided") {
      pending /*
      implicit val eventuallyConfig = EventuallyConfig(timeout = 500, interval = 2)
      
      var startTime: Option[Long] = None
      evaluating {
        eventually (timeout(1388), interval(1)) {
          if (startTime.isEmpty)
            startTime = Some(System.currentTimeMillis)
          1 + 1 should equal (3)
        } 
      } should produce [TestFailedException]
      (System.currentTimeMillis - startTime.get).toInt should be >= (1388) */
    }
    
    it("should, if an alternate explicit timeout is provided along with an explicit interval, query a never-ready future by at least the specified timeout, even if a different implicit is provided, with timeout specified second") {
      pending /*
      implicit val eventuallyConfig = EventuallyConfig(interval = 2, timeout = 500)
      
      var startTime: Option[Long] = None
      evaluating {
        eventually (interval(1), timeout(1388)) {
          if (startTime.isEmpty)
            startTime = Some(System.currentTimeMillis)
          1 + 1 should equal (3)
        } 
      } should produce [TestFailedException]
      (System.currentTimeMillis - startTime.get).toInt should be >= (1388) */
    }
// TODO: tests that make sure a Throwable is thrown. I think that those should just go rather than
// being wrapped in a stack depth? Not sure. If wrapped, then this test is relevant:
    it("should allow errors that do not normally cause a test to fail to propagate back without being wrapped in a TFE") {
      pending /*
      var count = 0
      intercept[VirtualMachineError] {
        eventually {
          count += 1
          throw new VirtualMachineError {}
          1 + 1 should equal (3)
        }
      }
      count should equal (1) */
    }
    // Same thing here and in 2.0 need to add a test for TestCanceledException
    it("should allow TestPendingException, which does not normally cause a test to fail, through immediately when thrown") {
      pending /*
      var count = 0
      intercept[TestPendingException] {
        eventually {
          count += 1
          pending
        }
      }
      count should equal (1) */
    }
  }
}

