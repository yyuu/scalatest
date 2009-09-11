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
package org.scalatest.mock

import org.scalatest._
import fixture.FixtureSuite

/**
 * Trait that will pass a new <code>JMockCycle</code> into any test that needs one.
 *
 * <p>
 * This trait, which must be mixed into a <code>FixtureSuite</code>, defines the
 * <code>Fixture</code> type to be <code>JMockCycle</code> and defines a
 * <code>withFixture</code> method that instantiates a new <code>JMockCycle</code>
 * and passes it to the test function.
 * </p>
 *
 * @author Bill Venners
 */
trait JMockCycleFixture { this: FixtureSuite =>

  /**
   * Defines the <code>Fixture</code> type to be <code>JMockCycle</code>.
   */
  type Fixture = JMockCycle

  /**
   * Instantiates a new <code>JMockCycle</code> and passes it to the test function.
   *
   * @param test the test function to which to pass a new <code>JMockCycle</code>
   */
  def withFixture(test: Test1) {
    test(new JMockCycle)
  }
}
