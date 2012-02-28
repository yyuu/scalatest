package org.scalatest.finders
import org.scalatest.Spec

class FunSpecFinderSuite extends FinderSuite {
  
  test("FunSpecFinder should find test name for tests written in test suite that extends org.scalatest.Spec") {
    
    class TestingSpec extends Spec {
      describe("A Stack") {
        describe("whenever it is empty") {
          println("nested -")
          describe("certainly ought to") {
            it("be empty") {
        
            }
            it("complain on peek") {
              println("in nested")
            }
            it("complain on pop") {
          
            }
          }
        }
        describe("but when full, by contrast, must") {
          it("be full") {
            
          }
          it("complain on push") {
          
          }
        }
      }
    }
    
    val suiteClass = classOf[TestingSpec]
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.Spec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FunSpecFinder], "Suite that uses org.scalatest.Spec should use FunSpecFinder.")
    
    val aStackNode: MethodInvocation = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{this}"), null, Array.empty, "describe", StringLiteral(suiteClass.getName, null, "A Stack"))
    val wheneverItIsEmpty = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{this}"), aStackNode, Array.empty, "describe", StringLiteral(suiteClass.getName, null, "whenever it is empty")) 
    val nestedDash = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), wheneverItIsEmpty, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested in"))
    val certainlyOughtTo = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{this}"), wheneverItIsEmpty, Array.empty, "describe", StringLiteral(suiteClass.getName, null, "certainly ought to"))
    val beEmpty = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), certainlyOughtTo, Array.empty, "apply", StringLiteral(suiteClass.getName, null, "be empty"))
    val complainOnPeek = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), certainlyOughtTo, Array.empty, "apply", StringLiteral(suiteClass.getName, null, "complain on peek")) 
    val inNested = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), complainOnPeek, Array.empty, "println", StringLiteral(suiteClass.getName, null, "in nested"))
    val complainOnPop = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), certainlyOughtTo, Array.empty, "apply", StringLiteral(suiteClass.getName, null, "complain on pop"))
    val butWhenFullByContrastMust = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{this}"), aStackNode, Array.empty, "describe", StringLiteral(suiteClass.getName, null, "but when full, by contrast, must"))
    val beFull = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), butWhenFullByContrastMust, Array.empty, "apply", StringLiteral(suiteClass.getName, null, "be full"))
    val complainOnPush = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "it"), butWhenFullByContrastMust, Array(), "apply", StringLiteral(suiteClass.getName, null, "complain on push"))
    
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
    
    val nestedDashTest = finder.find(nestedDash)
    expectSelection(nestedDashTest, suiteClass.getName, "A Stack whenever it is empty", Array(
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
    
    val inNestedTest = finder.find(inNested)
    expectSelection(inNestedTest, suiteClass.getName, "A Stack whenever it is empty certainly ought to complain on peek", Array("A Stack whenever it is empty certainly ought to complain on peek"))
    
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

}