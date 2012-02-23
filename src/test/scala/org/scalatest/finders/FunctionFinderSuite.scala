package org.scalatest.finders
import org.scalatest.FunSuite
import org.scalatest.PropSpec

class FunctionFinderSuite extends FinderSuite {
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
    assert(finder.getClass == classOf[FunSuiteFinder], "Suite that uses org.scalatest.FunSuite should use FunSuiteFinder.")
    val test1Selection = finder.find(test1)
    expectSelection(test1Selection, suiteClass.getName, suiteClass.getName + ": \"test 1\"", Array("test 1"))
    val test2Selection = finder.find(test2)
    expectSelection(test2Selection, suiteClass.getName, suiteClass.getName + ": \"test 2\"", Array("test 2"))
    val nestedSelection = finder.find(nested)
    expectSelection(nestedSelection, suiteClass.getName, suiteClass.getName + ": \"test 2\"", Array("test 2"))
    val test3Selection = finder.find(test3)
    expectSelection(test3Selection, suiteClass.getName, suiteClass.getName + ": \"test 3\"", Array("test 3"))
  }
  
  test("FunctionFinder should find test name for tests written in test suite that extends org.scalatest.PropSpec") {
    class TestingPropSpec extends PropSpec {
      property("Fraction constructor normalizes numerator and denominator.") {
        
      }
      
      property("Fraction constructor throws IAE on bad data.") {
        println("nested")
      }
    }
    
    val suiteClass = classOf[TestingPropSpec]
    val suiteConstructor = ConstructorBlock(suiteClass.getName, Array.empty)
    val prop1 = MethodInvocation(suiteClass.getName, null, suiteConstructor, Array.empty, "property", StringLiteral(suiteClass.getName, null, "Fraction constructor normalizes numerator and denominator."))
    val prop2 = MethodInvocation(suiteClass.getName, null, suiteConstructor, Array.empty, "property", StringLiteral(suiteClass.getName, null, "Fraction constructor throws IAE on bad data."))
    val nested = MethodInvocation(suiteClass.getName, null, prop2, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested"))
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.PropSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[PropSpecFinder], "Suite that uses org.scalatest.PropSpec should use PropSpecFinder.")
    val prop1Selection = finder.find(prop1)
    expectSelection(prop1Selection, suiteClass.getName, suiteClass.getName + ": \"Fraction constructor normalizes numerator and denominator.\"", Array("Fraction constructor normalizes numerator and denominator."))
    val prop2Selection = finder.find(prop2)
    expectSelection(prop2Selection, suiteClass.getName, suiteClass.getName + ": \"Fraction constructor throws IAE on bad data.\"", Array("Fraction constructor throws IAE on bad data."))
    val nestedSelection = finder.find(nested)
    expectSelection(nestedSelection, suiteClass.getName, suiteClass.getName + ": \"Fraction constructor throws IAE on bad data.\"", Array("Fraction constructor throws IAE on bad data."))
  }
}