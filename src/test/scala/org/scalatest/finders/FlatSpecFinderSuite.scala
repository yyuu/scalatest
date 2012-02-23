package org.scalatest.finders
import org.scalatest.FlatSpec

class FlatSpecFinderSuite extends FinderSuite {

  test("FlatSpecFinder should find test name for tests written in test suite that extends org.scalatest.FlatSpec, using behavior of way.") {
    class TestingFlatSpec1 extends FlatSpec {
      behavior of "A Stack"

      it should "pop values in last-in-first-out order" in { 
        println("nested")
      }

      it should "throw NoSuchElementException if an empty stack is popped" in { 
        println("nested")
      }
      
      behavior of "A List"
      
      it should "put values in the sequence they are put in" in {
        
      }
      
      it should "throw ArrayIndexOutOfBoundsException when invalid index is applied" in {
        
      }
    }
    val suiteClass = classOf[TestingFlatSpec1]
    val spec1Constructor = ConstructorBlock(suiteClass.getName, Array.empty)
    
    val spec1BehaviorOf1 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "behaviour"), spec1Constructor, Array.empty, "of", StringLiteral(suiteClass.getName, null, "A Stack"))
    val spec1ItShould1 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "pop values in last-in-first-out order"))
    val spec1ItShouldIn1 = MethodInvocation(suiteClass.getName, spec1ItShould1, spec1Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val spec1Nested = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), spec1ItShouldIn1, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested"))
    val spec1ItShould2 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "throw NoSuchElementException if an empty stack is popped"))
    val spec1ItShouldIn2 = MethodInvocation(suiteClass.getName, spec1ItShould2, spec1Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    val spec1BehaviorOf2 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "behaviour"), spec1Constructor, Array.empty, "of", StringLiteral(suiteClass.getName, null, "A List"))
    val spec1ItShould3 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "put values in the sequence they are put in"))
    val spec1ItShouldIn3 = MethodInvocation(suiteClass.getName, spec1ItShould3, spec1Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val spec1ItShould4 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "throw ArrayIndexOutOfBoundsException when invalid index is applied"))
    val spec1ItShouldIn4 = MethodInvocation(suiteClass.getName, spec1ItShould4, spec1Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    List[AstNode](spec1Constructor, spec1BehaviorOf1, spec1ItShould1, spec1ItShouldIn1, spec1ItShould2, spec1ItShouldIn2, 
                  spec1BehaviorOf2, spec1ItShould3, spec1ItShouldIn3, spec1ItShould4, spec1ItShouldIn4).foreach(_.parent)
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FlatSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FlatSpecFinder], "Suite that uses org.scalatest.FlatSpec should use FlatSpecFinder.")
    
    val spec1ConstructorSelection = finder.find(spec1Constructor)
    expectSelection(spec1ConstructorSelection, suiteClass.getName, suiteClass.getName, Array("A Stack should pop values in last-in-first-out order", "A Stack should throw NoSuchElementException if an empty stack is popped", "A List should put values in the sequence they are put in", "A List should throw ArrayIndexOutOfBoundsException when invalid index is applied"))
    
    val spec1BehaviorOf1Selection = finder.find(spec1BehaviorOf1)
    expectSelection(spec1BehaviorOf1Selection, suiteClass.getName, "A Stack", Array("A Stack should pop values in last-in-first-out order", "A Stack should throw NoSuchElementException if an empty stack is popped"))
    
    val spec1ItShouldIn1Selection = finder.find(spec1ItShouldIn1)
    expectSelection(spec1ItShouldIn1Selection, suiteClass.getName, "A Stack should pop values in last-in-first-out order", Array("A Stack should pop values in last-in-first-out order"))
    
    val spec1NestedSelection = finder.find(spec1Nested)
    expectSelection(spec1NestedSelection, suiteClass.getName, "A Stack should pop values in last-in-first-out order", Array("A Stack should pop values in last-in-first-out order"))
    
    val spec1ItShouldIn2Selection = finder.find(spec1ItShouldIn2)
    expectSelection(spec1ItShouldIn2Selection, suiteClass.getName, "A Stack should throw NoSuchElementException if an empty stack is popped", Array("A Stack should throw NoSuchElementException if an empty stack is popped"))
    
    val spec1BehaviorOf2Selection = finder.find(spec1BehaviorOf2)
    expectSelection(spec1BehaviorOf2Selection, suiteClass.getName, "A List", Array("A List should put values in the sequence they are put in", "A List should throw ArrayIndexOutOfBoundsException when invalid index is applied"))
    
    val spec1ItShouldIn3Selection = finder.find(spec1ItShouldIn3)
    expectSelection(spec1ItShouldIn3Selection, suiteClass.getName, "A List should put values in the sequence they are put in", Array("A List should put values in the sequence they are put in"))
    
    val spec1ItShouldIn4Selection = finder.find(spec1ItShouldIn4)
    expectSelection(spec1ItShouldIn4Selection, suiteClass.getName, "A List should throw ArrayIndexOutOfBoundsException when invalid index is applied", Array("A List should throw ArrayIndexOutOfBoundsException when invalid index is applied"))
  }
  
  test("FlatSpecFinder should find test name for tests written in test suite that extends org.scalatest.FlatSpec, using short-hand of way.") {
    class TestingFlatSpec2 extends FlatSpec {
      "A Stack" should "pop values in last-in-first-out order" in { 
        println("nested")
      }

      it should "throw NoSuchElementException if an empty stack is popped" in { 
        
      }
      
      "A List" should "put values in the sequence they are put in" in {
        
      }
      
      it should "throw ArrayIndexOutOfBoundsException when invalid index is applied" in {
        
      }
    }
    
    val suiteClass = classOf[TestingFlatSpec2]
    val spec2Constructor = ConstructorBlock(suiteClass.getName, Array.empty)
    
    val spec2ItShould1 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "A Stack"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "pop values in last-in-first-out order"))
    val spec2ItShouldIn1 = MethodInvocation(suiteClass.getName, spec2ItShould1, spec2Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val spec2Nested = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), spec2ItShouldIn1, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested"))
    val spec2ItShould2 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "throw NoSuchElementException if an empty stack is popped"))
    val spec2ItShouldIn2 = MethodInvocation(suiteClass.getName, spec2ItShould2, spec2Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    val spec2ItShould3 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "A List"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "put values in the sequence they are put in"))
    val spec2ItShouldIn3 = MethodInvocation(suiteClass.getName, spec2ItShould3, spec2Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val spec2ItShould4 = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), null, Array.empty, "should", StringLiteral(suiteClass.getName, null, "throw ArrayIndexOutOfBoundsException when invalid index is applied"))
    val spec2ItShouldIn4 = MethodInvocation(suiteClass.getName, spec2ItShould4, spec2Constructor, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    List[AstNode](spec2Constructor, spec2ItShould1, spec2ItShouldIn1, spec2ItShould2, spec2ItShouldIn2, 
                  spec2ItShould3, spec2ItShouldIn3, spec2ItShould4, spec2ItShouldIn4).foreach(_.parent)
                  
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FlatSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FlatSpecFinder], "Suite that uses org.scalatest.FlatSpec should use FlatSpecFinder.")
                  
    val spec2ConstructorSelection = finder.find(spec2Constructor)
    expectSelection(spec2ConstructorSelection, suiteClass.getName, suiteClass.getName, Array("A Stack should pop values in last-in-first-out order", "A Stack should throw NoSuchElementException if an empty stack is popped", "A List should put values in the sequence they are put in", "A List should throw ArrayIndexOutOfBoundsException when invalid index is applied"))
    
    val spec2ItShouldIn1Selection = finder.find(spec2ItShouldIn1)
    expectSelection(spec2ItShouldIn1Selection, suiteClass.getName, "A Stack should pop values in last-in-first-out order", Array("A Stack should pop values in last-in-first-out order"))
    
    val spec2NestedSelection = finder.find(spec2Nested)
    expectSelection(spec2NestedSelection, suiteClass.getName, "A Stack should pop values in last-in-first-out order", Array("A Stack should pop values in last-in-first-out order"))
    
    val spec2ItShouldIn2Selection = finder.find(spec2ItShouldIn2)
    expectSelection(spec2ItShouldIn2Selection, suiteClass.getName, "A Stack should throw NoSuchElementException if an empty stack is popped", Array("A Stack should throw NoSuchElementException if an empty stack is popped"))
    
    val spec2ItShouldIn3Selection = finder.find(spec2ItShouldIn3)
    expectSelection(spec2ItShouldIn3Selection, suiteClass.getName, "A List should put values in the sequence they are put in", Array("A List should put values in the sequence they are put in"))
    
    val spec2ItShouldIn4Selection = finder.find(spec2ItShouldIn4)
    expectSelection(spec2ItShouldIn4Selection, suiteClass.getName, "A List should throw ArrayIndexOutOfBoundsException when invalid index is applied", Array("A List should throw ArrayIndexOutOfBoundsException when invalid index is applied"))
  }
  
}