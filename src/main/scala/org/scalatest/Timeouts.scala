package org.scalatest

import java.util.TimerTask
import java.util.Timer
import StackDepthExceptionHelper.getStackDepth
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.Selector
import java.net.Socket
import org.scalatest.spi.interruptor.Interruptor
import org.scalatest.spi.interruptor.ThreadInterruptor

trait Timeouts {
  
  private class TimeoutTask(testThread: Thread, interruptor: Interruptor) extends TimerTask {
    @volatile
    var timeout = false
    var isTimeoutInterrupted = false
    override def run() {
      timeout = true
      val beforeIsInterrupted = testThread.isInterrupted()
      interruptor.interrupt(testThread)
      val afterIsInterrupted = testThread.isInterrupted()
      if(!beforeIsInterrupted && afterIsInterrupted)
        isTimeoutInterrupted = true
    }
  }
  
  implicit val defaultInterruptor: Interruptor = new ThreadInterruptor()

  def failAfter[T](millis: Long)(f: => T)(implicit interruptor: Interruptor): T = {
    val timer = new Timer()
    val task = new TimeoutTask(Thread.currentThread(), interruptor)
    timer.schedule(task, millis)
    try {
      val result = f
      timer.cancel()
      if (task.timeout) {
        if (task.isTimeoutInterrupted)
          Thread.interrupted()
        throw new TestFailedException(sde => Some(Resources("timeoutFailAfter", millis.toString)), None, getStackDepth("Timeouts.scala", "failAfter"))
      }
      result
    }
    catch {
      case t => 
        timer.cancel()
        if(task.timeout) {
          if (task.isTimeoutInterrupted)
            Thread.interrupted()
          throw new TestFailedException(sde => Some(Resources("timeoutFailAfter", millis.toString)), Some(t), getStackDepth("Timeouts.scala", "failAfter"))
        }
        else
          throw t
    }
  }
}

object Timeouts extends Timeouts