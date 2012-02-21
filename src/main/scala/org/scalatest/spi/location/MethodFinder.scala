package org.scalatest.spi.location
import scala.annotation.tailrec

class MethodFinder extends Finder {

  @tailrec
  final def find(node: AstNode): Option[Selection] = {
    node match {
      case MethodDefinition(className, parent, children, name, paramTypes) 
        if parent != null && parent.isInstanceOf[ConstructorBlock] && paramTypes.length == 0 =>
          Some(new Selection(className, className + "." + name, Array(name)))
      case _ => 
        if (node.parent != null)
          find(node.parent)
        else
          None
    }
  }
  
}