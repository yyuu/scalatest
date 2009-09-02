/*
  * Copyright 2001-2008 Artima, Inc.
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

import org.scalatest._
import org.scalatest.fixture.{ConfigMapFixture, FixtureSuite}

/**
 * Trait that can pass a new <code>Conductor</code> fixture into tests, for use
 * in suites such as <code>MultipleFixtureFunSuite</code> or <code>MultipleFixtureSpec</code>,
 * which facilitate writing tests that take different types of fixtures.
 *
 * <p>
 * Here's an example of the use of this trait to test the <code>ArrayBlockingQueue</code>
 * concurrency abstraction from <code>java.util.concurrent</code>:
 * </p>
 *
 * <pre>
 * import org.scalatest.fixture.MultipleFixtureFunSuite
 * import org.scalatest.concurrent.ConductorMultiFixture
 * import org.scalatest.matchers.ShouldMatchers
 * import java.util.concurrent.ArrayBlockingQueue
 *
 * class ArrayBlockingQueueSuite extends MultipleFixtureFunSuite with ConductorFixture with ShouldMatchers {
 * 
 *   test("calling put on a full queue blocks the producer thread") { (conductor: Conductor) => import conductor._
 *
 *     @volatile val buf = new ArrayBlockingQueue[Int](1)
 * 
 *     thread("producer") {
 *       buf put 42
 *       buf put 17
 *       beat should be (1)
 *     }
 * 
 *     thread("consumer") {
 *       waitForBeat(1)
 *       buf.take should be (42)
 *       buf.take should be (17)
 *     }
 * 
 *     whenFinished {
 *       buf should be ('empty)
 *     }
 *   }
 * </pre>
 *
 * @author Bill Venners
 */

trait ConductorMultiFixture { this: FixtureSuite with ConfigMapFixture =>

  /**
   * Creates a new <code>Conductor</code>, passes the <code>Conductor</code> to the
   * specified test function, and ensures that <code>conductTest</code> gets invoked
   * on the <code>Conductor</code>.
   *
   * <p>
   * After the test function returns (so long as it returns normally and doesn't
   * complete abruptly with an exception), this method will determine whether the
   * <code>conductTest</code> method has already been called (by invoking
   * <code>conductTestWasCalled</code> on the <code>Conductor</code>). If not,
   * this method will invoke <code>conductTest</code> to ensure that the
   * multi-threaded test is actually conducted.
   * </p>
   *
   */
  implicit def withConductorFixture(fun: Conductor => Unit): this.Fixture => Unit = { configMap =>
    val conductor = new Conductor
    fun(conductor)
    if (!conductor.conductTestWasCalled)
      conductor.conductTest()
  }
}