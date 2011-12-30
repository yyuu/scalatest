package org.scalatest.spi.interruptor

class DoNotInterrupt extends Interruptor {
  
  def interrupt(testThread: Thread) {
  }
}

object DoNotInterrupt {
  def apply() = new DoNotInterrupt()
}