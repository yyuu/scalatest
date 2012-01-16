package org.scalatest.spi.location

case class ConstructorBlock(
    className: String,
    var children: Array[AstNode]) 
extends AstNode {
  def parent = null
  def name = "constructor"
    def addChild(node: AstNode) {
    children = (children.toList ::: List(node)).toArray
  }
}