package org.scalatest.spi.location

class ToStringTarget(val parent: AstNode, var children: Array[AstNode], target: AnyRef) extends AstNode {

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