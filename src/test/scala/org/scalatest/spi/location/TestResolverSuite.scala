package org.scalatest.spi.location

import org.scalatest.FunSuite
import org.scalatest.Suite
import org.scalatest.Style
import org.scalatest.FeatureSpec
import org.scalatest.fixture.FixtureSuite
import org.scalatest.StringFixture

class TestResolverSuite extends FunSuite {
  
  def expectFun(test: Test, expectedClassName: String, expectedDisplayName: String, expectedTestNames: Array[String]) {
    expect(expectedClassName)(test.getClassName)
    expect(expectedDisplayName)(test.getDisplayName)
    expect(expectedTestNames.deepToString)(test.getTestNames.deepToString)
  }

  test("MethodTestResolver should resolve test name for tests written in test suite that extends org.scalatest.Suite") {
    class TestingSuite extends Suite {
      def testMethod1() {
        
      }
      def testMethod2() {
        
      }
      def testMethod3() {
        
      }
    }
    val testingSuiteClass = classOf[TestingSuite]
    val testingSuiteResolver: TestResolver = LocationUtils.getTestResolver(testingSuiteClass)
    assert(testingSuiteResolver.getClass == classOf[MethodTestResolver], "Suite that uses org.scalatest.Suite should use MethodTestResolver.")
    val testMethod1 = testingSuiteResolver.resolveTest(new MethodDefinition(testingSuiteClass.getName, null, Array.empty, "testMethod1"))
    expectFun(testMethod1, testingSuiteClass.getName, testingSuiteClass.getName + ".testMethod1", Array("testMethod1"))
    val testMethod2 = testingSuiteResolver.resolveTest(new MethodDefinition(testingSuiteClass.getName, null, Array.empty, "testMethod2"))
    expectFun(testMethod2, testingSuiteClass.getName, testingSuiteClass.getName + ".testMethod2", Array("testMethod2"))
    val testMethod3 = testingSuiteResolver.resolveTest(new MethodDefinition(testingSuiteClass.getName, null, Array.empty, "testMethod3"))
    expectFun(testMethod3, testingSuiteClass.getName, testingSuiteClass.getName + ".testMethod3", Array("testMethod3"))
  }
  
  test("MethodTestResolver should resolve test name for tests written in test suite that extends org.scalatest.fixture.FixtureSuite") {
    class TestingSuite extends FixtureSuite with StringFixture {
      def testMethod1() { arg: String =>
        
      }
      def testMethod2() { arg: String =>
        
      }
      def testMethod3() { arg: String =>
        
      }
    }
    val suiteClass = classOf[TestingSuite]
    val testingResolver: TestResolver = LocationUtils.getTestResolver(suiteClass)
    assert(testingResolver.getClass == classOf[MethodTestResolver], "Suite that uses org.scalatest.fixture.FixtureSuite should use MethodTestResolver.")
    val testMethod1 = testingResolver.resolveTest(new MethodDefinition(suiteClass.getName, null, Array.empty, "testMethod1"))
    expectFun(testMethod1, suiteClass.getName, suiteClass.getName + ".testMethod1", Array("testMethod1"))
    val testMethod2 = testingResolver.resolveTest(new MethodDefinition(suiteClass.getName, null, Array.empty, "testMethod2"))
    expectFun(testMethod2, suiteClass.getName, suiteClass.getName + ".testMethod2", Array("testMethod2"))
    val testMethod3 = testingResolver.resolveTest(new MethodDefinition(suiteClass.getName, null, Array.empty, "testMethod3"))
    expectFun(testMethod3, suiteClass.getName, suiteClass.getName + ".testMethod3", Array("testMethod3"))
  }
  
  test("FunctionTestResolver should resolve test name for tests written in test suite that extends org.scalatest.FunSuite") {
    class TestingFunSuite extends FunSuite {
      test("test 1") {
        
      }
      test("test 2") {
        
      }
      test("test 3") {
        
      }
    }
    val suiteClass = classOf[TestingFunSuite]
    val testResolver: TestResolver = LocationUtils.getTestResolver(suiteClass)
    assert(testResolver.getClass == classOf[FunctionTestResolver], "Suite that uses org.scalatest.FunSuite should use FunctionTestResolver.")
    val test1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, Array.empty, null, "test", "test 1"))
    expectFun(test1, suiteClass.getName, suiteClass.getName + ": \"test 1\"", Array("test 1"))
    val test2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, Array.empty, null, "test", "test 2"))
    expectFun(test2, suiteClass.getName, suiteClass.getName + ": \"test 2\"", Array("test 2"))
    val test3 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, Array.empty, null, "test", "test 3"))
    expectFun(test3, suiteClass.getName, suiteClass.getName + ": \"test 3\"", Array("test 3"))
  }
  
  test("FeatureSpecTestResolver should resolve test name for tests written in test suite that extends org.scalatest.FeatureSpec") {
    class TestingFeatureSpec extends FeatureSpec {
      feature("feature 1") {
        scenario("scenario 1") {
          
        }
        scenario("scenario 2") {
          
        }
      }
      feature("feature 2") {
        scenario("scenario 1") {
          
        }
        scenario("scenario 2") {
          
        }
      }
    }
    val suiteClass = classOf[TestingFeatureSpec]
    val testResolver: TestResolver = LocationUtils.getTestResolver(suiteClass)
    assert(testResolver.getClass == classOf[FeatureSpecTestResolver], "Suite that uses org.scalatest.FeatureSpec should use FeatureSpecTestResolver.")
    
    val f1s1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, new MethodInvocation(suiteClass.getName, null, Array.empty, null, "feature", "feature 1"), Array.empty, null, "scenario", "scenario 1"))
    expectFun(f1s1, suiteClass.getName, "feature 1 scenario 1", Array("feature 1 scenario 1"))
    val f1s2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, new MethodInvocation(suiteClass.getName, null, Array.empty, null, "feature", "feature 1"), Array.empty, null, "scenario", "scenario 2"))
    expectFun(f1s2, suiteClass.getName, "feature 1 scenario 2", Array("feature 1 scenario 2"))
    
    val f2s1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, new MethodInvocation(suiteClass.getName, null, Array.empty, null, "feature", "feature 2"), Array.empty, null, "scenario", "scenario 1"))
    expectFun(f2s1, suiteClass.getName, "feature 2 scenario 1", Array("feature 2 scenario 1"))
    val f2s2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, new MethodInvocation(suiteClass.getName, null, Array.empty, null, "feature", "feature 2"), Array.empty, null, "scenario", "scenario 2"))
    expectFun(f2s2, suiteClass.getName, "feature 2 scenario 2", Array("feature 2 scenario 2"))
    
    val f1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName(), null, Array(
      new MethodInvocation(suiteClass.getName(), null, Array.empty, null, "scenario", "scenario 1"),
      new MethodInvocation(suiteClass.getName(), null, Array.empty, null, "scenario", "scenario 2")
     ), null, "feature", "feature 1" ))
    expectFun(f1, suiteClass.getName, "feature 1", Array("feature 1 scenario 1", "feature 1 scenario 2"))
    
    val f2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName(), null, Array(
      new MethodInvocation(suiteClass.getName(), null, Array.empty, null, "scenario", "scenario 1"),
      new MethodInvocation(suiteClass.getName(), null, Array.empty, null, "scenario", "scenario 2")
     ), null, "feature", "feature 2" ))
    expectFun(f2, suiteClass.getName, "feature 2", Array("feature 2 scenario 1", "feature 2 scenario 2"))
  }
}