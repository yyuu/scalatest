package org.scalatest.spi.location

class FeatureSpecFinder extends Finder {
  
  def find(node: AstNode): Option[Test] = {
    node match {
      case MethodInvocation(className, target, parent, children, "scenario", args @ _*) =>
        if (parent == null || !parent.isInstanceOf[MethodInvocation])
          Some(new Test(className, args(0).toString(), Array(args(0).toString())))
        else {
          val parentInvocation = parent.asInstanceOf[MethodInvocation]
          if (parentInvocation.name == "feature" && parentInvocation.args.length == 1 && parentInvocation.args(0).getClass == classOf[String]) {
            val testName = parentInvocation.args(0) + " " + args(0)
            Some(new Test(className, testName, Array(testName)))
          }
          else
            Some(new Test(className, args(0).toString(), Array(args(0).toString())))
        }
      case MethodInvocation(className, target, parent, children, "feature", args @ _*) =>
        val featureText = args(0).toString
        val testNameList = children.filter( childNode => 
                             childNode.isInstanceOf[MethodInvocation] 
                             && childNode.name == "scenario" 
                             && childNode.asInstanceOf[MethodInvocation].args.length == 1
                             && childNode.asInstanceOf[MethodInvocation].args(0).getClass == classOf[String]
                             ).map { childNode => 
                               val child = childNode.asInstanceOf[MethodInvocation]
                               featureText + " " + child.args(0)
                             }
        Some(new Test(className, featureText, testNameList.toArray))
      case _ => 
        None
    }
  }
}