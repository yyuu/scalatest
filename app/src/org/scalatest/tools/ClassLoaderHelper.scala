package org.scalatest.tools

import java.lang.reflect.Constructor
import java.lang.reflect.Modifier
import java.net.URL
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.io.File
import java.io.IOException
import org.scalatest.Suite


private[tools] class ClassLoaderHelper(runpathList: List[String]) {

  if (runpathList == null) throw new NullPointerException
  
  val loader: ClassLoader = getRunpathClassLoader
  
  def loadClass( className: String ) = loader.loadClass(className)
  
  def getRunpathClassLoader: ClassLoader = {
    if (runpathList.isEmpty) {
      classOf[Suite].getClassLoader // Could this be null technically?
    }
    else {
      val urlsList: List[URL] =
        for (raw <- runpathList) yield {
          try {
            new URL(raw)
          }
          catch {
            case murle: MalformedURLException => {
  
              // Assume they tried to just pass in a file name
              val file: File = new File(raw)
  
              // file.toURL may throw MalformedURLException too, but for now
              // just let that propagate up.
              file.toURL() // If a dir, comes back terminated by a slash
            }
          }
        }
  
      // Here is where the Jini preferred class loader stuff went.
  
      // Tell the URLConnections to not use caching, so that repeated runs and reruns actually work
      // on the latest binaries.
      for (url <- urlsList) {
        try {
          url.openConnection.setDefaultUseCaches(false)
        }
        catch {
          case e: IOException => // just ignore these
        }
      }
  
      new URLClassLoader(urlsList.toArray, classOf[Suite].getClassLoader)
    }
  }
  
  def loadNamedSuites( suitesList: List[String] ): List[Suite] = {
    for (suiteClassName <- suitesList)
      yield {
        val clazz = loadClass(suiteClassName)
        clazz.newInstance.asInstanceOf[Suite]
     }
  }
  
  def loadCustomReporter(reporterClassName: String, argString: String): Reporter = {
    
    def handleException(resourceName: String, e: Exception) = {
      val msg1 = Resources(resourceName, reporterClassName)
      val msg2 = Resources("probarg", argString)
      val msg = msg1 + "\n" + msg2
    
      val iae = new IllegalArgumentException(msg)
      iae.initCause(e)
      throw iae
    }
    
    try {
      val reporterClass: java.lang.Class[_] = loadClass(reporterClassName) 
      reporterClass.newInstance.asInstanceOf[Reporter]
    }    // Could probably catch ClassCastException too
    catch {
      case e: ClassNotFoundException => handleException("cantLoadReporterClass",   e)
      case e: InstantiationException => handleException("cantInstantiateReporter", e)
      case e: IllegalAccessException => handleException("cantInstantiateReporter", e)
    }
  }

}
