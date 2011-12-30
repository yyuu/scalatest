package org.scalatest.spi.interruptor

class ThreadInterruptor extends Interruptor {

  def interrupt(testThread: Thread) {
    testThread.interrupt()
  }
  
}

object ThreadInterruptor {
  def apply = new ThreadInterruptor()
}