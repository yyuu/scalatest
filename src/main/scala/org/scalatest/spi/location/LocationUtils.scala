package org.scalatest.spi.location
import org.scalatest.Style
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object LocationUtils {
  
  private def getFinderInstance(clazz: Class[_]): Option[Finder] = {
    val style = clazz.getAnnotation(classOf[Style])
    if (style != null && style.value() != null) 
      Some(style.value().newInstance)
    else
      None
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
}