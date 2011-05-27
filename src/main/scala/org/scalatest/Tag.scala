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
package org.scalatest

/**
 * Class whose subclasses can be used to tag tests in types <code>FunSuite</code>,
 * <code>Spec</code>, <code>FlatSpec</code>, <code>WordSpec</code>, <code>FeatureSpec</code>, and their
 * sister traits in the <code>org.scalatest.fixture</code> package. For example, if you define:
 *
 * <pre class="stHighlight">
 * object SlowTest extends Tag("SlowTest")
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">object</span> <span class="stType">SlowTest</span> <span class="stReserved">extends</span> <span class="stType">Tag</span>(<span class="stQuotedString">"SlowTest"</span>)
 * </pre>
 *
 * then you can tag a test as a <code>SlowTest</code> in a <code>FunSuite</code> or <code>FixtureFunSuite</code> like this:
 * <pre class="stHighlight">
 * import org.scalatest.FunSuite
 *
 * class MySuite extends FunSuite {
 *
 *   test("my test", SlowTest) {
 *     Thread.sleep(1000)
 *   }
 * }
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">import</span> org.scalatest.FunSuite
 * <br /><span class="stReserved">class</span> <span class="stType">MySuite</span> <span class="stReserved">extends</span> <span class="stType">FunSuite</span> {
 * <br />  test(<span class="stQuotedString">"my test"</span>, <span class="stType">SlowTest</span>) {
 *     Thread.sleep(<span class="stLiteral">1000</span>)
 *   }
 * }
 * </pre>
 *
 * <p>
 * or in a <code>Spec</code> or <code>FixtureSpec</code> like this:
 * </p>
 *
 * <pre class="stHighlight">
 * import org.scalatest.Spec
 *
 * class MySpec extends Spec {
 *
 *   it("should sleep for a second", SlowTest) {
 *     Thread.sleep(1000)
 *   }
 * }
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">import</span> org.scalatest.Spec
 * <br /><span class="stReserved">class</span> <span class="stType">MySpec</span> <span class="stReserved">extends</span> <span class="stType">Spec</span> {
 * <br />  it(<span class="stQuotedString">"should sleep for a second"</span>, <span class="stType">SlowTest</span>) {
 *     Thread.sleep(<span class="stLiteral">1000</span>)
 *   }
 * }
 * </pre>
 *
 * <p>
 * or in a <code>FlatSpec</code> or <code>FixtureFlatSpec</code> like this:
 * </p>
 *
 * <pre class="stHighlight">
 * import org.scalatest.FlatSpec
 *
 * class MySpec extends FlatSpec {
 *
 *   it should "sleep for a second" taggedAs(SlowTest) in {
 *     Thread.sleep(1000)
 *   }
 * }
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">import</span> org.scalatest.FlatSpec
 * <br /><span class="stReserved">class</span> <span class="stType">MySpec</span> <span class="stReserved">extends</span> <span class="stType">FlatSpec</span> {
 * <br />  it should <span class="stQuotedString">"sleep for a second"</span> taggedAs(<span class="stType">SlowTest</span>) in {
 *     Thread.sleep(<span class="stLiteral">1000</span>)
 *   }
 * }
 * </pre>
 *
 * <p>
 * or in a <code>WordSpec</code> or <code>FixtureWordSpec</code> like this:
 * </p>
 *
 * <pre class="stHighlight">
 * import org.scalatest.WordSpec
 *
 * class MySpec extends WordSpec {
 *
 *   "should sleep for a second" taggedAs(SlowTest) in {
 *     Thread.sleep(1000)
 *   }
 * }
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">import</span> org.scalatest.WordSpec
 * <br /><span class="stReserved">class</span> <span class="stType">MySpec</span> <span class="stReserved">extends</span> <span class="stType">WordSpec</span> {
 * <br />  <span class="stQuotedString">"should sleep for a second"</span> taggedAs(<span class="stType">SlowTest</span>) in {
 *     Thread.sleep(<span class="stLiteral">1000</span>)
 *   }
 * }
 * </pre>
 *
 * <p>
 * or in a <code>FeatureSpec</code> or <code>FixtureFeatureSpec</code> like this:
 * </p>
 *
 * <pre class="stHighlight">
 * import org.scalatest.FeatureSpec
 *
 * class MySpec extends FeatureSpec {
 *
 *   scenario("should sleep for a second", SlowTest) {
 *     Thread.sleep(1000)
 *   }
 * }
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">import</span> org.scalatest.FeatureSpec
 * <br /><span class="stReserved">class</span> <span class="stType">MySpec</span> <span class="stReserved">extends</span> <span class="stType">FeatureSpec</span> {
 * <br />  scenario(<span class="stQuotedString">"should sleep for a second"</span>, <span class="stType">SlowTest</span>) {
 *     Thread.sleep(<span class="stLiteral">1000</span>)
 *   }
 * }
 * </pre>
 *
 * <p>
 * Alternatively you can create Tag objects using <code>new</code> or by using the factory method in the Tag object. E.g.,
 * using the example scenario from above:
 * </p>
 *
 * <pre class="stHighlight">
 *   scenario("should sleep for a second", new Tag("SlowTest"))
 * </pre><pre class="stHighlighted">
 * scenario(<span class="stQuotedString">"should sleep for a second"</span>, <span class="stReserved">new</span> <span class="stType">Tag</span>(<span class="stQuotedString">"SlowTest"</span>))
 * </pre>
 *
 * <p>
 * or just:
 * </p>
 *
 * <pre class="stHighlight">
 *   scenario("should sleep for a second", Tag("SlowTest"))
 * </pre><pre class="stHighlighted">
 * scenario(<span class="stQuotedString">"should sleep for a second"</span>, <span class="stType">Tag</span>(<span class="stQuotedString">"SlowTest"</span>))
 * </pre>
 *
 * If you have created Java annotation interfaces for use as tag names in direct subclasses of <code>org.scalatest.Suite</code>,
 * then you may want to use group names on your <code>FunSuite</code>s and <code>Spec</code>s that match. To do so, simply 
 * pass the fully qualified names of the Java interface to the <code>Tag</code> constructor. For example, if you've
 * defined a Java annotation interface with fully qualified name, <code>com.mycompany.testtags.SlowTest</code>, then you could
 * create a matching group for <code>FunSuite</code>s like this:
 *
 * <pre class="stHighlight">
 * object SlowTest extends Tag("com.mycompany.testtags.SlowTest")
 * </pre><pre class="stHighlighted">
 * <span class="stReserved">object</span> <span class="stType">SlowTest</span> <span class="stReserved">extends</span> <span class="stType">Tag</span>(<span class="stQuotedString">"com.mycompany.testtags.SlowTest"</span>)
 * </pre>
 *
 * @author Bill Venners
 * @author George Berger
 */
class Tag(val name: String)

/**
 * Companion object for <code>Tag</code>, which offers a factory method.
 *
 * @author George Berger
 * @author Bill Venners
 */
object Tag {

  /**
   * Factory method for creating new <code>Tag</code> objects.
   */
  def apply(name: String): Tag = {
    new Tag(name)
  }
}

