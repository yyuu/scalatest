package org.scalatest.sbt

import org.scalatools.testing.{Logger, Framework}

class ScalaTestFramework extends Framework
{
  def name = "ScalaTest"
  
  def tests = Array(new org.scalatools.testing.TestFingerprint{
    def superClassName = "org.scalatest.Suite"
    def isModule = false
  })

  def  testRunner(testLoader: ClassLoader, loggers: Array[Logger]) = {
    new ScalaTestRunner(testLoader, loggers)
  }
}