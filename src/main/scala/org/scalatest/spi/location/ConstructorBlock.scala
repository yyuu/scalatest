package org.scalatest.spi.location

case class ConstructorBlock(
    pClassName: String,
    pChildren: Array[AstNode]) 
extends AstNode(pClassName, null, pChildren, "constructor")