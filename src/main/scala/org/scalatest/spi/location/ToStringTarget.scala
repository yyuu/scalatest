package org.scalatest.spi.location

class ToStringTarget(pClassName: String, pParent: AstNode, pChildren: Array[AstNode], val target: AnyRef) extends AstNode {
  import scala.collection.mutable.ListBuffer
  private val childrenBuffer = new ListBuffer[AstNode]()
  childrenBuffer ++= pChildren
  
  def className = pClassName
  def parent = pParent
  if (parent != null)
    parent.addChild(this)
  def children = childrenBuffer.toArray
  def name = target.toString
  def addChild(node: AstNode) = childrenBuffer += node
  override def toString() = {
    target.toString
  }
}

object ToStringTarget {
  def apply(className: String, parent: AstNode, children: Array[AstNode], target: AnyRef) = 
    new ToStringTarget(className, parent, children, target)
  def unapply(value: ToStringTarget): Option[(String, AstNode, Array[AstNode], AnyRef)] = 
    if (value != null)
      Some((value.className, value.parent, value.children, value.target))
    else
      None
}