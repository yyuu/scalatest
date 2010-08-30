/*
 * Copyright 2001-2009 Artima, Inc.
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
package org.scalatest.verb

import org.scalatest._

/**
 * Provides an implicit conversion that adds <code>can</code> methods to <code>String</code>
 * to support the syntax of <code>FlatSpec</code>, <code>WordSpec</code>, <code>FixtureFlatSpec</code>,
 * and <code>FixtureWordSpec</code>.
 *
 * <p>
 * For example, this trait enables syntax such as the following test registration in <code>FlatSpec</code>
 * and <code>FixtureFlatSpec</code>:
 * </p>
 *
 * <pre>
 * "A Stack (when empty)" can "be empty" in { ... }
 *                        ^
 * </pre>
 *
 * <p>
 * It also enables syntax such as the following shared test registration in <code>FlatSpec</code>
 * and <code>FixtureFlatSpec</code>:
 * </p>
 *
 * <pre>
 * "A Stack (with one item)" can behave like nonEmptyStack(stackWithOneItem, lastValuePushed)
 *                           ^
 * </pre>
 *
 * <p>
 * In addition, it supports the registration of subject descriptions in <code>WordSpec</code>
 * and <code>FixtureWordSpec</code>, such as:
 * </p>
 *
 * <pre>
 * "A Stack (when empty)" can { ...
 *                        ^
 * </pre>
 *
 * <p>
 * And finally, it also supportds the registration of subject descriptions with after words
 * in <code>WordSpec</code> and <code>FixtureWordSpec</code>. For example:
 * </p>
 *
 * <pre>
 *    def provide = afterWord("provide")
 *
 *   "The ScalaTest Matchers DSL" can provide {
 *                                ^
 * </pre>
 *
 * <p>
 * The reason this implicit conversion is provided in a separate trait, instead of being provided
 * directly in <code>FlatSpec</code>, <code>WordSpec</code>, <code>FixtureFlatSpec</code>, and
 * <code>FixtureWordSpec</code>, is primarily for design symmetry with <code>ShouldVerb</code>
 * and <code>MustVerb</code>. Both <code>ShouldVerb</code> and <code>MustVerb</code> must exist
 * as a separate trait because an implicit conversion provided directly would conflict
 * with the implicit conversion that provides <code>should</code> or <code>must</code> methods on <code>String</code>
 * in the <code>ShouldMatchers</code> and <code>MustMatchers</code> traits.
 * </p>
 *
 * @author Bill Venners
 */
trait CanVerb {

  // This one can be final, because it isn't extended by anything in the matchers DSL.
  /**
   * This class supports the syntax of <code>FlatSpec</code>, <code>WordSpec</code>, <code>FixtureFlatSpec</code>,
   * and <code>FixtureWordSpec</code>.
   *
   * <p>
   * This class is used in conjunction with an implicit conversion to enable <code>can</code> methods to
   * be invoked on <code>String</code>s.
   * </p>
   *
   * @author Bill Venners
   */
  final class StringCanWrapperForVerb(left: String) {

    /**
     * Supports test registration in <code>FlatSpec</code> and <code>FixtureFlatSpec</code>.
     *
     * <p>
     * For example, this method enables syntax such as the following in <code>FlatSpec</code>
     * and <code>FixtureFlatSpec</code>:
     * </p>
     *
     * <pre>
     * "A Stack (when empty)" can "be empty" in { ... }
     *                        ^
     * </pre>
     *
     * <p>
     * <code>FlatSpec</code> passes in a function via the implicit parameter that takes
     * three strings and results in a <code>ResultOfStringPassedToVerb</code>. This method
     * simply invokes this function, passing in left, the verb string
     * <code>"can"</code>, and right, and returns the result.
     * </p>
     */
    def can(right: String)(implicit fun: (String, String, String) => ResultOfStringPassedToVerb): ResultOfStringPassedToVerb = {
      fun(left, "can", right)
    }

    /**
     * Supports shared test registration in <code>FlatSpec</code> and <code>FixtureFlatSpec</code>.
     *
     * <p>
     * For example, this method enables syntax such as the following in <code>FlatSpec</code>
     * and <code>FixtureFlatSpec</code>:
     * </p>
     *
     * <pre>
     * "A Stack (with one item)" can behave like nonEmptyStack(stackWithOneItem, lastValuePushed)
     *                           ^
     * </pre>
     *
     * <p>
     * <code>FlatSpec</code> and <code>FixtureFlatSpec</code> passes in a function via the implicit parameter that takes
     * a string and results in a <code>BehaveWord</code>. This method
     * simply invokes this function, passing in left, and returns the result.
     * </p>
     */
    def can(right: BehaveWord)(implicit fun: (String) => BehaveWord): BehaveWord = {
      fun(left)
    }

    /**
     * Supports the registration of subject descriptions in <code>WordSpec</code>
     * and <code>FixtureWordSpec</code>.
     *
     * <p>
     * For example, this method enables syntax such as the following in <code>WordSpec</code>
     * and <code>FixtureWordSpec</code>:
     * </p>
     *
     * <pre>
     * "A Stack (when empty)" can { ...
     *                        ^
     * </pre>
     *
     * <p>
     * <code>WordSpec</code> passes in a function via the implicit parameter of type <code>StringVerbBlockRegistration</code>,
     * a function that takes two strings and a no-arg function and results in <code>Unit</code>. This method
     * simply invokes this function, passing in left, the verb string
     * <code>"can"</code>, and the right by-name parameter transformed into a
     * no-arg function.
     * </p>
     */
    def can(right: => Unit)(implicit fun: StringVerbBlockRegistration) {
      fun(left, "can", right _)
    }

    /**
     * Supports the registration of subject descriptions with after words
     * in <code>WordSpec</code> and <code>FixtureWordSpec</code>.
     *
     * <p>
     * For example, this method enables syntax such as the following in <code>WordSpec</code>
     * and <code>FixtureWordSpec</code>:
     * </p>
     *
     * <pre>
     *    def provide = afterWord("provide")
     *
     *   "The ScalaTest Matchers DSL" can provide {
     *                                ^
     * </pre>
     *
     * <p>
     * <code>WordSpec</code> passes in a function via the implicit parameter that takes
     * two strings and a <code>ResultOfAfterWordApplication</code> and results in <code>Unit</code>. This method
     * simply invokes this function, passing in left, the verb string
     * <code>"can"</code>, and the <code>ResultOfAfterWordApplication</code> passed to <code>can</code>.
     * </p>
     */
    def can(resultOfAfterWordApplication: ResultOfAfterWordApplication)(implicit fun: (String, String, ResultOfAfterWordApplication) => Unit) {
      fun(left, "can", resultOfAfterWordApplication)
    }
  }

  /**
   * Implicitly converts an object of type <code>String</code> to a <code>StringCanWrapper</code>,
   * to enable <code>can</code> methods to be invokable on that object.
   */
  implicit def convertToStringCanWrapper(o: String): StringCanWrapperForVerb = new StringCanWrapperForVerb(o)
}
