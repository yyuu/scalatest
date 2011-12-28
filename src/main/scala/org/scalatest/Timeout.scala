package org.scalatest

import java.util.TimerTask
import java.util.Timer
import StackDepthExceptionHelper.getStackDepth

trait Timeout {
  
  class TimeoutTask(testThread: Thread) extends TimerTask {
    override def run() {
      testThread.interrupt
    }
  }

  def failAfter[T](miliseconds: Long)(f: => T): T = {
    val timer = new Timer()
    timer.schedule(new TimeoutTask(Thread.currentThread()), miliseconds)
    try {
      val result = f
      timer.cancel()
      result
    }
    catch {
      case ie: InterruptedException => 
        throw new TestFailedException(sde => Some(Resources("timeoutFailAfter", miliseconds.toString)), None, getStackDepth("Timeout.scala", "failAfter"))
    }
  }
  
}

object Timeout extends Timeout