package org.scalatest.spi.location

case class MethodInvocation (
  className: String,
  target: AstNode, 
  parent: AstNode,
  var children: Array[AstNode],
  name: String, 
  args: AnyRef*) 
extends AstNode {
  def addChild(node: AstNode) {
    children = (children.toList ::: List(node)).toArray
  }
}