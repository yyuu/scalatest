package org.scalatest.spi.location

class ToStringTarget(parent: AstNode, children: Array[AstNode], target: AnyRef) extends AstNode(target.getClass.getName, parent, children, target.toString) {

  override def toString() = {
    target.toString
  }
  
}