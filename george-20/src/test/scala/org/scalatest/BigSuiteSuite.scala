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
package org.scalatest

class BigSuiteSuite extends FunSuite {
  test("a BigSuite(Some(0)) has 100 tests") {
    val bs = new BigSuite(Some(0))
    assert(bs.expectedTestCount(Filter()) === 100)
  }
  test("a BigSuite(Some(0)) has no nested suites") {
    val bs = new BigSuite(Some(0))
    assert(bs.nestedSuites.size === 0)
  }
  test("a BigSuite(Some(1)) has 1 nested suite") {
    val bs = new BigSuite(Some(1))
    assert(bs.nestedSuites.size === 1)
  }
  test("a BigSuite(Some(1))'s 1 nested suite has no nested suites") {
    val bs = new BigSuite(Some(1))
    assert(bs.nestedSuites.head.nestedSuites.size === 0)
  }
  test("a BigSuite(Some(2)) has 2 nested suites") {
    val bs = new BigSuite(Some(2))
    assert(bs.nestedSuites.size === 2)
  }
  test("a BigSuite(Some(1))'s 2 nested suites have one nested suite each, etc.") {
    val bs = new BigSuite(Some(2))
    for (suite <- bs.nestedSuites) {
      assert(suite.nestedSuites.size === 1)
      for (s2 <- suite.nestedSuites)
        assert(s2.nestedSuites.size === 0)
    }
  }
  test("a BigSuite(Some(3)) has 3 nested suites") {
    val bs = new BigSuite(Some(3))
    assert(bs.nestedSuites.size === 3)
  }
  test("a BigSuite(Some(2))'s 3 nested suites have two nested suites each, etc.") {
    val bs = new BigSuite(Some(3))
    for (suite <- bs.nestedSuites) {
      assert(suite.nestedSuites.size === 2)
      for (s2 <- suite.nestedSuites) {
        assert(s2.nestedSuites.size === 1)
        for (s3 <- s2.nestedSuites)
          assert(s3.nestedSuites.size === 0)
      }
    }
  }
}