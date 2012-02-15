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
 * and an <code>Interruptor</code> by invoking <code>defaultTestInterruptor</code>:
 * </p>
 * 
 * <pre>
 * failAfter(timeLimit) {
 *   super.withFixture(test)
 * } (defaultTestInterruptor)
 * </pre>
 *
 * <p>
 * Note that the <code>failAfter</code> method executes the body of the by-name passed to it using the same
 * thread that invoked <code>failAfter</code>. This means that the same thread will run the <code>withFixture</code> method
 * as well as each test, so no extra synchronization is required. A second thread is used to run a timer, and if the timeout
 * expires, that second thread will attempt to interrupt the main test thread via the <code>defaultTestInterruptor</code>.
 * </p>
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
 * If you run the above <code>ExampleSpec</code>, the second test will fail with the error message: 
 * </p>
 * 
 * <p>
 * <code>The test did not complete within the specified 200 millisecond time limit.</code>
 * </p>
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
 * <p>
 * The <code>failAfter</code> method uses an <code>Interruptor</code> to attempt to interrupt the main test thread if the timeout
 * expires. The default <code>Interruptor</code> returned by the <code>defaultTestInterruptor</code> method is a
 * <code>ThreadInterruptor</code>, which calls <code>interrupt</code> on the main test thread. If you wish to change this
 * interruption strategy, override <code>defaultTestInterruptor</code> to return a different <code>Interruptor</code>. For example,
 * here's how you'd change the default to <code>DoNotInterrupt</code>, a very patient interruption strategy that does nothing to
 * interrupt the main test thread.
 * </p>
 */
trait TimeLimitedTests extends AbstractSuite { this: Suite =>

  /**
   *
   */
  abstract override def withFixture(test: NoArgTest) {
    try {
      failAfter(timeLimit) {
        super.withFixture(test)
      } (defaultTestInterruptor)
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
  
  val defaultTestInterruptor: Interruptor = ThreadInterruptor
}

/*
Will need to add cancelAfter to the doc comment in 2.0.
*/

