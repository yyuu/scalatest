package org.scalatest.spi.location
import scala.annotation.tailrec

class FunctionFinder extends Finder {

  @tailrec
  final def find(node: AstNode): Option[Selection] = {
    node match {
      case MethodInvocation(className, target, parent, children, "test", args)
        if parent != null && parent.isInstanceOf[ConstructorBlock] && args.length > 0 && args(0).isInstanceOf[StringLiteral] =>
          Some(new Selection(className, className + ": \"" + args(0).toString + "\"", Array(args(0).toString)))
      case _ => 
        if (node.parent != null)
          find(node.parent)
        else
          None
    }
  }
  
}