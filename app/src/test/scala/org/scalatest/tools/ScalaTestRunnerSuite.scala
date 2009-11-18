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
package org.scalatest.tools {

import org.scalatest.FunSuite
import org.scalatools.testing.{Event, Result, Logger}

object AntSkipTest extends Tag("AntSkipTest") // skip during ant builds

  // testing runner.run:
  // def run(testClassName: String, fingerprint: TestFingerprint, args: Array[String]): Array[Event]
  class ScalaTestRunnerSuite extends FunSuite {

    test("call with simple class") {
      val results = runner.run("org.scalatest.tools.SimpleTest", fingerprint, Array())
      assert(results.size === 1)
      assert(results(0).testName === "hello, world")
      assert(results(0).result === Result.Success)
    }

    test("three different results") {
      val results = runner.run("org.scalatest.tools.ThreeTestsTest", fingerprint, Array())
      assert(results.size === 3)

      assert(results(0).testName === "hello, world")
      assert(results(0).result === Result.Success)

      assert(results(1).testName === "throw")
      assert(results(1).result === Result.Failure)
      assert(results(1).error.getMessage === "baah")

      assert(results(2).testName === "assert bad")
      assert(results(2).result === Result.Failure)
      assert(results(2).error.getMessage === "1 did not equal 3")
    }

    test("illegal arg on private constructor"){
      intercept[IllegalArgumentException] {
        runner.run("org.scalatest.tools.PrivateConstructor", fingerprint, Array())
      }
    }


    test("skipped test results in Result.Skipped") {
      val results = runner.run("org.scalatest.tools.SuiteWithSkippedTest", fingerprint, Array())
      assert(results.size === 2)

      assert(results(0).testName === "dependeeThatFails")
      assert(results(0).result === Result.Failure)
      assert(results(0).error.getMessage === "fail")

      assert(results(1).testName === "depender")
      assert(results(1).result === Result.Skipped)
    }


    test("pending test results in Result.Skipped") {
      val results = runner.run("org.scalatest.tools.PendingTest", fingerprint, Array())
      assert(results.size === 1)

      assert(results(0).testName === "i am pending")
      assert(results(0).result === Result.Skipped)
    }

    val framework = new ScalaTestFramework

    val runner: ScalaTestRunner = {
      framework.testRunner(currentThread.getContextClassLoader, Array(new TestLogger)).asInstanceOf[ScalaTestRunner]
    }

    val fingerprint = {
      val fingerprints = framework.tests
      fingerprints(0)
    }

    class TestLogger extends Logger {
      def error(msg: String) {}
      def warn(msg: String) {}
      def info(msg: String) {}
      def debug(msg: String) {}
      def ansiCodesSupported = false
    }

  }

  private class SimpleTest extends FunSuite {
    test("hello, world") {"hello, world"}
  }

  private class ThreeTestsTest extends FunSuite {
    test("hello, world") {"hello, world"}
    test("throw", AntSkipTest) {throw new Exception("baah")}
    test("assert bad", AntSkipTest) {assert(1 === 3)}
  }

  private class PrivateConstructor private() extends FunSuite

  private class PendingTest extends FunSuite {
    test("i am pending")(pending)
  }

  import org.scalatest.testng.TestNGSuite
  private class SuiteWithSkippedTest extends TestNGSuite {
    import org.testng.annotations.Test
    @Test{val groups = Array("run", "AntSkipTest")} def dependeeThatFails() { throw new Exception("fail") }
    @Test{val dependsOnGroups = Array("run")} def depender() {}
    /* For 2.8
      @Test(groups = Array("run")) def dependeeThatFails() { throw new Exception("fail") }
      @Test(dependsOnGroups = Array("run")) def depender() {}
    */
  }

}
