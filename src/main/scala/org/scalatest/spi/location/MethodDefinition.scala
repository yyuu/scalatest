package org.scalatest.spi.location

case class MethodDefinition(
  className: String,
  parent: AstNode,
  var children: Array[AstNode],
  name: String, 
  paramTypes: String*) 
extends AstNode {
  
  if (parent != null)
    parent.addChild(this)
  
  def addChild(node: AstNode) {
    children = (children.toList ::: List(node)).toArray
  }
}