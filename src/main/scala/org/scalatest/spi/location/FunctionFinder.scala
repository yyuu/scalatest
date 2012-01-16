package org.scalatest.spi.location

class FunctionFinder extends Finder {

  def find(node: AstNode): Option[Selection] = {
    node match {
      case MethodInvocation(className, target, parent, children, name, args @ _*) => 
        if(name == "test" && args.length > 0 && args(0).getClass() == classOf[String])
          Some(new Selection(className, className + ": \"" + args(0).toString + "\"", Array(args(0).toString)))
        else
          None
      case _ => None
    }
  }
  
}