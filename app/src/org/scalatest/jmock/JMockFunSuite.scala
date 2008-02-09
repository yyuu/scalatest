package org.scalatest.jmock;

import org.scalatest.fun.FunSuite

class JMockFunSuite extends FunSuite with JMockHelper{

  def mockTest(msg: String)(f: => Unit): Unit = test(msg){ withMock{ f } }
  
}
