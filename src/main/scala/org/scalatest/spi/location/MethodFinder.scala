package org.scalatest.spi.location

class MethodFinder extends Finder {

  def find(node: AstNode): Option[Test] = {
    node match {
      case MethodDefinition(className, parent, children, name, paramTypes @ _*) => 
        if (paramTypes.length == 0)
          Some(new Test(className, className + "." + name, Array(name)))
        else
          None
      case _ => None
    }
  }
  
}