package org.scalatest.spi.interruptor

class FunInterruptor(fun: => Unit) extends Interruptor {

  def interrupt(testThread: Thread) {
    fun
  }
}