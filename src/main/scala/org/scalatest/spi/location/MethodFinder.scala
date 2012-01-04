package org.scalatest.spi.location

class MethodFinder extends Finder {

  def find(node: AstNode): Option[Selection] = {
    node match {
      case MethodDefinition(className, parent, children, name, paramTypes @ _*) => 
        if (paramTypes.length == 0)
          Some(new Selection(className, className + "." + name, Array(name)))
        else
          None
      case _ => None
    }
  }
  
}