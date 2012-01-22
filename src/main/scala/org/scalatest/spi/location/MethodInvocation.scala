package org.scalatest.spi.location

case class MethodInvocation (
  className: String,
  target: AstNode, 
  parent: AstNode,
  var children: Array[AstNode],
  name: String, 
  args: AstNode*) 
extends AstNode {
  
  if (parent != null)
    parent.addChild(this)
  
  def addChild(node: AstNode) {
    children = (children.toList ::: List(node)).toArray
  }
}