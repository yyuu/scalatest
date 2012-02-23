package org.scalatest.finders

import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec

class FreeSpecFinder extends Finder {
  
  private def getTestNameBottomUp(invocation: MethodInvocation): String = {
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
            if (invocation.name == "in" || invocation.name == "is") {
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
          case "in" | "is" if parent.name == "-" =>
            val testName = getTestNameBottomUp(invocation)
            Some(new Selection(className, testName, Array(testName)))
          case "-" =>
            val displayName = getTestNameBottomUp(invocation)
            val testNames = getTestNamesTopDown(invocation)
            Some(new Selection(className, displayName, testNames.toArray))
          case _ => 
            if (node.parent != null)
              find(node.parent)
            else
              None
        }
      case _ => 
        if (node.parent != null)
          find(node.parent)
        else
          None
    }
  }
}