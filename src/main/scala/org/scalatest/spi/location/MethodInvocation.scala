package org.scalatest.spi.location

case class MethodInvocation (
  pClassName: String,
  target: AstNode, 
  pParent: AstNode,
  pChildren: Array[AstNode],
  pName: String, 
  args: AnyRef*) 
extends AstNode(pClassName, pParent, pChildren, pName)