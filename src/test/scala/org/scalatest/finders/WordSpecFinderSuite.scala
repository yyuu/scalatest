package org.scalatest.finders
import org.scalatest.WordSpec

class WordSpecFinderSuite extends FinderSuite {

  test("FeatureSpecFinder should find test name for tests written in test suite that extends org.scalatest.FeatureSpec, using should and in") {
    
    class TestingWordSpec1 extends WordSpec {

      "A Stack" should {

        "pop values in last-in-first-out order" in { 
         
        }

        "throw NoSuchElementException if an empty stack is popped" in { 
          println("nested") 
        } 
      } 
    }
    
    val suiteClass = classOf[TestingWordSpec1]
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.WordSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[WordSpecFinder], "Suite that uses org.scalatest.WordSpec should use WordSpecFinder.")
    
    val constructorBlock = ConstructorBlock(suiteClass.getName, Array.empty)
    val aStack = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "A Stack"), constructorBlock, Array.empty, "should", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val popValuesInLifo = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "pop values in last-in-first-out order"), aStack, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val throwNsee = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "throw NoSuchElementException if an empty stack is popped"), aStack, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val nested = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), throwNsee, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested"))
    List[AstNode](constructorBlock, aStack, popValuesInLifo, throwNsee, nested).foreach(_.parent)
    
    val aStackSelection = finder.find(aStack)
    expectSelection(aStackSelection, suiteClass.getName, "A Stack", Array("A Stack should pop values in last-in-first-out order", "A Stack should throw NoSuchElementException if an empty stack is popped"))
    val popValuesInLifoSelection = finder.find(popValuesInLifo)
    expectSelection(popValuesInLifoSelection, suiteClass.getName, "A Stack should pop values in last-in-first-out order", Array("A Stack should pop values in last-in-first-out order"))
    val throwNseeSelection = finder.find(throwNsee)
    expectSelection(throwNseeSelection, suiteClass.getName, "A Stack should throw NoSuchElementException if an empty stack is popped", Array("A Stack should throw NoSuchElementException if an empty stack is popped"))
    val nestedSelection = finder.find(nested)
    expectSelection(nestedSelection, suiteClass.getName, "A Stack should throw NoSuchElementException if an empty stack is popped", Array("A Stack should throw NoSuchElementException if an empty stack is popped"))
  }
  
  test("FeatureSpecFinder should find test name for tests written in test suite that extends org.scalatest.FeatureSpec, using when, must, can and in") {
    class TestingWordSpec2 extends WordSpec {
      "A Stack" when {
        "empty" must {
          "be empty" in {
        
          }
          "complain on peek" in {
            println("nested")
          }
          "complain on pop" in {
        
          }
        }
        "full" can {
          "be full" in {
        
          }
          "complain on push" in {
        
          }
        }
      }
    }
    
    val suiteClass = classOf[TestingWordSpec2]
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.WordSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[WordSpecFinder], "Suite that uses org.scalatest.WordSpec should use WordSpecFinder.")
    
    val constructorBlock = ConstructorBlock(suiteClass.getName, Array.empty)
    val aStack = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "A Stack"), constructorBlock, Array.empty, "when", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val empty = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "empty"), aStack, Array.empty, "must", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val beEmpty = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "be empty"), empty, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val complainOnPeek = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on peek"), empty, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val nested = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), complainOnPeek, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested"))
    val complainOnPop = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on pop"), empty, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val full = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "full"), aStack, Array.empty, "can", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val beFull = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "be full"), full, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val complainOnPush = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on push"), full, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    List[AstNode](constructorBlock, aStack, empty, beEmpty, complainOnPeek, nested, complainOnPop, full, beFull, complainOnPush).foreach(_.parent)
    
    val aStackSelection = finder.find(aStack)
    expectSelection(aStackSelection, suiteClass.getName, "A Stack", Array("A Stack when empty must be empty", "A Stack when empty must complain on peek", "A Stack when empty must complain on pop", 
                                                                          "A Stack when full can be full", "A Stack when full can complain on push"))
    val emptySelection = finder.find(empty)
    expectSelection(emptySelection, suiteClass.getName, "A Stack when empty", Array("A Stack when empty must be empty", "A Stack when empty must complain on peek", "A Stack when empty must complain on pop"))
    val beEmptySelection = finder.find(beEmpty)
    expectSelection(beEmptySelection, suiteClass.getName, "A Stack when empty must be empty", Array("A Stack when empty must be empty"))
    val complainOnPeekSelection = finder.find(complainOnPeek)
    expectSelection(complainOnPeekSelection, suiteClass.getName, "A Stack when empty must complain on peek", Array("A Stack when empty must complain on peek"))
    val nestedSelection = finder.find(nested)
    expectSelection(nestedSelection, suiteClass.getName, "A Stack when empty must complain on peek", Array("A Stack when empty must complain on peek"))
    val complainOnPopSelection = finder.find(complainOnPop)
    expectSelection(complainOnPopSelection, suiteClass.getName, "A Stack when empty must complain on pop", Array("A Stack when empty must complain on pop"))
    val fullSelection = finder.find(full)
    expectSelection(fullSelection, suiteClass.getName, "A Stack when full", Array("A Stack when full can be full", "A Stack when full can complain on push"))
    val beFullSelection = finder.find(beFull)
    expectSelection(beFullSelection, suiteClass.getName, "A Stack when full can be full", Array("A Stack when full can be full"))
    val complainOnPushSelection = finder.find(complainOnPush)
    expectSelection(complainOnPushSelection, suiteClass.getName, "A Stack when full can complain on push", Array("A Stack when full can complain on push"))
  }
  
  test("FeatureSpecFinder should find test name for tests written in test suite that extends org.scalatest.FeatureSpec, using should, which and in") {
    class TestingWordSpec3 extends WordSpec {

      "The ScalaTest Matchers DSL" should { 
        "provide an and operator" that { 
          "returns silently when evaluating true and true" in {} 
          "throws a TestFailedException when evaluating true and false" in {} 
          "throws a TestFailedException when evaluating false and true" in {
            println("nested")
          } 
          "throws a TestFailedException when evaluating false and false" in {} 
        } 
        "provide an or operator" that { // we'll use 'which' in the DSL below.
          "returns silently when evaluating true or true" in {} 
          "returns silently when evaluating true or false" in {} 
          "returns silently when evaluating false or true" in {} 
          "throws a TestFailedException when evaluating false or false" in {} 
        } 
      } 
    }
    
    val suiteClass = classOf[TestingWordSpec3]
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.WordSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[WordSpecFinder], "Suite that uses org.scalatest.WordSpec should use WordSpecFinder.")
    
    val constructorBlock = ConstructorBlock(suiteClass.getName, Array.empty)
    val scalaTestDsl = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "The ScalaTest Matchers DSL"), constructorBlock, Array.empty, "should", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val provideAndOpr = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "provide an and operator"), scalaTestDsl, Array.empty, "that", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val andSilentTrueTrue = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "returns silently when evaluating true and true"), provideAndOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val andThrowTfeTrueFalse = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "throws a TestFailedException when evaluating true and false"), provideAndOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val andThrowTfeFalseTrue = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "throws a TestFailedException when evaluating false and true"), provideAndOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val nested = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "${Predef}"), andThrowTfeFalseTrue, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested"))
    val andThrowTfeFalseFalse = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "throws a TestFailedException when evaluating false and false"), provideAndOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val provideOrOpr = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "provide an or operator"), scalaTestDsl, Array.empty, "which", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val orSilentTrueTrue = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "returns silently when evaluating true or true"), provideOrOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val orSilentTrueFalse = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "returns silently when evaluating true or false"), provideOrOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val orSilentFalseTrue = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "returns silently when evaluating false or true"), provideOrOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val orSilentFalseFalse = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "throws a TestFailedException when evaluating false or false"), provideOrOpr, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    List[AstNode](constructorBlock, scalaTestDsl, provideAndOpr, andSilentTrueTrue, andThrowTfeTrueFalse, andThrowTfeFalseTrue, 
                  nested, andThrowTfeFalseFalse, provideOrOpr, orSilentTrueTrue, orSilentTrueFalse, orSilentFalseTrue, orSilentFalseFalse).foreach(_.parent)
                  
    val scalaTestDslSelection = finder.find(scalaTestDsl)
    expectSelection(scalaTestDslSelection, suiteClass.getName, "The ScalaTest Matchers DSL", 
                    Array("The ScalaTest Matchers DSL should provide an and operator that returns silently when evaluating true and true", 
                          "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating true and false", 
                          "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and true", 
                          "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and false", 
                          "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or true", 
                          "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or false", 
                          "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating false or true", 
                          "The ScalaTest Matchers DSL should provide an or operator which throws a TestFailedException when evaluating false or false"))
                          
    val provideAndOprSelection = finder.find(provideAndOpr)
    expectSelection(provideAndOprSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an and operator", 
                    Array("The ScalaTest Matchers DSL should provide an and operator that returns silently when evaluating true and true", 
                          "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating true and false", 
                          "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and true", 
                          "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and false"))
    
    val andSilentTrueTrueSelection = finder.find(andSilentTrueTrue)
    expectSelection(andSilentTrueTrueSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an and operator that returns silently when evaluating true and true", 
                    Array("The ScalaTest Matchers DSL should provide an and operator that returns silently when evaluating true and true"))
                    
    val andThrowTfeTrueFalseSelection = finder.find(andThrowTfeTrueFalse)
    expectSelection(andThrowTfeTrueFalseSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating true and false", 
                    Array("The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating true and false"))
                    
    val andThrowTfeFalseTrueSelection = finder.find(andThrowTfeFalseTrue)
    expectSelection(andThrowTfeFalseTrueSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and true", 
                    Array("The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and true"))
                    
    val nestedSelection = finder.find(nested)
    expectSelection(nestedSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and true", 
                    Array("The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and true"))
                    
    val andThrowTfeFalseFalseSelection = finder.find(andThrowTfeFalseFalse)
    expectSelection(andThrowTfeFalseFalseSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and false", 
                    Array("The ScalaTest Matchers DSL should provide an and operator that throws a TestFailedException when evaluating false and false"))
                    
    val provideOrOprSelection = finder.find(provideOrOpr)
    expectSelection(provideOrOprSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an or operator", 
                    Array("The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or true", 
                          "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or false", 
                          "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating false or true", 
                          "The ScalaTest Matchers DSL should provide an or operator which throws a TestFailedException when evaluating false or false"))
                          
    val orSilentTrueTrueSelection = finder.find(orSilentTrueTrue)
    expectSelection(orSilentTrueTrueSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or true", 
                    Array("The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or true"))
                    
    val orSilentTrueFalseSelection = finder.find(orSilentTrueFalse)
    expectSelection(orSilentTrueFalseSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or false", 
                    Array("The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating true or false"))
                    
    val orSilentFalseTrueSelection = finder.find(orSilentFalseTrue)
    expectSelection(orSilentFalseTrueSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating false or true", 
                    Array("The ScalaTest Matchers DSL should provide an or operator which returns silently when evaluating false or true"))
                    
    val orSilentFalseFalseSelection = finder.find(orSilentFalseFalse)
    expectSelection(orSilentFalseFalseSelection, suiteClass.getName, "The ScalaTest Matchers DSL should provide an or operator which throws a TestFailedException when evaluating false or false", 
                    Array("The ScalaTest Matchers DSL should provide an or operator which throws a TestFailedException when evaluating false or false"))
  }
  
}