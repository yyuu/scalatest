package org.scalatest.multi

import actors.Actor
import java.io.{StringWriter, PrintWriter}
import java.util.concurrent.{Semaphore, ArrayBlockingQueue, CountDownLatch, TimeUnit, ConcurrentHashMap}

import java.util.{HashMap, IdentityHashMap, Random}
import java.util.concurrent.locks.{Condition, ReentrantReadWriteLock}
import org.scalatest.matchers.MustMatchers
import org.scalatest.{FunSuite, Suite}
import scala.collection.mutable.Map


import Thread.State._
import PimpedThreadGroup._

trait MultiThreadedTest {

  type Tick = Int

  ////////////////////////// copyied/pasted here for now /////////////

  /**
   * Command line key for indicating the regularity (in milliseconds)
   * with which the clock thread regulates the thread methods.
   */
  val CLOCKPERIOD_KEY = "tunit.clockPeriod"

  /**
   * Command line key for indicating the time limit (in seconds) for
   * runnable threads.
   */
  val RUNLIMIT_KEY = "tunit.runLimit"

  /**
   * The default clock period in milliseconds
   */
  val DEFAULT_CLOCKPERIOD = 10

  /**
   * The default run limit in seconds
   */
  val DEFAULT_RUNLIMIT = 5

  //////////////////////////////////////////////////////////////////////

  /**
   * The metronome used to coordinate between threads. This clock
   * is advanced by the clock thread started by       { @link TestFramework }.
   * The clock will not advance if it is frozen.
   *
   * @see # waitForTick ( int )
   * @see # freezeClock ( )
   * @see # unfreezeClock ( )
   */
  val clock = new Clock

  /**
   * If true, the debugging information is printed to standard out
   * while the test runs
   */
  var trace = System.getProperty("tunit.trace") == "true"

  /**
   * a BlockingQueue containing the first Error/Exception that occured
   * in thread methods or that are thrown by the clock thread
   */
  val error = new ArrayBlockingQueue[Throwable](20)

  /////////////////////// thread management start //////////////////////////////

  // place all threads in a new thread group
  val threadGroup = new ThreadGroup("MTC-Threads")
  var threads = List[Thread]()
  val threadRegistration = new Semaphore(0)

  /**
   * Get a thread given the method name that it corresponds to. E.g.
   * to get the thread running the contents of the method
   * <code>thread1()</code>, call <code>getThreadByName("thread1")</code>
   *
   * <p>
   * NOTE:       { @link # initialize ( ) } is called before threads are created,
   * so this method returns null if called from       { @link # initialize ( ) }
   * (but not from       { @link # finish ( ) } ).
   *
   * @see # getThread ( int )
   *
   * @param methodName
   *             the name of the method corresponding to the thread requested
   * @return
   * the thread corresponding to methodName
   */
  def getThread(name: String): Thread = {
    clock.synchronized {
      threads.find(_.getName == name) match {
        case Some(t) => t
        case None => throw new NoSuchElementException("no thread with name: " + name)
      }
    }
  }

  def getThread(i: Int): Thread = getThread("thread" + i)

  def thread[T](f: => T): Thread = thread("thread" + threads.size) {f}

  def thread[T](desc: String)(f: => T): Thread = {
     val t = createTestThread(desc, f _)
     threads = t :: threads
     t
   }

  def testing[T](as:Actor*) = for( a <- as ) thread("thread for: " + a){ a.start }

   private lazy val threadStartLatch = new CountDownLatch(threads.size)

   private def createTestThread[T](name: String, f: () => T) = {
     val r = new Runnable() {
       def run() {
         //println("thread is running!")
         try {
           threadRegistration.release
           threadStartLatch.countDown
           threadStartLatch.await
           // At this point all threads are created and released
           // (in random order?) together to run in parallel
           //println("about to execute function!")
           f.apply
           //println("executed function!")
         } catch {
           case e: ThreadDeath => return
           case t: Throwable => {
             //println("offering error: " + t)
             error offer t
             signalError()
           }
         } 
       }
     }
     new Thread(threadGroup, r, name)
   }

  /////////////////////// thread management end //////////////////////////////

  /////////////////////// finish handler end //////////////////////////////


  def finish(f: => Unit) {finishFunction = Some(f _)}

  private var finishFunction: Option[() => Unit] = None

  /**
   * This method is invoked in a test after after all test threads have
   * finished.
   *
   */
  private def runFinishFunction {
    finishFunction match {
      case Some(f) => f()
      case _ =>
    }
  }

  /////////////////////// finish handler end //////////////////////////////

  /////////////////////// clock management start //////////////////////////////

  /**
   * Force this thread to block until the thread metronome reaches the
   * specified value, at which point the thread is unblocked.
   *
   * @param c the tick value to wait for
   */
  def waitForTick(t: Tick) { clock.waitForTick(t) }

  /**
   * Gets the current value of the thread metronome. Primarily useful in
   * assert statements.
   *
   * @see # assertTick ( int )
   *
   * @return the current tick value
   */
  def tick: Int = clock.time

  def withClockFrozen[T](f: => T) = clock.withClockFrozen(f _)

  /**
   * Check if the clock has been frozen by any threads.
   */
  def isClockFrozen: Boolean = clock.isFrozen

  /////////////////////// clock management end //////////////////////////////


  // ===============================
  // -- Customized Wait Functions --
  // - - - - - - - - - - - - - - - -


  /**
   * Calling this method from one of the test threads may cause the
   * thread to yield. Use this between statements to generate more
   * interleavings.
   */
  def mayYield() {mayYield(0.5)}

  /**
   * Calling this method from one of the test threads may cause the
   * thread to yield. Use this between statements to generate more
   * interleavings.
   *
   * @param probability
   *             (a number between 0 and 1) the likelihood that Thread.yield() is called
   */
  def mayYield(probability: Double) {
    if (new Random().nextDouble() < probability) Thread.`yield`
  }

  def getStackTraces = {
    val sw = new StringWriter()
    val out = new PrintWriter(sw)
    for (t <- threads) {
      out.println(t.getName + " " + t.getState)
      for (st <- t.getStackTrace) {
        out.println("  " + st)
      }
    }
  }

  //////////////////////////////// run methods start ////////////////////////////////////////

  def runMultiThreadedTest {
    runMultiThreadedTest(getInt(CLOCKPERIOD_KEY, DEFAULT_CLOCKPERIOD), getInt(RUNLIMIT_KEY, DEFAULT_RUNLIMIT))
  }
  
  def getInt(prop: String, default: Int): Int = Integer.getInteger(prop, default).intValue

  /**
   * Run multithreaded test case once.
   *
   * @param clockPeriod
   *               The period (in ms) between checks for the clock (or null for
   *               default or global setting)
   * @param runLimit
   *               The limit to run the test in seconds (or null for default or
   *               global setting)
   */
  def runMultiThreadedTest(clockPeriod: Int, runLimit: Int) {
    // invoke each thread method in a seperate thread
    startThreads

    // start and add clock thread
    val clockThread = startClock(clockPeriod, runLimit)

    // wait until all threads have ended
    waitForThreads(threads + clockThread)

    // invoke finish at the end of each run
    runFinishFunction
  }

  /**
   * Invoke each of the thread methods in a seperate thread and
   * place them all in a common (new) thread group.
   * 
   * @return
   * The thread group for all the newly created test case threads
   */
  private def startThreads {
    threads.foreach {
      t => {
        start(t)
        // wait for at least one thread to be started.
        threadRegistration.acquireUninterruptibly
      }
    }
    (threadGroup, threads)
  }

  /**
   * Start and return a clock thread which periodically checks all the test case
   * threads and regulates them.
   *
   * <p>
   * If all the threads are blocked and at least one is waiting for a tick, the clock
   * advances to the next tick and the waiting thread is notified. If none of the
   * threads are waiting for a tick or in timed waiting, a deadlock is detected. The
   * clock thread times out if a thread is in runnable or all are blocked and one is
   * in timed waiting for longer than the runLimit.
   *
   * @param clockPeriod
   *             The period (in ms) between checks for the clock (or null for
   *             default or global setting)
   * @param runLimit
   *             The limit to run the test in seconds (or null for default or
   *             global setting)
   * @return
   * The (already started) clock thread
   */
  def startClock(clockPeriod: Int, runLimit: Int): Thread = {
    // hold a reference to the current thread. This thread
    // will be waiting for all the test threads to finish. It
    // should be interrupted if there is an deadlock or timeout
    // in the clock thread
    start(ClockThread(Thread.currentThread, clockPeriod, runLimit))
  }

  def start(t: Thread): Thread = {
    println("starting thread: " + t.getName)
    t.start
    t
  }

  /**
   * Wait for all of the test case threads to complete, or for one
   * of the threads to throw an exception, or for the clock thread to
   * interrupt this (main) thread of execution. When the clock thread
   * or other threads fail, the error is placed in the shared error array
   * and thrown by this method.
   *
   * @param threads
   *             List of all the test case threads and the clock thread
   * @throws Throwable
   *             The first error or exception that is thrown by one of the threads
   */
  private def waitForThreads(threads: List[Thread]) {
    //println("waiting for threads: " + threads)

    def waitForThread(t: Thread) {
      //println("waiting for: " + t.getName + " which is in state:" + t.getState)
      try {
        if (t.isAlive() && error.peek != null) {
          println("stopping thread: " + t.getName)
          t.stop()
          println("...stopped thread: " + t.getName)
        }
        else {
          println("joining thread: " + t.getName)
          t.join()
          println("...joined thread: " + t.getName)
        }
      } catch {
        case e: InterruptedException => {
          println("killed waiting for threads. probably deadlock or timeout.")
          error offer new AssertionError(e)
        }
      }
    }

    threads foreach waitForThread

    error.peek match {
      case null =>
      case e => throw e
    }
  }

  /**
   * Stop all test case threads and clock thread, except the thread from
   * which this method is called. This method is used when a thread is
   * ready to end in failure and it wants to make sure all the other
   * threads have ended before throwing an exception.
   *
   * @param threads
   *             LinkedList of all the test case threads and the clock thread
   */
  def signalError() {
    //println("signaling error to all threads")
    for (t <- threads; if(t != Thread.currentThread)) {
      //println("signaling error to " + t.getName)
      val assertionError = new AssertionError(t.getName + " killed by " + currentThread.getName)
      assertionError setStackTrace t.getStackTrace
      t stop assertionError
    }
  }

  case class ClockThread(mainThread: Thread,
                         clockPeriod: Int,
                         maxRunTime: Int) extends Thread("Clock Thread") {
    this setDaemon true
    var lastProgress = System.currentTimeMillis
    var deadlockCount = 0

    override def run {
      while (threadGroup.any_threads_alive_?) {
        if (threadGroup.any_threads_running_?) {
          if (running_too_long_?) timeout
        }
        else if (clock.any_thread_waiting_for_a_tick_?) {
          clock.tick
          deadlockCount = 0
          lastProgress = System.currentTimeMillis
        }
        else if (!threadGroup.any_threads_in_timed_waiting_?) {
          detectDeadlock
        }
        Thread sleep clockPeriod
      }
    }

    def dump {
      println("------------dump-------------------")
      threadGroup.getThreads.foreach {t => println(t + " in state: " + t.getState)}
    }

    def running_too_long_? = System.currentTimeMillis - lastProgress > 1000L * maxRunTime

    def timeout {
      mainThread.interrupt
      error offer new IllegalStateException("No progress")
      signalError
    }

    def detectDeadlock {
      if (deadlockCount == 50) {
        mainThread.interrupt
        error offer  new IllegalStateException("Deadlock")
        signalError
      }
      else deadlockCount += 1
    }
  }
}
