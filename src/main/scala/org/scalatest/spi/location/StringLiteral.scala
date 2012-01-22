package org.scalatest.spi.location

class StringLiteral(val className: String, val parent: AstNode, val value: String) extends AstNode {
  
  if (parent != null)
    parent.addChild(this)
  
  val name = "StringLiteral"
  
  def children = Array.empty[AstNode]
  
  def addChild(node: AstNode) {
    throw new UnsupportedOperationException("StringLiteral does not support addChild.")
  }
  
  override def toString() = {
    value.toString
  } 
  
}

object StringLiteral {
  def apply(className: String, parent: AstNode, value: String) = 
    new StringLiteral(className, parent, value)
}