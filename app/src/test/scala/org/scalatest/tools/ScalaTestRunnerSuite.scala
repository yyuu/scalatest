package org.scalatest.tools

import org.scalatest.FunSuite
import org.scalatools.testing.{Event, Result, Logger}

// testing runner.run:
// def run(testClassName: String, fingerprint: TestFingerprint, args: Array[String]): Array[Event]
class ScalaTestRunnerSuite extends FunSuite {

  test("call with simple class"){
    val results = runner.run("org.scalatest.tools.test.SimpleTest", fingerprint, Array())
    assert(results.size === 1)
    assert(results(0).testName === "hello, world")
    assert(results(0).result === Result.Success)
  }

  test("three different results"){
    val results = runner.run("org.scalatest.tools.test.ThreeTestsTest", fingerprint, Array())
    assert(results.size === 3)

    assert(results(0).testName === "hello, world")
    assert(results(0).result === Result.Success)

    assert(results(1).testName === "throw")
    assert(results(1).result === Result.Failure)
    assert(results(1).error.getMessage === "baah")

    assert(results(2).testName === "assert bad")
    assert(results(2).result === Result.Failure)
    assert(results(2).error.getMessage === "expected 1 got three ....fix me!")
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
    def error(msg:String){}
    def warn(msg:String){}
    def info(msg:String){}
    def debug(msg:String){}
    def ansiCodesSupported = false
  }
}

package test {
  class SimpleTest extends FunSuite {
    test("hello, world"){ "hello, world" }
  }

  class ThreeTestsTest extends FunSuite {
    test("hello, world"){ "hello, world" }
    test("throw"){ throw new Exception("baah") }
    test("assert bad"){ assert( 1 === 3 ) }
  }
}
