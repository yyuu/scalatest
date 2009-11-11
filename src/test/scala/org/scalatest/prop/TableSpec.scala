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
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._

class TableSpec extends WordSpec with ShouldMatchers with PropertyChecks {

  "A table" should {

    "work properly with 3 columns" in {

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

    "work properly with 4 columns" in {

      val examples =
        Table(
          (     "n",     "d", "numer", "denom"),
          (       1,       2,       1,       2),
          (       1,       2,       1,       3), // Should fail
          (      -1,       2,      -1,       2),
          (       1,      -2,       1,      -2)
        )

      evaluating {
        forAll (examples) { (n: Int, d: Int, numer: Int, denom: Int) =>
          val r = new Rational(n, d)
          r.numer should equal (numer)
          r.denom should equal (denom)
        }
      } should produce [TestFailedException]

      forAll (examples) { (n: Int, d: Int, numer: Int, denom: Int) =>
        whenever (denom != 3) {
          val r = new Rational(n, d)
          r.numer should equal (numer)
          r.denom should equal (denom)
        }
      }
    }

    "work properly with 2 columns" in {

      val examples =
        Table(
          (   "n",   "d"),
          (     1,     2),
          (    -1,     0), // Should fail
          (    -1,     2),
          (     1,    -2)
        )

      evaluating {
        forAll (examples) { (n: Int, d: Int) =>
          def gcd(a: Int, b: Int): Int =
            if (b == 0) a else gcd(b, a % b)
          val g = gcd(n.abs, d.abs)
          val r = new Rational(n, d)
          r.numer should equal (n / g)
          r.denom should equal (d / g)
        }
      } should produce [IllegalArgumentException]

      forAll (examples) { (n: Int, d: Int) =>
        whenever (d != 0) {
          def gcd(a: Int, b: Int): Int =
            if (b == 0) a else gcd(b, a % b)
          val g = gcd(n.abs, d.abs)
          val r = new Rational(n, d)
          r.numer should equal (n / g)
          r.denom should equal (d / g)
        }
      }
    }

    "work properly with no columns" in {

      forAll { (n: Int, d: Int) =>
        whenever (d != 0) {
          def gcd(a: Int, b: Int): Int =
            if (b == 0) a else gcd(b, a % b)
          val g = gcd(n.abs, d.abs)
          val r = new Rational(n, d)
          r.numer should equal (n / g)
          r.denom should equal (d / g)
        }
      }
    }

    "fail for sure" in {
      forAll { (a: String, b: String) => 
        (a + b).length should be > a.length
        (a + b).length should be > b.length
      }
    }
  }
}
