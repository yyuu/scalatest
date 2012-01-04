package org.scalatest.spi.location

case class MethodDefinition(
  pClassName: String,
  pParent: AstNode,
  pChildren: Array[AstNode],
  pName: String, 
  paramTypes: Class[_]*) 
extends AstNode(pClassName, pParent, pChildren, pName)