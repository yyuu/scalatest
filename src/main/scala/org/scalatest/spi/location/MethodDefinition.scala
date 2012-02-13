package org.scalatest.spi.location

class MethodDefinition(
  pClassName: String,
  pParent: AstNode,
  pChildren: Array[AstNode],
  pName: String, 
  pParamTypes: String*) 
extends AstNode {
  import scala.collection.mutable.ListBuffer
  private val childrenBuffer = new ListBuffer[AstNode]()
  childrenBuffer ++= pChildren
  
  def className = pClassName
  lazy val parent = getParent
  protected def getParent() = {
    if (pParent != null)
      pParent.addChild(this)
    pParent
  }
  def children = childrenBuffer.toArray
  def name = pName
  def addChild(node: AstNode) = if (!childrenBuffer.contains(node)) childrenBuffer += node
  def paramTypes = pParamTypes
}

object MethodDefinition {
  def apply(className: String, parent: AstNode, children: Array[AstNode], name: String, paramTypes: String*) = 
    new MethodDefinition(className, parent, children, name, paramTypes.toList: _*)
  def unapply(value: MethodDefinition): Option[(String, AstNode, Array[AstNode], String, Array[String])] = 
    if (value != null)
      Some((value.className, value.parent, value.children, value.name, value.paramTypes.toArray))
    else
      None
}