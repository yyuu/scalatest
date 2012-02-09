package org.scalatest.spi.location

class MethodInvocation (
  pClassName: String,
  pTarget: AstNode, 
  pParent: AstNode,
  pChildren: Array[AstNode],
  pName: String, 
  pArgs: AstNode*) 
extends AstNode {
  import scala.collection.mutable.ListBuffer
  private val childrenBuffer = new ListBuffer[AstNode]()
  childrenBuffer ++= pChildren
    
  def className = pClassName
  def parent = pParent
  if (parent != null)
    parent.addChild(this)
  def children = childrenBuffer.toArray
  def name = pName
  def addChild(node: AstNode) = childrenBuffer += node
  def target = pTarget
  def args = pArgs
}

object MethodInvocation {
  def apply(className: String, target: AstNode, parent: AstNode, children: Array[AstNode], name: String, args: AstNode*): MethodInvocation = 
    new MethodInvocation(className, target, parent, children, name, args.toList: _*)
  def unapply(value: MethodInvocation): Option[(String, AstNode, AstNode, Array[AstNode], String, Array[AstNode])] = 
    if (value != null)
      Some((value.className, value.target, value.parent, value.children, value.name, value.args.toArray))
    else
      None
}