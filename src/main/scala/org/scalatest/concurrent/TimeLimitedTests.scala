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

/**
 * Trait mixed into exceptions thrown by <code>failAfter</code> due to a timeout.
 */
trait TimeLimitedTests extends AbstractSuite { this: Suite =>

  abstract override def withFixture(test: NoArgTest) {
    super.withFixture(test)
/*
    try {
      super.withFixture(test)
    }
    catch {
      case e: TimeoutException => e.timeout
    }
*/
  }

  /**
   * The time limit, in milliseconds, in which each test in a <code>Suite</code> that mixes in
   * <code>TimeLimitedTests</code> must complete.
   *
   * 
   */
  def timeLimit: Long
}

/*
Will need to add cancelAfter to the doc comment in 2.0.
*/

