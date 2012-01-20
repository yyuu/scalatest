package org.scalatest.path

trait FunSpec extends org.scalatest.FunSpec {
  
  
  

}

private[path] object FunSpec {
   private[this] val path = new ThreadLocal[Option[List[Int]]]
   path.set(None)

   private def setPath(ints: List[Int]) {
     if (path.get.isDefined)
       throw new IllegalStateException("Path was already defined when setPath was called, as: " + path.get)
     path.set(Some(ints))
   }

   private def getPath(): Option[List[Int]] = {
     val p = path.get
     path.set(None)
     p
   }
}