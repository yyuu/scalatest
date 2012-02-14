/*
 * Copyright 2001-2012 Artima, Inc.
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

import org.scalatest.AbstractSuite
import org.scalatest.Suite
import Timeouts._
import org.scalatest.ModifiableMessage
import org.scalatest.Resources

/**
 * Trait mixed into exceptions thrown by <code>failAfter</code> due to a timeout.
 *
 * <p>
 * This trait overrides <code>withFixture</code>, wrapping a <code>super.withFixture(test)</code> call
 * in a <code>failAfter</code> invocation, specifying a timeout obtained by invoking <code>timeLimit</code>
 * on this instance:
 * </p>
 * 
 * <pre>
 * failAfter(timeLimit) {
 *   super.withFixture(test)
 * }
 * </pre>
 *
 * <p>
 * The <code>timeLimit</code> field is abstract in this trait. Thus you must specify a time limit when you use it.
 * For example, the following code specifies that each test must complete within 200 milliseconds:
 * </p>
 * 
 * <pre>
 * import org.scalatest.FunSpec
 * import org.scalatest.concurrent.TimeLimitedTests
 * 
 * class ExampleSpec extends FunSpec with TimeLimitedTests {
 *
 *   val timeLimit = 200L
 *
 *   describe("A time-limited test") {
 *     it("should succeed if it completes within the time limit") {
 *       Thread.sleep(100)
 *     }
 *     it("should fail if it is taking too darn long") {
 *       Thread.sleep(300)
 *     }
 *   }
 * }
 * </pre>
 *
 * <p>
 * If you prefer, you can mix in or import the members of <code>TimeSugar</code> and place units on the time limit, for example:
 * </p>
 *
 * <pre>
 * import org.scalatest.TimeSugar._
 *
 * val timeLimit = 200 millis
 * </pre>
 *
 * <code>The test did not complete within the specified 100 millisecond time limit.</code>
 */
trait TimeLimitedTests extends AbstractSuite { this: Suite =>

  /**
   *
   */
  abstract override def withFixture(test: NoArgTest) {
    try {
      failAfter(timeLimit) {
        super.withFixture(test)
      }
    }
    catch {
      case e: ModifiableMessage[_] with TimeoutException =>
        throw e.modifyMessage(opts => Some(Resources("testTimeLimitExceeded", e.timeout.toString)))
    }
  }

  /**
   * The time limit, in milliseconds, in which each test in a <code>Suite</code> that mixes in
   * <code>TimeLimitedTests</code> must complete.
   */
  def timeLimit: Long
}

/*
Will need to add cancelAfter to the doc comment in 2.0.
*/

