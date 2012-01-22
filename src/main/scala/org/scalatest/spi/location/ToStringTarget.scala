package org.scalatest.spi.location

class ToStringTarget(val parent: AstNode, var children: Array[AstNode], target: AnyRef) extends AstNode {
  
  if (parent != null)
    parent.addChild(this)

  def className() = {
    target.getClass.getName
  }
  
  def name() = {
    target.toString
  }
  
  override def toString() = {
    target.toString
  }  
  
  def addChild(node: AstNode) {
    children = (children.toList ::: List(node)).toArray
  }
}

object ToStringTarget {
  def apply(parent: AstNode, children: Array[AstNode], target: AnyRef) = 
    new ToStringTarget(parent, children, target)
}