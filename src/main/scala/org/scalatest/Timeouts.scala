package org.scalatest

import java.util.TimerTask
import java.util.Timer
import StackDepthExceptionHelper.getStackDepth
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.Selector
import java.net.Socket

trait Timeouts {
  
  private class TimeoutTask(testThread: Thread, resources: List[AnyRef]) extends TimerTask {
    @volatile
    var timeout = false
    override def run() {
      timeout = true
      testThread.interrupt
      resources.foreach { res => 
        res match {
          case socket: Socket => socket.close()
          case selector: Selector => selector.wakeup()
        }
      }
    }
  }

  def failAfter[T](millis: Long, resources: List[AnyRef] = List.empty)(f: => T): T = {
    val timer = new Timer()
    val task = new TimeoutTask(Thread.currentThread(), resources)
    timer.schedule(task, millis)
    try {
      val result = f
      timer.cancel()
      if (task.timeout)
        throw new TestFailedException(sde => Some(Resources("timeoutFailAfter", millis.toString)), None, getStackDepth("Timeouts.scala", "failAfter"))
      result
    }
    catch {
      case _: Throwable if (task.timeout) =>
        throw new TestFailedException(sde => Some(Resources("timeoutFailAfter", millis.toString)), None, getStackDepth("Timeouts.scala", "failAfter"))
    }
    finally {
      // Make sure to clear the interrupt status
      Thread.interrupted()
    }
  }
}

object Timeouts extends Timeouts
