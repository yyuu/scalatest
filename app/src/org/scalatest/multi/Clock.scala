/*
 * Created by IntelliJ IDEA.
 * User: joshcough
 * Date: Jun 20, 2009
 * Time: 8:56:54 AM
 */
package org.scalatest.multi

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import scala.collection.mutable.Map

class Clock {
  
  type Tick = Int
  private var _time = 0
  val lock = new AnyRef

  /**
   * Read locks are acquired when clock is frozen and must be
   * released before the clock can advance in a waitForTick().
   */
  val rwLock = new ReentrantReadWriteLock()


  /**
   * Map each thread to the clock tick it is waiting for.
   */
  var threadsWithTickCounts = Map[Thread, Tick]()

  def tick {
    lock.synchronized {
      rwLock.writeLock.lock
      println("tick! from: " + _time + " to: " + (_time + 1))
      _time = _time + 1
      rwLock.writeLock.unlock
      lock.notifyAll
    }
  }

  def time: Tick = {
    rwLock.readLock.lock
    val t = _time
    rwLock.readLock.unlock
    t
  }

  def waitForTick(t: Tick) {

    lock.synchronized {
      threadsWithTickCounts += (Thread.currentThread -> t)
      while (time < t) {
        try {
          //println(Thread.currentThread().getName() + " is waiting for time " + t)
          //println("the current tick is: " + time)
          lock.wait
        } catch {
          case e: InterruptedException => throw new AssertionError(e)
        }
      }
    }
  }

  def any_thread_waiting_for_a_tick_? = threadsWithTickCounts.values.exists(_ > time)


  // =======================================
  // -- Components for freezing the clock --
  // - - - - - - - - - - - - - - - - - - - -


  /**
   * When the clock is frozen, it will not advance even when all threads
   * are blocked. Use this to block the current thread with a time limit,
   * but prevent the clock from advancing due to a { @link # waitForTick ( int ) } in
   * another thread.
   */
  def withClockFrozen[T](f: => T) {
    rwLock.readLock.lock
    f
    rwLock.readLock.unlock
  }


  /**
   * Check if the clock has been frozen by any threads.
   */
  def isFrozen: Boolean = rwLock.getReadLockCount > 0

}
