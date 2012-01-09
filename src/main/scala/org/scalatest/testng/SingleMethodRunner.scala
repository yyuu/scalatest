package org.scalatest.testng

import org.testng.TestNG

trait SingleMethodRunner {
  def run(testName: String, testng: TestNG)
}