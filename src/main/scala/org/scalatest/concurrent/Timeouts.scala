/*
 * Copyright 2001-2012 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.concurrent

import java.util.TimerTask
import java.util.Timer
import org.scalatest.StackDepthExceptionHelper.getStackDepthFun
import org.scalatest.TestFailedException
import org.scalatest.Resources
import org.scalatest.StackDepthException
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.Selector
import java.net.Socket

/**
 * Trait that provides a <code>failAfter</code> construct, which allows you to specify a time limit for an
 * operation passed as a by-name parameter, as well as a way to interrupt it if the operation exceeds its time limit.
 *
 * <p>
 * Here's an example:
 * </p>
 *
 * <pre>
 * failAfter(100) {
 *   Thread.sleep(200)
 * }
 * </pre>
 *
 * <p>
 * The above code, after 100 milliseconds, will produce a <code>TestFailedException</code> with a message
 * that indicates a timeout expired:
 * </p>
 *
 * <p>
 * <code>The code passed to failAfter did not complete before the specified timeout of 100 milliseconds.</code>
 * </p>
 *
 *
 * @author Chua Chee Seng
 * @author Bill Venners
 */
trait Timeouts {

  private class TimeoutTask(testThread: Thread, interruptor: Interruptor) extends TimerTask {
    @volatile var timedOut = false
    @volatile var needToResetInterruptedStatus = false
    override def run() {
      timedOut = true
      val beforeIsInterrupted = testThread.isInterrupted()
      interruptor.interrupt(testThread)
      val afterIsInterrupted = testThread.isInterrupted()
      if(!beforeIsInterrupted && afterIsInterrupted)
        needToResetInterruptedStatus = true
    }
  }

  implicit val defaultInterruptor: Interruptor = new ThreadInterruptor()

  def failAfter[T](timeout: Long)(fun: => T)(implicit interruptor: Interruptor): T = {
    timeoutAfter(
      timeout,
      fun,
      interruptor,
      t => new TestFailedException(
        sde => Some(Resources("timeoutFailedAfter", timeout.toString)), t, getStackDepthFun("Timeouts.scala", "failAfter")
      )
    )
  }

/* Uncomment for 2.0
  def cancelAfter[T](timeout: Long)(f: => T)(implicit interruptor: Interruptor): T = {
    timeoutAfter(timeout, f, interruptor, t => new TestCanceledException(sde => Some(Resources("timeoutCanceledAfter", timeout.toString)), t, getStackDepthFun("Timeouts.scala", "cancelAfter")))
  }
*/

  private def timeoutAfter[T](timeout: Long, f: => T, interruptor: Interruptor, exceptionFun: Option[Throwable] => StackDepthException): T = {
    val timer = new Timer()
    val task = new TimeoutTask(Thread.currentThread(), interruptor)
    timer.schedule(task, timeout)
    try {
      val result = f
      timer.cancel()
      if (task.timedOut) {
        if (task.needToResetInterruptedStatus)
          Thread.interrupted() // To reset the flag probably. He only does this if it was not set before and was set after, I think.
        throw exceptionFun(None)
      }
      result
    }
    catch {
      case t => 
        timer.cancel() // Duplicate code could be factored out I think. Maybe into a finally? Oh, not that doesn't work. So a method.
        if(task.timedOut) {
          if (task.needToResetInterruptedStatus)
            Thread.interrupted() // Clear the interrupt status (There's a race condition here, but not sure we an do anything about that.)
          throw exceptionFun(Some(t))
        }
        else
          throw t
    }
  }
}

/**
 * Companion object that facilitates the importing of <code>Timeouts</code> members as 
 * an alternative to mixing in the trait. One use case is to import <code>Timeouts</code>'s members so you can use
 * them in the Scala interpreter.
 */
object Timeouts extends Timeouts
