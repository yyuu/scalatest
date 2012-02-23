package org.scalatest.finders
import org.scalatest.FreeSpec

class FreeSpecFinderSuite extends FinderSuite {
  
  test("FreeSpecFinder should find test name for tests written in test suite that extends org.scalatest.FreeSpec") {
    class TestingFreeSpec extends FreeSpec {
      "A Stack" - {
        "whenever it is empty" - {
          println("nested -")
          "certainly ought to" - {
            "be empty" in {
        
            }
            "complain on peek" in {
              println("in nested")
            }
            "complain on pop" in {
          
            }
          }
        }
        "but when full, by contrast, must" - {
          "be full" in {
            "in nested" in {
              
            }
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
    val nestedDash = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), wheneverItIsEmpty, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested in"))
    val certainlyOughtTo = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "certainly ought to"), wheneverItIsEmpty, Array.empty, "-", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val beEmpty = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "be empty"), certainlyOughtTo, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val complainOnPeek = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on peek"), certainlyOughtTo, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}")) 
    val inNested = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "{Predef}"), complainOnPeek, Array.empty, "println", StringLiteral(suiteClass.getName, null, "in nested"))
    val complainOnPop = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "complain on pop"), certainlyOughtTo, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val butWhenFullByContrastMust = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "but when full, by contrast, must"), aStackNode, Array.empty, "-", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val beFull = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "be full"), butWhenFullByContrastMust, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val nestedIn = MethodInvocation(suiteClass.getName, ToStringTarget(suiteClass.getName, null, Array.empty, "in nested"), beFull, Array.empty, "in", ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
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
    
    val nestedInTest = finder.find(nestedIn)
    expectSelection(nestedInTest, suiteClass.getName, "A Stack but when full, by contrast, must be full", Array("A Stack but when full, by contrast, must be full"))
    
    val complainOnPushTest = finder.find(complainOnPush)
    expectSelection(complainOnPushTest, suiteClass.getName, "A Stack but when full, by contrast, must complain on push", Array("A Stack but when full, by contrast, must complain on push"))
  }
  
}