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

import org.scalatest._
import Assertions.fail

class Waiter {

  private final val creatingThread = Thread.currentThread

  @volatile private var dismissedCount = 0
  @volatile private var thrown: Option[Throwable] = None
  
  private def setThrownIfEmpty(t: Throwable) {
    synchronized {
      if (thrown.isEmpty) thrown = Some(t)
    }
  }
  
  def apply(fun: => Unit) {
    try {
      fun
    }
    catch {
      case t: Throwable => setThrownIfEmpty(t)
    }
  }
  
  // -1 is forever? Or should I have a default of 1000?
  def await(timeout: Long = -1, dismissals: Int = 1) {
    if (Thread.currentThread != creatingThread)
      fail(Resources("awaitMustBeCalledOnCreatingThread"))
      
    val startTime = System.currentTimeMillis
    def timedOut = timeout >= 0 && startTime + timeout < System.currentTimeMillis
    while (dismissedCount < dismissals && !timedOut && thrown.isEmpty)
      Thread.sleep(10)
  
    if (thrown.isDefined)
      throw thrown.get
    else if (timedOut)
      fail(Resources("awaitTimedOut"))
  }
  
  def dismiss() {
    dismissedCount += 1
  }
}
