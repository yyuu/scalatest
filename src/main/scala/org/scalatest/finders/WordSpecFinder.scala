package org.scalatest.finders
import scala.annotation.tailrec

class WordSpecFinder extends Finder {
  
  val scopeSet = Set("should", "must", "can", "which", "when", "that")  // note 'that' is deprecated
  
  private def getTestNameBottomUp(invocation: MethodInvocation): String = {
    val targetText = 
      if (invocation.name == "in")
        invocation.target.toString
      else
        invocation.target.toString + " " + invocation.name
    if(invocation.parent == null || !invocation.parent.isInstanceOf[MethodInvocation])
      targetText
    else
      getTestNameBottomUp(invocation.parent.asInstanceOf[MethodInvocation]) + " " + targetText  
  }
  
  private def getDisplayNameBottomUp(invocation: MethodInvocation): String = {
    if(invocation.parent == null || !invocation.parent.isInstanceOf[MethodInvocation])
      invocation.target.toString
    else
      getTestNameBottomUp(invocation.parent.asInstanceOf[MethodInvocation]) + " " + invocation.target.toString 
  }
  
  private def getTestNamesTopDown(invocation: MethodInvocation): List[String] = {
    @tailrec
    def getTestNamesTopDownAcc(invocations: List[AstNode], acc: List[String]): List[String] = invocations match {
      case Nil => acc
      case node :: rs => 
        node match {
          case invocation: MethodInvocation => 
            if (invocation.name == "in") {
              val testName = getTestNameBottomUp(invocation)
              getTestNamesTopDownAcc(rs, testName :: acc)
            }
            else {
              getTestNamesTopDownAcc(invocation.children.toList ::: rs, acc)
            }
          case _ => getTestNamesTopDownAcc(rs, acc)
        }
    }
    getTestNamesTopDownAcc(List(invocation), List.empty).reverse
  }
  
  def find(node: AstNode): Option[Selection] = {
    node match {
      case invocation @ MethodInvocation(className, target, parent, children, name, args) =>
        name match {
          case "in" if scopeSet.contains(parent.name) =>
            val testName = getTestNameBottomUp(invocation)
            Some(new Selection(className, testName, Array(testName)))  
          case _ => 
            if (scopeSet.contains(name)) {
              val displayName = getDisplayNameBottomUp(invocation)
              val testNames = getTestNamesTopDown(invocation)
              Some(new Selection(className, displayName, testNames.toArray))
            }
            else {
              if (node.parent != null)
                find(node.parent)
              else
                None
            }
        }
      case _ => 
        if (node.parent != null)
          find(node.parent)
        else
          None
    }
  }
}