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
package org.scalatest.prop

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class TableSpec extends WordSpec with ShouldMatchers with PropertyChecks {

  "A table" should {

    "take a row of column names and rows of elements" in {

      val examples =
        Table(
          ("start", "eat", "left"),
          (     12,     5,      7),
          (     12,     5,      8), // Should fail
          (     20,     5,     15)
        )

       evaluating {
         forAll (examples) { (start: Int, eat: Int, left: Int) =>
           start - eat should equal (left)
         }
       } should produce [TestFailedException]

       forAll (examples) { (start: Int, eat: Int, left: Int) =>
         whenever (left != 8) {
           start - eat should equal (left)
         }
       }
     }
  }
}
