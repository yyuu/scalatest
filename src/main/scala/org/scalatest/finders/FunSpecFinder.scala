package org.scalatest.finders

import scala.annotation.tailrec

class FunSpecFinder extends Finder {
  
  private def getTestNameBottomUp(invocation: MethodInvocation): String = {
    if(invocation.parent == null || !invocation.parent.isInstanceOf[MethodInvocation])
      invocation.args(0).toString
    else
      getTestNameBottomUp(invocation.parent.asInstanceOf[MethodInvocation]) + " " + invocation.args(0).toString  
  }
  
  private def getTestNamesTopDown(invocation: MethodInvocation): List[String] = {
    @tailrec
    def getTestNamesTopDownAcc(invocations: List[AstNode], acc: List[String]): List[String] = invocations match {
      case Nil => acc
      case node :: rs => 
        node match {
          case invocation: MethodInvocation => 
            if (isDescribeOrIt("it", invocation)) {
              val testName = getTestNameBottomUp(invocation)
              getTestNamesTopDownAcc(rs, testName :: acc)
            }
            else if(isDescribeOrIt("describe", invocation)) {
              getTestNamesTopDownAcc(invocation.children.toList ::: rs, acc)
            }
            else
              getTestNamesTopDownAcc(rs, acc)
          case _ => getTestNamesTopDownAcc(rs, acc)
        }
    }
    getTestNamesTopDownAcc(List(invocation), List.empty).reverse
  }
  
  private def isDescribeOrIt(name: String, astNode: AstNode): Boolean = {
    if (name == astNode.name) {
      astNode match {
        case MethodInvocation(_, _, _, _, _, args) => 
          args.length > 0 && args(0).isInstanceOf[StringLiteral]
        case _ => false
      }
    }
    else
      false
  }
  
  def find(node: AstNode): Option[Selection] = {
    node match {
      case invocation @ MethodInvocation(className, target, parent, children, name, args) =>
        name match {
          case "it" if isDescribeOrIt("describe", parent) =>
            val testName = getTestNameBottomUp(invocation)
            Some(new Selection(className, testName, Array(testName)))
          case "describe" if isDescribeOrIt("describe", node) =>
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