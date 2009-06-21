package org.scalatest.multi 

/**
 * @author dood
 * Date: Jun 20, 2009
 * Time: 8:54:27 AM
 */
import Thread.State._


object PimpedThreadGroup {
  implicit def threadGroupToPimpedThreadGroup(tg: ThreadGroup): PimpedThreadGroup = new PimpedThreadGroup(tg)
}

class PimpedThreadGroup(threadGroup: ThreadGroup) {
  def getThreads: List[Thread] = getThreads(true)

  def getThreads(recursive: Boolean): List[Thread] = {
    def getThreads(sizeEstimate: Int): Seq[Thread] = {
      val ths = new Array[Thread](sizeEstimate)
      if (threadGroup.enumerate(ths, recursive) == sizeEstimate) getThreads(sizeEstimate * 2)
      else for (t <- ths; if (t != null)) yield t
    }
    getThreads(threadGroup.activeCount() + 10).toList
  }

  def filter(state: Thread.State): List[Thread] = getThreads.filter(_.getState == state)
  def filter(f: Thread => Boolean): List[Thread] = getThreads.filter(f)

  def exists(state: Thread.State): Boolean = getThreads.find(_.getState == state).isDefined
  def exists(f: Thread => Boolean): Boolean = getThreads.find(f).isDefined

  def any_threads_alive_? = exists(t => t.getState != NEW && t.getState != TERMINATED)
  def any_threads_running_? = exists(_.getState == RUNNABLE)
  def any_threads_in_timed_waiting_? = exists(_.getState == TIMED_WAITING)
}
