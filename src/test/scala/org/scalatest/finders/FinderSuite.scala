package org.scalatest.finders

import org.scalatest.FunSuite
import org.scalatest.Suite
import org.scalatest.Style
import org.scalatest.FeatureSpec
import org.scalatest.fixture.FixtureSuite
import org.scalatest.FreeSpec
import org.scalatest.FlatSpec

class FinderSuite extends FunSuite {
  
  def expectSelection(selectionOpt: Option[Selection], expectedClassName: String, expectedDisplayName: String, expectedTestNames: Array[String]) {
    assert(selectionOpt.getClass == classOf[Some[_]], "Test is None, expected className=" + expectedClassName + ", displayName=" + expectedDisplayName + ", testNames=" + expectedTestNames.deepToString)
    val selection = selectionOpt.get
    expect(expectedClassName)(selection.className)
    expect(expectedDisplayName)(selection.displayName)
    expect(expectedTestNames.deepToString)(selection.testNames.deepToString)
  }

  test("MethodFinder should find test name for tests written in test suite that extends org.scalatest.Suite") {
    class TestingSuite extends Suite {
      def testMethod1(aParam: String) {
        
      }
      def testMethod2() {
        
      }
      def testMethod3() {
        def testNested() {
          
        }
      }
    }
    
    val suiteClass = classOf[TestingSuite]
    val suiteConstructor = new ConstructorBlock(suiteClass.getName, Array.empty)
    val testMethod1 = MethodDefinition(suiteClass.getName, suiteConstructor, Array.empty, "testMethod1", "java.lang.String")
    val testMethod2 = MethodDefinition(suiteClass.getName, suiteConstructor, Array.empty, "testMethod2")
    val testMethod3 = MethodDefinition(suiteClass.getName, suiteConstructor, Array.empty, "testMethod3")
    val testNested = MethodDefinition(suiteClass.getName, testMethod3, Array.empty, "testNested")
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.Suite.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[MethodFinder], "Suite that uses org.scalatest.Suite should use MethodFinder.")
    val selectionMethod1 = finder.find(testMethod1)
    expect(None)(selectionMethod1)
    val selectionMethod2 = finder.find(testMethod2)
    expectSelection(selectionMethod2, suiteClass.getName, suiteClass.getName + ".testMethod2", Array("testMethod2"))
    val selectionMethod3 = finder.find(testMethod3)
    expectSelection(selectionMethod3, suiteClass.getName, suiteClass.getName + ".testMethod3", Array("testMethod3"))
    val selectionNested = finder.find(testNested)
    expectSelection(selectionNested, suiteClass.getName, suiteClass.getName + ".testMethod3", Array("testMethod3"))
  }
  
  test("FunctionFinder should find test name for tests written in test suite that extends org.scalatest.FunSuite") {
    class TestingFunSuite extends FunSuite {
      test("test 1") {
        
      }
      test("test 2") {
        test("nested") {
          
        }
      }
      test("test 3") {
        
      }
    }
    
    val suiteClass = classOf[TestingFunSuite]
    val suiteConstructor = ConstructorBlock(suiteClass.getName, Array.empty)
    val test1 = MethodInvocation(suiteClass.getName, null, suiteConstructor, Array.empty, "test", StringLiteral(suiteClass.getName, null, "test 1"))
    val test2 = MethodInvocation(suiteClass.getName, null, suiteConstructor, Array.empty, "test", StringLiteral(suiteClass.getName, null, "test 2"))
    val nested = MethodInvocation(suiteClass.getName, null, test2, Array.empty, "test", StringLiteral(suiteClass.getName, null, "nested"))
    val test3 = MethodInvocation(suiteClass.getName, null, suiteConstructor, Array.empty, "test", StringLiteral(suiteClass.getName, null, "test 3"))
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FunSuite.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FunctionFinder], "Suite that uses org.scalatest.FunSuite should use FunctionFinder.")
    val test1Selection = finder.find(test1)
    expectSelection(test1Selection, suiteClass.getName, suiteClass.getName + ": \"test 1\"", Array("test 1"))
    val test2Selection = finder.find(test2)
    expectSelection(test2Selection, suiteClass.getName, suiteClass.getName + ": \"test 2\"", Array("test 2"))
    val nestedSelection = finder.find(nested)
    expectSelection(nestedSelection, suiteClass.getName, suiteClass.getName + ": \"test 2\"", Array("test 2"))
    val test3Selection = finder.find(test3)
    expectSelection(test3Selection, suiteClass.getName, suiteClass.getName + ": \"test 3\"", Array("test 3"))
  }
  
  test("FeatureSpecFinder should find test name for tests written in test suite that extends org.scalatest.FeatureSpec") {
    class TestingFeatureSpec extends FeatureSpec {
      feature("feature 1") {
        scenario("scenario 1") {
          
        }
        scenario("scenario 2") {
          scenario("nested") {
            
          }
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
    val featureSpecConstructor = ConstructorBlock(suiteClass.getName, Array.empty)
    val feature1: MethodInvocation = MethodInvocation(suiteClass.getName, null, featureSpecConstructor, Array(), "feature", StringLiteral(suiteClass.getName, null, "feature 1"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val feature1Scenario1 = MethodInvocation(suiteClass.getName, null, feature1, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 1"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val feature1Scenario2 = MethodInvocation(suiteClass.getName, null, feature1, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 2"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val nestedScenario = MethodInvocation(suiteClass.getName, null, feature1Scenario2, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "nested"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    val feature2: MethodInvocation = MethodInvocation(suiteClass.getName, null, featureSpecConstructor, Array.empty, "feature", StringLiteral(suiteClass.getName, null, "feature 2"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val feature2Scenario1 = MethodInvocation(suiteClass.getName, null, feature2, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 1"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val feature2Scenario2 = MethodInvocation(suiteClass.getName, null, feature2, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 2"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FeatureSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FeatureSpecFinder], "Suite that uses org.scalatest.FeatureSpec should use FeatureSpecFinder.")
    
    val f1s1 = finder.find(feature1Scenario1)                      
    expectSelection(f1s1, suiteClass.getName, "feature 1 scenario 1", Array("feature 1 scenario 1"))
    val f1s2 = finder.find(feature1Scenario2)
    expectSelection(f1s2, suiteClass.getName, "feature 1 scenario 2", Array("feature 1 scenario 2"))
    val ns = finder.find(nestedScenario)
    expectSelection(ns, suiteClass.getName, "feature 1 scenario 2", Array("feature 1 scenario 2"))
    
    val f2s1 = finder.find(feature2Scenario1)
    expectSelection(f2s1, suiteClass.getName, "feature 2 scenario 1", Array("feature 2 scenario 1"))
    val f2s2 = finder.find(feature2Scenario2)
    expectSelection(f2s2, suiteClass.getName, "feature 2 scenario 2", Array("feature 2 scenario 2"))
    
    val f1 = finder.find(feature1)
    expectSelection(f1, suiteClass.getName, "feature 1", Array("feature 1 scenario 1", "feature 1 scenario 2"))
    
    val f2 = finder.find(feature2)
    expectSelection(f2, suiteClass.getName, "feature 2", Array("feature 2 scenario 1", "feature 2 scenario 2"))
  }
  
  test("FreeSpecFinder should find test name for tests written in test suite that extends org.scalatest.FreeSpec") {
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
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FreeSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FreeSpecFinder], "Suite that uses org.scalatest.FreeSpec should use FreeSpecFinder.")
    
    val aStackNode: MethodInvocation = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "A Stack"), null, Array.empty, "-", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val wheneverItIsEmpty = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "whenever it is empty"), aStackNode, Array.empty, "-", ToStringTarget(suiteClass.getName, null, Array.empty, "{}")) 
    val certainlyOughtTo = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "certainly ought to"), wheneverItIsEmpty, Array.empty, "-", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val beEmpty = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "be empty"), certainlyOughtTo, Array(), "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val complainOnPeek = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on peek"), certainlyOughtTo, Array(), "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}")) 
    val complainOnPop = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on pop"), certainlyOughtTo, Array(), "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val butWhenFullByContrastMust = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "but when full, by contrast, must"), aStackNode, Array.empty, "-", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val beFull = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "be full"), butWhenFullByContrastMust, Array(), "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val complainOnPush = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on push"), butWhenFullByContrastMust, Array(), "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    List[AstNode](aStackNode, wheneverItIsEmpty, certainlyOughtTo, beEmpty, complainOnPeek, complainOnPop, butWhenFullByContrastMust, 
        beFull, complainOnPush).foreach(_.parent)
    
    val aStackTest = finder.find(aStackNode)
    expectSelection(aStackTest, suiteClass.getName, "A Stack", Array(
      "A Stack whenever it is empty certainly ought to be empty", 
      "A Stack whenever it is empty certainly ought to complain on peek", 
      "A Stack whenever it is empty certainly ought to complain on pop", 
      "A Stack but when full, by contrast, must be full", 
      "A Stack but when full, by contrast, must complain on push"
    ))
    
    val wheneverItIsEmptyTest = finder.find(wheneverItIsEmpty)
    expectSelection(wheneverItIsEmptyTest, suiteClass.getName, "A Stack whenever it is empty", Array(
      "A Stack whenever it is empty certainly ought to be empty", 
      "A Stack whenever it is empty certainly ought to complain on peek", 
      "A Stack whenever it is empty certainly ought to complain on pop"
    ))
    
    val certainlyOughtToTest = finder.find(certainlyOughtTo)
    expectSelection(certainlyOughtToTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to", Array(
      "A Stack whenever it is empty certainly ought to be empty", 
      "A Stack whenever it is empty certainly ought to complain on peek", 
      "A Stack whenever it is empty certainly ought to complain on pop"
    ))
    
    val beEmptyTest = finder.find(beEmpty)
    expectSelection(beEmptyTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to be empty", Array("A Stack whenever it is empty certainly ought to be empty"))
    
    val complainOnPeekTest = finder.find(complainOnPeek)
    expectSelection(complainOnPeekTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to complain on peek", Array("A Stack whenever it is empty certainly ought to complain on peek"))
    
    val complainOnPopTest = finder.find(complainOnPop)
    expectSelection(complainOnPopTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to complain on pop", Array("A Stack whenever it is empty certainly ought to complain on pop"))
    
    val butWhenFullByContrastMustTest = finder.find(butWhenFullByContrastMust)
    expectSelection(butWhenFullByContrastMustTest, suiteClass.getName, "A Stack but when full, by contrast, must", Array(
      "A Stack but when full, by contrast, must be full", 
      "A Stack but when full, by contrast, must complain on push"    
    ))
    
    val beFullTest = finder.find(beFull)
    expectSelection(beFullTest, suiteClass.getName, "A Stack but when full, by contrast, must be full", Array("A Stack but when full, by contrast, must be full"))
    
    val complainOnPushTest = finder.find(complainOnPush)
    expectSelection(complainOnPushTest, suiteClass.getName, "A Stack but when full, by contrast, must complain on push", Array("A Stack but when full, by contrast, must complain on push"))
  }
  
  test("FlatSpecFinder should find test name for tests written in test suite that extends org.scalatest.FlatSpec, using behavior of way.") {
    class TestingFlatSpec1 extends FlatSpec {
      behavior of "A Stack"

      it should "pop values in last-in-first-out order" in { 
        
      }

      it should "throw NoSuchElementException if an empty stack is popped" in { 
        
      }
    }
    val suiteClass = classOf[TestingFlatSpec1]
    val spec1Constructor = ConstructorBlock(suiteClass.getName, Array.empty)
    val spec1BehaviourOf = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "behaviour"), spec1Constructor, Array.empty, "of", StringLiteral(suiteClass.getName, null, "A Stack"))
    val spec1ItShould1 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "pop values in last-in-first-out order"))
    val spec1ItShouldIn1 = MethodInvocation(suiteClass.getName, spec1ItShould1, spec1Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val spec1ItShould2 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "throw NoSuchElementException if an empty stack is popped"))
    val spec1ItShouldIn2 = MethodInvocation(suiteClass.getName, spec1ItShould2, spec1Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    List[AstNode](spec1Constructor, spec1BehaviourOf, spec1ItShould1, spec1ItShouldIn1, spec1ItShould2, spec1ItShouldIn2).foreach(_.parent)
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FlatSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FlatSpecFinder], "Suite that uses org.scalatest.FlatSpec should use FlatSpecFinder.")
    
    val spec1ConstructorSelection = finder.find(spec1Constructor)
    expectSelection(spec1ConstructorSelection, suiteClass.getName, "A Stack", Array("A Stack should pop values in last-in-first-out order", "A Stack should throw NoSuchElementException if an empty stack is popped"))
    
    val spec1BehaviourOfSelection = finder.find(spec1BehaviourOf)
    expectSelection(spec1BehaviourOfSelection, suiteClass.getName, "A Stack", Array("A Stack should pop values in last-in-first-out order", "A Stack should throw NoSuchElementException if an empty stack is popped"))
    
    val spec1ItShouldIn1Selection = finder.find(spec1ItShouldIn1)
    expectSelection(spec1ItShouldIn1Selection, suiteClass.getName, "A Stack should pop values in last-in-first-out order", Array("A Stack should pop values in last-in-first-out order"))
    
    val spec1ItShouldIn2Selection = finder.find(spec1ItShouldIn2)
    expectSelection(spec1ItShouldIn2Selection, suiteClass.getName, "A Stack should throw NoSuchElementException if an empty stack is popped", Array("A Stack should throw NoSuchElementException if an empty stack is popped"))
  }
  
  test("FlatSpecFinder should find test name for tests written in test suite that extends org.scalatest.FlatSpec, using short-hand of way.") {
    class TestingFlatSpec2 extends FlatSpec {
      "A Stack" should "pop values in last-in-first-out order" in { 
        
      }

      it should "throw NoSuchElementException if an empty stack is popped" in { 
        
      }
    }
    
    val suiteClass = classOf[TestingFlatSpec2]
    val spec2Constructor = ConstructorBlock(suiteClass.getName, Array.empty)
    val spec2ItShould1 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "A Stack"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "pop values in last-in-first-out order"))
    val spec2ItShouldIn1 = MethodInvocation(suiteClass.getName, spec2ItShould1, spec2Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val spec2ItShould2 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "throw NoSuchElementException if an empty stack is popped"))
    val spec2ItShouldIn2 = MethodInvocation(suiteClass.getName, spec2ItShould2, spec2Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    List[AstNode](spec2Constructor, spec2ItShould1, spec2ItShouldIn1, spec2ItShould2, spec2ItShouldIn2).foreach(_.parent)
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FlatSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FlatSpecFinder], "Suite that uses org.scalatest.FlatSpec should use FlatSpecFinder.")
    
    val spec2ConstructorSelection = finder.find(spec2Constructor)
    expectSelection(spec2ConstructorSelection, suiteClass.getName, "A Stack", Array("A Stack should pop values in last-in-first-out order", "A Stack should throw NoSuchElementException if an empty stack is popped"))
    
    val spec2ItShouldIn1Selection = finder.find(spec2ItShouldIn1)
    expectSelection(spec2ItShouldIn1Selection, suiteClass.getName, "A Stack should pop values in last-in-first-out order", Array("A Stack should pop values in last-in-first-out order"))
    
    val spec2ItShouldIn2Selection = finder.find(spec2ItShouldIn2)
    expectSelection(spec2ItShouldIn2Selection, suiteClass.getName, "A Stack should throw NoSuchElementException if an empty stack is popped", Array("A Stack should throw NoSuchElementException if an empty stack is popped"))
  }
}