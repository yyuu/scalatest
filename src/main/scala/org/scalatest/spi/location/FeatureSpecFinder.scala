package org.scalatest.spi.location

class FeatureSpecFinder extends Finder {
  
  def find(node: AstNode): Option[Selection] = {
    node match {
      case MethodInvocation(className, target, parent, children, "scenario", args @ _*) =>
        parent match {
          case parentInvocation: MethodInvocation => 
            if (parentInvocation.name == "feature" && parentInvocation.args.length > 1 && parentInvocation.args(0).getClass == classOf[StringLiteral]) {
              val testName = parentInvocation.args(0) + " " + args(0).toString
              Some(new Selection(className, testName, Array(testName)))
            }
            else
              Some(new Selection(className, args(0).toString, Array(args(0).toString)))
          case _ => 
            Some(new Selection(className, args(0).toString, Array(args(0).toString)))
        }
        
      case MethodInvocation(className, target, parent, children, "feature", args @ _*) =>
        if (args.length > 0 && args(0).getClass == classOf[StringLiteral]) {
          val featureText = args(0).toString
          val testNameList = children.filter( childNode => 
                               childNode.isInstanceOf[MethodInvocation] 
                               && childNode.name == "scenario" 
                               && childNode.asInstanceOf[MethodInvocation].args.length > 1
                               && childNode.asInstanceOf[MethodInvocation].args(0).getClass == classOf[StringLiteral]
                               ).map { childNode => 
                                 val child = childNode.asInstanceOf[MethodInvocation]
                                 featureText + " " + child.args(0)
                               }
          Some(new Selection(className, featureText, testNameList.toArray))
        }
        else
          None
      case _ => 
        None
    }
  }
}