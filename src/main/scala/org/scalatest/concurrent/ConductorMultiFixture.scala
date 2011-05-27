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
import fixture.{ConfigMapFixture, FixtureSuite}

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
 * <pre class="stHighlight">
 * import org.scalatest.fixture.MultipleFixtureFunSuite
 * import org.scalatest.concurrent.ConductorMultiFixture
 * import org.scalatest.matchers.ShouldMatchers
 * import java.util.concurrent.ArrayBlockingQueue
 *
 * class ArrayBlockingQueueSuite extends MultipleFixtureFunSuite with ConductorMultiFixture with ShouldMatchers {
 * 
 *   test("calling put on a full queue blocks the producer thread") { (conductor: Conductor) => import conductor._
 *
 *     val buf = new ArrayBlockingQueue[Int](1)
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
 *
 *   test("calling take on an empty queue blocks the consumer thread") { (conductor: Conductor) => import conductor._
 *
 *     val buf = new ArrayBlockingQueue[Int](1)
 *
 *     thread("producer") {
 *       waitForBeat(1)
 *       buf put 42
 *       buf put 17
 *     }
 *
 *     thread("consumer") {
 *       buf.take should be (42)
 *       buf.take should be (17)
 *       beat should be (1)
 *     }
 *
 *     whenFinished {
 *       buf should be ('empty)
 *     }
 *   }
 * }
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">import</span> org.scalatest.fixture.MultipleFixtureFunSuite
 * <span class="stReserved">import</span> org.scalatest.concurrent.ConductorMultiFixture
 * <span class="stReserved">import</span> org.scalatest.matchers.ShouldMatchers
 * <span class="stReserved">import</span> java.util.concurrent.ArrayBlockingQueue
 * <br /><span class="stReserved">class</span> <span class="stType">ArrayBlockingQueueSuite</span> <span class="stReserved">extends</span> <span class="stType">MultipleFixtureFunSuite</span> <span class="stReserved">with</span> <span class="stType">ConductorMultiFixture</span> <span class="stReserved">with</span> <span class="stType">ShouldMatchers</span> {
 * <br />  test(<span class="stQuotedString">"calling put on a full queue blocks the producer thread"</span>) { (conductor: <span class="stType">Conductor</span>) => <span class="stReserved">import</span> conductor._
 * <br />    <span class="stReserved">val</span> buf = <span class="stReserved">new</span> <span class="stType">ArrayBlockingQueue[Int]</span>(<span class="stLiteral">1</span>)
 * <br />    thread(<span class="stQuotedString">"producer"</span>) {
 *       buf put <span class="stLiteral">42</span>
 *       buf put <span class="stLiteral">17</span>
 *       beat should be (<span class="stLiteral">1</span>)
 *     }
 * <br />    thread(<span class="stQuotedString">"consumer"</span>) {
 *       waitForBeat(<span class="stLiteral">1</span>)
 *       buf.take should be (<span class="stLiteral">42</span>)
 *       buf.take should be (<span class="stLiteral">17</span>)
 *     }
 * <br />    whenFinished {
 *       buf should be (<span class="stQuotedString">'empty</span>)
 *     }
 *   }
 * <br />  test(<span class="stQuotedString">"calling take on an empty queue blocks the consumer thread"</span>) { (conductor: <span class="stType">Conductor</span>) => <span class="stReserved">import</span> conductor._
 * <br />    <span class="stReserved">val</span> buf = <span class="stReserved">new</span> <span class="stType">ArrayBlockingQueue[Int]</span>(<span class="stLiteral">1</span>)
 * <br />    thread(<span class="stQuotedString">"producer"</span>) {
 *       waitForBeat(<span class="stLiteral">1</span>)
 *       buf put <span class="stLiteral">42</span>
 *       buf put <span class="stLiteral">17</span>
 *     }
 * <br />    thread(<span class="stQuotedString">"consumer"</span>) {
 *       buf.take should be (<span class="stLiteral">42</span>)
 *       buf.take should be (<span class="stLiteral">17</span>)
 *       beat should be (<span class="stLiteral">1</span>)
 *     }
 * <br />    whenFinished {
 *       buf should be (<span class="stQuotedString">'empty</span>)
 *     }
 *   }
 * }
 * </pre>
 *
 * <p>
 * For an explanation of how these tests work, see the documentation for <a href="Conductor.html"><code>Conductor</code></a>.
 * </p>
 *
 * @author Bill Venners
 */

trait ConductorMultiFixture { this: FixtureSuite with ConfigMapFixture =>

  /**
   * Creates a new <code>Conductor</code>, passes the <code>Conductor</code> to the
   * specified test function, and ensures that <code>conduct</code> gets invoked
   * on the <code>Conductor</code>.
   *
   * <p>
   * After the test function returns (so long as it returns normally and doesn't
   * complete abruptly with an exception), this method will determine whether the
   * <code>conduct</code> method has already been called (by invoking
   * <code>conductingHasBegun</code> on the <code>Conductor</code>). If not,
   * this method will invoke <code>conduct</code> to ensure that the
   * multi-threaded test is actually conducted.
   * </p>
   *
   */
  implicit def withConductorFixture(fun: Conductor => Unit): this.FixtureParam => Unit = { configMap =>
    val conductor = new Conductor
    fun(conductor)
    if (!conductor.conductingHasBegun)
      conductor.conduct()
  }
}
