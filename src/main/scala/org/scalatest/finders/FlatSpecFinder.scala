package org.scalatest.finders

import LocationUtils._
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

class FlatSpecFinder extends Finder {

  def find(node: AstNode): Option[Selection] = {
    node match {
      case constructor: ConstructorBlock => 
        getAllTestSelection(node.className, constructor.children)
      case invocation: MethodInvocation
        if invocation.name == "of" || invocation.name == "in" || invocation.name == "should" => 
        val constructorOpt: Option[ConstructorBlock] = node match {
          case constructor: ConstructorBlock => Some(constructor)
          case _ => 
            getParentOfType(node, classOf[ConstructorBlock])
        }
        constructorOpt match {
          case Some(constructor) =>
            val scopeNodeOpt = getScopeNode(node, constructor.children)
            scopeNodeOpt match {
              case Some(scopeNode) => 
                val prefix = getPrefix(scopeNode.asInstanceOf[MethodInvocation])
                getNodeTestSelection(node, prefix, constructor.children)
              case None => 
                if (node.parent != null)
                  find(node.parent)
                else
                  None
            }
          case None => None
        }
      case _ => 
        if (node.parent != null)
          find(node.parent)
        else
          None
    }
  }
  
  private def getAllTestSelection(className: String, constructorChildren: Array[AstNode]) = {
    var prefix: String = null
    val listBuffer = new ListBuffer[String]()
    for (child <- constructorChildren) {
      if (isScope(child))
        prefix = getPrefix(child.asInstanceOf[MethodInvocation])
      if(prefix != null && child.isInstanceOf[MethodInvocation] && child.name == "in") 
        listBuffer += getTestName(prefix, child.asInstanceOf[MethodInvocation])
    }
    Some(new Selection(className, className, listBuffer.toArray))
  }
  
  @tailrec
  private def getPrefix(scopeInvocation: MethodInvocation): String = {
    if (scopeInvocation.name == "of")
      scopeInvocation.args(0).toString
    else { 
      scopeInvocation.target match {
        case inInvocation @ MethodInvocation(className, target, parent, children, "should", args) => // in
          getPrefix(inInvocation)
        case _ => 
          scopeInvocation.target.toString
      }
    }
  }
  
  private def getScopeNode(node: AstNode, constructorChildren: Array[AstNode]): Option[AstNode] = {
    var scopeNode: AstNode = null
    if (isScope(node))
      return Some(node)
    else {
      for (child <- constructorChildren) {
        if (isScope(child))
          scopeNode = child
        else if(child == node || (child.isInstanceOf[MethodInvocation] && child.asInstanceOf[MethodInvocation].target == node)) {
          if (scopeNode != null)
            return Some(scopeNode)
          else
            return None
        }
      }
    }
    if (scopeNode != null)
      Some(scopeNode)
    else
      None
  }
  
  private def isScope(node: AstNode): Boolean = {
    def isScopeShould(invocation: MethodInvocation) = invocation.name == "should" && invocation.args.length > 0 && invocation.target != null && invocation.target.toString != "it"
    node match {
      case invocation: MethodInvocation //(className, target, parent, children, name, args) 
        if invocation.name == "of" || 
           isScopeShould(invocation) || 
           (invocation.name == "in" && invocation.target != null && invocation.target.isInstanceOf[MethodInvocation] && isScopeShould(invocation.target.asInstanceOf[MethodInvocation]))
           => 
           true
      case _ =>
        false
    }
  }
  
  private def getNodeTestSelection(node: AstNode, prefix: String, constructorChildren: Array[AstNode]) = {
    node match {
      case ConstructorBlock(className, children) => 
        val testNames = getTestNamesFromChildren(prefix, children)
        Some(new Selection(className, if (prefix.length > 0) prefix else className, testNames))
      case invocation @ MethodInvocation(className, target, parent, children, name, args) =>
        if (name == "of") {
          val nodeIdx = constructorChildren.indexOf(node)
          if (nodeIdx >= 0) {
            val startList = constructorChildren.drop(nodeIdx + 1)
            val subList = startList.takeWhile(!isScope(_))
            val testNames = getTestNamesFromChildren(prefix, subList)
            Some(new Selection(className, prefix, testNames))
          }
          else 
            None
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