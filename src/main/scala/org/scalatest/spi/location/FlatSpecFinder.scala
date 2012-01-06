package org.scalatest.spi.location

import LocationUtils._
import scala.annotation.tailrec

class FlatSpecFinder extends Finder {

  def find(node: AstNode): Option[Selection] = {
    val constructorOpt: Option[ConstructorBlock] = node match {
      case constructor: ConstructorBlock => Some(constructor)
      case _ => 
        getParentOfType(node, classOf[ConstructorBlock])
    }
    constructorOpt match {
      case Some(constructor) =>
        // Get the first method invocation, which could be behaviour of or "xxxx" should method invocation.
        val branchNodeOpt = constructor.children.find { node => 
          node match {
            case invocation: MethodInvocation => 
              isSingleStringParamInvocationWithName(invocation, Set("of", "should"))
            case _ => false
          }
        }
        // Get the prefix from the branch, which should be from the first method invocation
        val prefix = branchNodeOpt match {
          case Some(branchNode) => 
            val branchInvocation = branchNode.asInstanceOf[MethodInvocation]
            if (branchInvocation.name == "of")
              branchInvocation.args(0).toString
            else // should
              branchInvocation.target.toString
          case None => ""
        }
        // Now get the test names.
        node match {
          case ConstructorBlock(className, children) => 
            val testNames = getTestNamesFromChildren(prefix, children)
            Some(new Selection(className, if (prefix.length > 0) prefix else className, testNames))
          case invocation @ MethodInvocation(className, target, parent, children, name, args @ _*) =>
            if (target.toString == "behaviour" && name == "of") {
              // behaviour of get selected.
              val testNames = getTestNamesFromChildren(prefix, constructor.children)
              Some(new Selection(className, prefix, testNames))
            }
            else {
              val shouldMustInvocationOpt = findShouldMustMethodInvocation(invocation)
              shouldMustInvocationOpt match {
                case Some(shouldMustInvocation) => 
                  val testName = getTestName(prefix, shouldMustInvocation)
                  Some(new Selection(className, testName, Array[String](testName)))
                case None => None
              }
            }
          case _ => None
        }
      case None => None
    }
  }

  private def getTestNamesFromChildren(prefix: String, children: Array[AstNode]) = {
    children
      .filter(node => node.isInstanceOf[MethodInvocation] && isValidName(node.name, Set("should", "must")))
      .map { node =>
        val invocation = node.asInstanceOf[MethodInvocation]
        getTestName(prefix, invocation)
      }
  }
  
  private def getTestName(prefix: String, invocation: MethodInvocation) = {
    prefix + " " + invocation.name  + " " + invocation.args(0)
  }
  
  @tailrec
  private def findShouldMustMethodInvocation(invocation: MethodInvocation): Option[MethodInvocation] = {
    if (isSingleStringParamInvocationWithName(invocation, Set("should", "must")))
      Some(invocation)
    else if (invocation.target.isInstanceOf[MethodInvocation])
      findShouldMustMethodInvocation(invocation.target.asInstanceOf[MethodInvocation])
    else
      None
  }
}