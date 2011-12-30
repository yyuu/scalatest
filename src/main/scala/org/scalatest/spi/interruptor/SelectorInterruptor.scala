package org.scalatest.spi.interruptor
import java.nio.channels.Selector

private[scalatest] class SelectorInterruptor(selector: Selector) extends Interruptor {
  
  def interrupt(testThread: Thread) {
    selector.wakeup()
  }
  
}

object SelectorInterruptor {
  def apply(selector: Selector) = new SelectorInterruptor(selector)
}