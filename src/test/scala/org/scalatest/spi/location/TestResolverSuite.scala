package org.scalatest.spi.location

import org.scalatest.FunSuite
import org.scalatest.Suite
import org.scalatest.Style
import org.scalatest.FeatureSpec
import org.scalatest.fixture.FixtureSuite
import org.scalatest.StringFixture
import org.scalatest.FreeSpec

class TestResolverSuite extends FunSuite {
  
  def expectTest(test: Test, expectedClassName: String, expectedDisplayName: String, expectedTestNames: Array[String]) {
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
    expectTest(testMethod1, testingSuiteClass.getName, testingSuiteClass.getName + ".testMethod1", Array("testMethod1"))
    val testMethod2 = testingSuiteResolver.resolveTest(new MethodDefinition(testingSuiteClass.getName, null, Array.empty, "testMethod2"))
    expectTest(testMethod2, testingSuiteClass.getName, testingSuiteClass.getName + ".testMethod2", Array("testMethod2"))
    val testMethod3 = testingSuiteResolver.resolveTest(new MethodDefinition(testingSuiteClass.getName, null, Array.empty, "testMethod3"))
    expectTest(testMethod3, testingSuiteClass.getName, testingSuiteClass.getName + ".testMethod3", Array("testMethod3"))
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
    expectTest(testMethod1, suiteClass.getName, suiteClass.getName + ".testMethod1", Array("testMethod1"))
    val testMethod2 = testingResolver.resolveTest(new MethodDefinition(suiteClass.getName, null, Array.empty, "testMethod2"))
    expectTest(testMethod2, suiteClass.getName, suiteClass.getName + ".testMethod2", Array("testMethod2"))
    val testMethod3 = testingResolver.resolveTest(new MethodDefinition(suiteClass.getName, null, Array.empty, "testMethod3"))
    expectTest(testMethod3, suiteClass.getName, suiteClass.getName + ".testMethod3", Array("testMethod3"))
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
    val test1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, null, Array.empty, "test", "test 1"))
    expectTest(test1, suiteClass.getName, suiteClass.getName + ": \"test 1\"", Array("test 1"))
    val test2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, null, Array.empty, "test", "test 2"))
    expectTest(test2, suiteClass.getName, suiteClass.getName + ": \"test 2\"", Array("test 2"))
    val test3 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, null, Array.empty, "test", "test 3"))
    expectTest(test3, suiteClass.getName, suiteClass.getName + ": \"test 3\"", Array("test 3"))
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
    
    val f1s1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, new MethodInvocation(suiteClass.getName, null, null, Array.empty, "feature", "feature 1"), Array.empty, "scenario", "scenario 1"))
    expectTest(f1s1, suiteClass.getName, "feature 1 scenario 1", Array("feature 1 scenario 1"))
    val f1s2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, new MethodInvocation(suiteClass.getName, null, null, Array.empty, "feature", "feature 1"), Array.empty, "scenario", "scenario 2"))
    expectTest(f1s2, suiteClass.getName, "feature 1 scenario 2", Array("feature 1 scenario 2"))
    
    val f2s1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, new MethodInvocation(suiteClass.getName, null, null, Array.empty, "feature", "feature 2"), Array.empty, "scenario", "scenario 1"))
    expectTest(f2s1, suiteClass.getName, "feature 2 scenario 1", Array("feature 2 scenario 1"))
    val f2s2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName, null, new MethodInvocation(suiteClass.getName, null, null, Array.empty, "feature", "feature 2"), Array.empty, "scenario", "scenario 2"))
    expectTest(f2s2, suiteClass.getName, "feature 2 scenario 2", Array("feature 2 scenario 2"))
    
    val f1 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName(), null, null, Array(
      new MethodInvocation(suiteClass.getName(), null, null, Array.empty, "scenario", "scenario 1"),
      new MethodInvocation(suiteClass.getName(), null, null, Array.empty, "scenario", "scenario 2")
     ), "feature", "feature 1" ))
    expectTest(f1, suiteClass.getName, "feature 1", Array("feature 1 scenario 1", "feature 1 scenario 2"))
    
    val f2 = testResolver.resolveTest(new MethodInvocation(suiteClass.getName(), null, null, Array(
      new MethodInvocation(suiteClass.getName(), null, null, Array.empty, "scenario", "scenario 1"),
      new MethodInvocation(suiteClass.getName(), null, null, Array.empty, "scenario", "scenario 2")
     ), "feature", "feature 2" ))
    expectTest(f2, suiteClass.getName, "feature 2", Array("feature 2 scenario 1", "feature 2 scenario 2"))
  }
  
  test("FreeSpecTestResolver should resolve test name for tests written in test suite that extends org.scalatest.FreeSpec") {
    class TestingFreeSpec extends FreeSpec {
      "A Stack" - {
        "whenever it is empty" - {
          "certainly ought to" - {
            "be empty" in {
        
            }
            "complain on peek" in {
          
            }
            "complain on pop" in {
          
            }
          }
        }
        "but when full, by contrast, must" - {
          "be full" in {
        
          }
          "complain on push" in {
          
          }
        }
      }
    }
    val suiteClass = classOf[TestingFreeSpec]
    val testResolver: TestResolver = LocationUtils.getTestResolver(suiteClass)
    assert(testResolver.getClass == classOf[FreeSpecTestResolver], "Suite that uses org.scalatest.FreeSpec should use FreeSpecTestResolver.")
    
    val aStackNode = new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "A Stack"), null, 
                     Array(
                           new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "whenever it is empty"), null, Array(
                             new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "certainly ought to"), null, Array(
                               new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "be empty"), null, Array(), "in", () => {}), 
                               new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "complain on peek"), null, Array(), "in", () => {}), 
                               new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "complain on pop"), null, Array(), "in", () => {})
                             ), "-", () => {})
                           ), "-", () => {}), 
                           new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "but when full, by contrast, must"), null, Array(
                             new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "be full"), null, Array(), "in", () => {}), 
                             new MethodInvocation(suiteClass.getName, new ToStringOwner(null, Array.empty, "complain on push"), null, Array(), "in", () => {})
                           ), "-", () => {})
                     ), "-", () => {})
    LocationUtils.fillAstNodeParent(aStackNode)
    val aStackTest = testResolver.resolveTest(aStackNode)
    expectTest(aStackTest, suiteClass.getName, "A Stack", Array(
      "A Stack whenever it is empty certainly ought to be empty", 
      "A Stack whenever it is empty certainly ought to complain on peek", 
      "A Stack whenever it is empty certainly ought to complain on pop", 
      "A Stack but when full, by contrast, must be full", 
      "A Stack but when full, by contrast, must complain on push"
    ))
    
    val wheneverItIsEmptyNode = aStackNode.children(0)
    val wheneverItIsEmptyTest = testResolver.resolveTest(wheneverItIsEmptyNode)
    expectTest(wheneverItIsEmptyTest, suiteClass.getName, "A Stack whenever it is empty", Array(
      "A Stack whenever it is empty certainly ought to be empty", 
      "A Stack whenever it is empty certainly ought to complain on peek", 
      "A Stack whenever it is empty certainly ought to complain on pop"
    ))
    
    val certainlyOughtToNode = aStackNode.children(0).children(0)
    val certainlyOughtToTest = testResolver.resolveTest(certainlyOughtToNode)
    expectTest(certainlyOughtToTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to", Array(
      "A Stack whenever it is empty certainly ought to be empty", 
      "A Stack whenever it is empty certainly ought to complain on peek", 
      "A Stack whenever it is empty certainly ought to complain on pop"
    ))
    
    val beEmptyNode = aStackNode.children(0).children(0).children(0)
    val beEmptyTest = testResolver.resolveTest(beEmptyNode)
    expectTest(beEmptyTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to be empty", Array("A Stack whenever it is empty certainly ought to be empty"))
    
    val complainOnPeekNode = aStackNode.children(0).children(0).children(1)
    val complainOnPeekTest = testResolver.resolveTest(complainOnPeekNode)
    expectTest(complainOnPeekTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to complain on peek", Array("A Stack whenever it is empty certainly ought to complain on peek"))
    
    val complainOnPopNode = aStackNode.children(0).children(0).children(2)
    val complainOnPopTest = testResolver.resolveTest(complainOnPopNode)
    expectTest(complainOnPopTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to complain on pop", Array("A Stack whenever it is empty certainly ought to complain on pop"))
    
    val butWhenFullByContrastMustNode = aStackNode.children(1)
    val butWhenFullByContrastMustTest = testResolver.resolveTest(butWhenFullByContrastMustNode)
    expectTest(butWhenFullByContrastMustTest, suiteClass.getName, "A Stack but when full, by contrast, must", Array(
      "A Stack but when full, by contrast, must be full", 
      "A Stack but when full, by contrast, must complain on push"    
    ))
    
    val beFullNode = aStackNode.children(1).children(0)
    val beFullTest = testResolver.resolveTest(beFullNode)
    expectTest(beFullTest, suiteClass.getName, "A Stack but when full, by contrast, must be full", Array("A Stack but when full, by contrast, must be full"))
    
    val complainOnPushNode = aStackNode.children(1).children(1)
    val complainOnPushTest = testResolver.resolveTest(complainOnPushNode)
    expectTest(complainOnPushTest, suiteClass.getName, "A Stack but when full, by contrast, must complain on push", Array("A Stack but when full, by contrast, must complain on push"))
  }
}