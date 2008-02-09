package org.scalatest.jmock;

trait JMockHelper {

  import org.specs.mock.JMocker._
  
  def withMock(f: => Unit): Unit = {
    try{
      restart
      f
      context.assertIsSatisfied
    }catch { 
      case e: org.jmock.api.ExpectationError => {
        throw new Exception(e)
      }
    }
  }
}
