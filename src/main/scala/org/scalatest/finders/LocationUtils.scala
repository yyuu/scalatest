package org.scalatest.finders
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object LocationUtils {
  
  private def getFinderInstance(clazz: Class[_]): Option[Finder] = {
    val styleOpt = clazz.getAnnotations.find(annt => annt.annotationType.getName == "org.scalatest.Style")
    styleOpt match {
      case Some(style) => 
        val valueMethod = style.annotationType.getMethod("value")
        val finderClassName = valueMethod.invoke(style).asInstanceOf[String]
        if (finderClassName != null) {
          val finderClass = clazz.getClassLoader.loadClass(finderClassName)
          val instance = finderClass.newInstance
          instance match {
            case finder: Finder => Some(finder)
            case _ => None
          }
        }
        else
          None
      case None => None
    }
    
  }
  
  @tailrec
  private def lookInSuperClasses(clazz: Class[_]): Option[Finder] = {
    val superClass = clazz.getSuperclass
    if (superClass != null) {
      val finder = getFinderInstance(superClass)
      finder match {
        case Some(finder) => Some(finder)
        case None => lookInSuperClasses(superClass)
      }
    }
    else
      None
  }
  
  @tailrec
  private def lookInInterfaces(interfaces: Array[Class[_]]): Option[Finder] = {
    if (interfaces.length > 0) {
      val interfaceWithFinderOpt = interfaces.find { getFinderInstance(_).isInstanceOf[Some[Finder]] }
      interfaceWithFinderOpt match {
        case Some(interfaceWithFinder) => getFinderInstance(interfaceWithFinder)
        case None => 
          val superInterfaces = new ListBuffer[Class[_]]
          superInterfaces ++= (interfaces.map { intf => intf.getInterfaces }.toList.flatten )
          lookInInterfaces(superInterfaces.toArray)
      }
    }
    else
      None
  }

  def getFinder(clazz: Class[_]) = {
    val ownFinderOpt = getFinderInstance(clazz)
    ownFinderOpt match {
      case Some(ownFinder) => Some(ownFinder)
      case None =>
        // Look for super interface first since style traits are compiled as Java interfaces
        val intfFinderOpt = lookInInterfaces(clazz.getInterfaces)
        intfFinderOpt match {
          case Some(intfFinder) => Some(intfFinder)
          case None => 
            // Look in super classes, in case custom test style is a class instead of trait.
            lookInSuperClasses(clazz)
        }
    }
  }
  
  @tailrec
  def getParentOfType[T <: AstNode](node: AstNode, clazz: Class[T]): Option[T] = {
    if (node.parent == null)
      None
    else {
      if (clazz.isAssignableFrom(node.parent.getClass))
        Some(node.parent.asInstanceOf[T])
      else
        getParentOfType(node.parent, clazz)
    }
  }
  
  @tailrec
  def getParentBeforeType[T <: AstNode](node: AstNode, clazz: Class[T]): Option[AstNode] = {
    if (node.parent == null)
      None
    else {
      if (clazz.isAssignableFrom(node.parent.getClass))
        Some(node)
      else
        getParentBeforeType(node.parent, clazz)
    }
  }
  
  def isSingleStringParamInvocationWithName(invocation: MethodInvocation, validNames: Set[String]): Boolean = {
    isValidName(invocation.name, validNames) && invocation.args.length == 1
  }
  
  def isValidName(name: String, validNames: Set[String]) = validNames.contains(name)
}