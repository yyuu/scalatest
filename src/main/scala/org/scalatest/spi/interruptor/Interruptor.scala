package org.scalatest.spi.interruptor

trait Interruptor {
  def interrupt(testThread: Thread): Unit
}

object Interruptor {
  def apply(fun: => Unit) = new FunInterruptor(fun)
}