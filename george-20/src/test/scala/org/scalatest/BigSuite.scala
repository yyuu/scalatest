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

class BigSuite(nestedSuiteCount: Option[Int]) extends FunSuite {

  def this() = this(None)
  
  override def nestedSuites = {

    def makeList(remaining: Int, soFar: List[Suite], nestedCount: Int): List[Suite] =
      if (remaining == 0) soFar
      else makeList(remaining - 1, (new BigSuite(Some(nestedCount - 1)) :: soFar), nestedCount)

    nestedSuiteCount match {
      case None =>
        val sizeString = System.getProperty("org.scalatest.BigSuite.size", "0")
        val size =
          try {
            sizeString.toInt
          }
          catch {
            case e: NumberFormatException => 0
          }
        makeList(size, Nil, size)
      case Some(n) =>
        if (n == 0) List()
        else {
          makeList(n, Nil, n)
        }
    }
  }

  test("number 1") {
    nestedSuiteCount match {
      case Some(0) => assert(1 + 1 === 3)
      case _ => assert(1 + 1 === 2)
    }
  }

  test("number 2") {
    assert(1 + 1 === 2)
  }

  test("number 3") {
    assert(1 + 1 === 2)
  }

  test("number 4") {
    assert(1 + 1 === 2)
  }

  test("number 5") {
    assert(1 + 1 === 2)
  }

  test("number 6") {
    assert(1 + 1 === 2)
  }

  test("number 7") {
    assert(1 + 1 === 2)
  }

  test("number 8") {
    assert(1 + 1 === 2)
  }

  test("number 9") {
    assert(1 + 1 === 2)
  }

  test("number 10") {
    assert(1 + 1 === 2)
  }

  test("number 11") {
    assert(1 + 1 === 2)
  }

  test("number 12") {
    assert(1 + 1 === 2)
  }

  test("number 13") {
    assert(1 + 1 === 2)
  }

  test("number 14") {
    assert(1 + 1 === 2)
  }

  test("number 15") {
    assert(1 + 1 === 2)
  }

  test("number 16") {
    assert(1 + 1 === 2)
  }

  test("number 17") {
    assert(1 + 1 === 2)
  }

  test("number 18") {
    assert(1 + 1 === 2)
  }

  test("number 19") {
    assert(1 + 1 === 2)
  }

  test("number 20") {
    assert(1 + 1 === 2)
  }

  test("number 21") {
    assert(1 + 1 === 2)
  }

  test("number 22") {
    assert(1 + 1 === 2)
  }

  test("number 23") {
    assert(1 + 1 === 2)
  }

  test("number 24") {
    assert(1 + 1 === 2)
  }

  test("number 25") {
    assert(1 + 1 === 2)
  }

  test("number 26") {
    assert(1 + 1 === 2)
  }

  test("number 27") {
    assert(1 + 1 === 2)
  }

  test("number 28") {
    assert(1 + 1 === 2)
  }

  test("number 29") {
    assert(1 + 1 === 2)
  }

  test("number 30") {
    assert(1 + 1 === 2)
  }

  test("number 31") {
    assert(1 + 1 === 2)
  }

  test("number 32") {
    assert(1 + 1 === 2)
  }

  test("number 33") {
    assert(1 + 1 === 2)
  }

  test("number 34") {
    assert(1 + 1 === 2)
  }

  test("number 35") {
    assert(1 + 1 === 2)
  }

  test("number 36") {
    assert(1 + 1 === 2)
  }

  test("number 37") {
    assert(1 + 1 === 2)
  }

  test("number 38") {
    assert(1 + 1 === 2)
  }

  test("number 39") {
    assert(1 + 1 === 2)
  }

  test("number 40") {
    assert(1 + 1 === 2)
  }

  test("number 41") {
    assert(1 + 1 === 2)
  }

  test("number 42") {
    assert(1 + 1 === 2)
  }

  test("number 43") {
    assert(1 + 1 === 2)
  }

  test("number 44") {
    assert(1 + 1 === 2)
  }

  test("number 45") {
    assert(1 + 1 === 2)
  }

  test("number 46") {
    assert(1 + 1 === 2)
  }

  test("number 47") {
    assert(1 + 1 === 2)
  }

  test("number 48") {
    assert(1 + 1 === 2)
  }

  test("number 49") {
    assert(1 + 1 === 2)
  }

  test("number 50") {
    assert(1 + 1 === 2)
  }

  test("number 51") {
    assert(1 + 1 === 2)
  }

  test("number 52") {
    assert(1 + 1 === 2)
  }

  test("number 53") {
    assert(1 + 1 === 2)
  }

  test("number 54") {
    assert(1 + 1 === 2)
  }

  test("number 55") {
    assert(1 + 1 === 2)
  }

  test("number 56") {
    assert(1 + 1 === 2)
  }

  test("number 57") {
    assert(1 + 1 === 2)
  }

  test("number 58") {
    assert(1 + 1 === 2)
  }

  test("number 59") {
    assert(1 + 1 === 2)
  }

  test("number 60") {
    assert(1 + 1 === 2)
  }

  test("number 61") {
    assert(1 + 1 === 2)
  }

  test("number 62") {
    assert(1 + 1 === 2)
  }

  test("number 63") {
    assert(1 + 1 === 2)
  }

  test("number 64") {
    assert(1 + 1 === 2)
  }

  test("number 65") {
    assert(1 + 1 === 2)
  }

  test("number 66") {
    assert(1 + 1 === 2)
  }

  test("number 67") {
    assert(1 + 1 === 2)
  }

  test("number 68") {
    assert(1 + 1 === 2)
  }

  test("number 69") {
    assert(1 + 1 === 2)
  }

  test("number 70") {
    assert(1 + 1 === 2)
  }

  test("number 71") {
    assert(1 + 1 === 2)
  }

  test("number 72") {
    assert(1 + 1 === 2)
  }

  test("number 73") {
    assert(1 + 1 === 2)
  }

  test("number 74") {
    assert(1 + 1 === 2)
  }

  test("number 75") {
    assert(1 + 1 === 2)
  }

  test("number 76") {
    assert(1 + 1 === 2)
  }

  test("number 77") {
    assert(1 + 1 === 2)
  }

  test("number 78") {
    assert(1 + 1 === 2)
  }

  test("number 79") {
    assert(1 + 1 === 2)
  }

  test("number 80") {
    assert(1 + 1 === 2)
  }

  test("number 81") {
    assert(1 + 1 === 2)
  }

  test("number 82") {
    assert(1 + 1 === 2)
  }

  test("number 83") {
    assert(1 + 1 === 2)
  }

  test("number 84") {
    assert(1 + 1 === 2)
  }

  test("number 85") {
    assert(1 + 1 === 2)
  }

  test("number 86") {
    assert(1 + 1 === 2)
  }

  test("number 87") {
    assert(1 + 1 === 2)
  }

  test("number 88") {
    assert(1 + 1 === 2)
  }

  test("number 89") {
    assert(1 + 1 === 2)
  }

  test("number 90") {
    assert(1 + 1 === 2)
  }

  test("number 91") {
    assert(1 + 1 === 2)
  }

  test("number 92") {
    assert(1 + 1 === 2)
  }

  test("number 93") {
    assert(1 + 1 === 2)
  }

  test("number 94") {
    assert(1 + 1 === 2)
  }

  test("number 95") {
    assert(1 + 1 === 2)
  }

  test("number 96") {
    assert(1 + 1 === 2)
  }

  test("number 97") {
    assert(1 + 1 === 2)
  }

  test("number 98") {
    assert(1 + 1 === 2)
  }

  test("number 99") {
    assert(1 + 1 === 2)
  }

  test("number 100") {
    assert(1 + 1 === 2)
  }
}
