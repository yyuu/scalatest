package org.scalatest.spi.location

case class MethodDefinition(
  className: String,
  parent: AstNode,
  var children: Array[AstNode],
  name: String, 
  paramTypes: Class[_]*) 
extends AstNode {
  def addChild(node: AstNode) {
    children = (children.toList ::: List(node)).toArray
  }
}