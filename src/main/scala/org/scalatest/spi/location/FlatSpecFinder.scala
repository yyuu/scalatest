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
              //isSingleStringParamInvocationWithName(invocation, Set("of", "in"))
              Set("of", "in").contains(invocation.name)
            case _ => false
          }
        }
        // Get the prefix from the branch, which should be from the first method invocation
        val prefix = branchNodeOpt match {
          case Some(branchNode) => 
            val branchInvocation = branchNode.asInstanceOf[MethodInvocation]
            if (branchInvocation.name == "of")
              branchInvocation.args(0).toString
            else { // in 
              //branchInvocation.target.toString
              branchInvocation.target match {
                case MethodInvocation(className, target, parent, children, name, args) => 
                  target.toString
                case _ => branchInvocation.target.toString
              }
            }
          case None => ""
        }
        // Now get the test names.
        node match {
          case ConstructorBlock(className, children) => 
            val testNames = getTestNamesFromChildren(prefix, children)
            Some(new Selection(className, if (prefix.length > 0) prefix else className, testNames))
          case invocation @ MethodInvocation(className, target, parent, children, name, args) =>
            if (name == "of") {
              // behaviour of get selected.
              val testNames = getTestNamesFromChildren(prefix, constructor.children)
              Some(new Selection(className, prefix, testNames))
            }
            else if (name == "should") {
              invocation.parent match {
                case invocationParent @ MethodInvocation(className, target, parent, children, "in", args) => 
                  val testName = getTestName(prefix, invocationParent)
                  Some(new Selection(className, testName, Array(testName)))
                case _ => 
                  None
              }
            }
            else if (name == "in") {
              val testName = getTestName(prefix, invocation)
              Some(new Selection(className, testName, Array[String](testName)))
            }
            else 
              None
          case _ => None
        }
      case None => None
    }
  }

  private def getTestNamesFromChildren(prefix: String, children: Array[AstNode]) = {
    children
      .filter(node => node.isInstanceOf[MethodInvocation] && isValidName(node.name, Set("in")))
      .map { node =>
        val invocation = node.asInstanceOf[MethodInvocation]
        getTestName(prefix, invocation)
      }
  }
  
  private def getTargetString(target: AstNode, prefix: String, postfix: String): String = {
    if (target == null)
      postfix
    else {
      target match {
        case MethodInvocation(className, targetTarget, parent, children, "should", args) if (args.length > 0) => 
          "should " + args(0).toString
        case _ => 
          target.toString
      }
    } 
  }
  
  private def getTestName(prefix: String, invocation: MethodInvocation) = {
    prefix + " " + getTargetString(invocation.target, prefix, "")
  }
}