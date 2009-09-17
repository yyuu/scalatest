package org.scalatest

class ClickSuite extends SuperSuite(
  List(
    new org.scalatest.SuiteSuite,
    new BeforeAndAfterSuite,
    new CatchReporterSuite,
    new EasySuite,
    new ExamplesSuite,
    new FunSuiteSuite,
    new SpecSuite,
    new SuiteSuite
  )
)
