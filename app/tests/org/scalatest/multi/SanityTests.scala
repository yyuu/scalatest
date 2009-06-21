package org.scalatest.multi

/**
 * @author dood
 * Date: Jun 16, 2009
 * Time: 7:58:00 PM
 */

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import org.scalatest.matchers.MustMatchers
import org.scalatest.Suite

trait MustBeSugar { this: MustMatchers =>

  implicit def anyToMustBe(a: Any) = new {
    def mustBe(b: Any) {
      a must be(b)
    }

    def must_be(b: Any) {
      a must be(b)
    }
  }
}

class SanityMetronomeOrder extends MultiThreadedSuite with MustMatchers with MustBeSugar {

  //trace = true
  var s = ""

  thread("t1") {
    waitForTick(1)
    s = s + "A"

    waitForTick(3)
    s = s + "C"

    waitForTick(6)
    s = s + "F"
  }

  thread("t2") {
    waitForTick(2)
    s = s + "B"

    waitForTick(5)
    s = s + "E"

    waitForTick(8)
    s = s + "H"
  }

  thread("t3") {
    waitForTick(4)
    s = s + "D"

    waitForTick(7)
    s = s + "G"

    waitForTick(9)
    s = s + "I"
  }

  finish {
    s must be("ABCDEFGHI") // "Threads were not called in correct order"
  }
}

// Test order called is init, then thread, then finish
class SanityInitBeforeThreadsBeforeFinish extends MultiThreadedSuite with MustMatchers with MustBeSugar {

  val v1 = new AtomicInteger(0)
  val v2 = new AtomicInteger(0)
  assert(v1.compareAndSet(0, 1))
  assert(v2.compareAndSet(0, 1))
  val c = new CountDownLatch(2)

  thread("t1") {
    assert(v1.compareAndSet(1, 2))
    c.countDown()
    c.await()
  }

  thread("t2") {
    assert(v2.compareAndSet(1, 2))
    c.countDown()
    c.await()
  }

  finish {
    v1.intValue() must be(2)
    v2.intValue() must be(2)
  }
}

class SanityWaitForTickAdvancesWhenTestsAreBlocked extends MultiThreadedSuite with MustMatchers with MustBeSugar {
  var c: CountDownLatch = new CountDownLatch(3)

  thread {
    c.countDown()
    c.await()
  }

  thread {
    c.countDown()
    c.await()
  }

  thread {
    waitForTick(1)
    c.getCount() must be(1)
    waitForTick(2) // advances quickly
    c.getCount() must be(1)
    c.countDown()
  }

  finish {
    c.getCount() must be(0)
  }
}

class SanityWaitForTickBlocksThread extends MultiThreadedSuite with MustMatchers with MustBeSugar {
  var t: Thread = null

  thread {
    t = Thread.currentThread()
    waitForTick(2)
  }

  thread {
    waitForTick(1)
    t.getState must be(Thread.State.WAITING)
  }
}


class SanityThreadTerminatesBeforeFinishIsCalled extends MultiThreadedSuite with MustMatchers with MustBeSugar {
  var t1, t2: Thread = null

  thread {
    t1 = Thread.currentThread
  }

  thread {
    t2 = Thread.currentThread
  }

  finish {
    t1.getState must be(Thread.State.TERMINATED)
    t2.getState must be(Thread.State.TERMINATED)
  }
}

class SanityThreadMethodsInvokedInDifferentThreads extends MultiThreadedSuite with MustMatchers with MustBeSugar {
  var t1, t2: Thread = null

  thread {
    t1 = Thread.currentThread
    waitForTick(2)
  }

  thread {
    t2 = Thread.currentThread
    waitForTick(2)
  }

  thread {
    waitForTick(1)
    t1 must not(be(t2))
  }
}

// doesnt really make sense anymore....isnt really useful....
class SanityGetThreadReturnsCorrectThread extends MultiThreadedSuite with MustMatchers with MustBeSugar {
  var t: Thread = null

  val t0 = thread {
    t = Thread.currentThread
    waitForTick(2)
  }

  thread {
    waitForTick(1)
    t0 mustBe t
    getThread(0) mustBe t
  }
}

class SanityGetThreadByNameReturnsCorrectThread extends MultiThreadedSuite with MustMatchers with MustBeSugar {
  var t: Thread = null

  val fooey = thread("Fooey") {
    t = Thread.currentThread
    waitForTick(2)
  }

  thread("Booey") {
    waitForTick(1)
    getThread("Fooey") mustBe t
  }
}

