package org.scalatest.spi.location

class ConstructorBlock(
    pClassName: String, 
    pChildren: Array[AstNode]) 
extends AstNode {
  import scala.collection.mutable.ListBuffer
  private val childrenBuffer = new ListBuffer[AstNode]()
  childrenBuffer ++= pChildren
  // Because parent of constructor block is always null now, should enable this when we add ClassDef later.
  def className = pClassName
  def parent = null
  def children = childrenBuffer.toArray
  def name = "constructor"
  def addChild(node: AstNode) = if (!childrenBuffer.contains(node)) childrenBuffer += node
}

object ConstructorBlock {
  def apply(className: String, children: Array[AstNode]) = new ConstructorBlock(className, children)
  def unapply(value: ConstructorBlock): Option[(String, Array[AstNode])] = if (value != null) Some((value.className, value.children)) else None
}