package org.scalatest.spi.location

class StringLiteral(pClassName: String, pParent: AstNode, pValue: String) extends AstNode {
  def className = pClassName
  def parent = pParent
  if (parent != null)
    parent.addChild(this)
  def children = Array.empty
  def name = "StringLiteral"
  def addChild(node: AstNode) = throw new UnsupportedOperationException("StringLiteral does not support addChild method.")
  val value = pValue
  override def toString() = {
    value.toString
  }
}

object StringLiteral {
  def apply(className: String, parent: AstNode, value: String) = 
    new StringLiteral(className, parent, value)
  def unapply(lit: StringLiteral): Option[(String, AstNode, String)] = 
    if (lit != null)
      Some((lit.className, lit.parent, lit.value))
    else
      None
}