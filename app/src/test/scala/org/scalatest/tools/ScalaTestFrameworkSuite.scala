package org.scalatest.tools

import org.scalatest.FunSuite
import org.scalatools.testing.Logger

class ScalaTestFrameworkSuite extends FunSuite{

  test("framework name"){
    assert(new ScalaTestFramework().name === "ScalaTest")
  }

  test("tests contains single test fingerprint"){
    val framework = new ScalaTestFramework
    val fingerprints = framework.tests
    assert(fingerprints.size == 0)
    assert(fingerprints(0).isModule === false)
    assert(fingerprints(0).superClassName === "org.scalatest.Suite")
  }

  test("creates runner with given arguments"){
    val framework = new ScalaTestFramework
    val loggers: Array[Logger] = Array(new TestLogger)
    val runner = framework.testRunner(currentThread.getContextClassLoader, loggers).asInstanceOf[ScalaTestRunner]
    assert(runner.testLoader == currentThread.getContextClassLoader)
    assert(runner.loggers === loggers)
  }

  class TestLogger extends Logger{
    def error(msg:String){}
    def warn(msg:String){}
    def info(msg:String){}
    def debug(msg:String){}
    def ansiCodesSupported = false
  }
}