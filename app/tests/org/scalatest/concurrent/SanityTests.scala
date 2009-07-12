package org.scalatest.concurrent

/**
 * @author dood
 * Date: Jun 16, 2009
 * Time: 7:58:00 PM
 */
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import matchers.ShouldMatchers
import Thread.State._

trait MustBeSugar { this: ShouldMatchers =>

  implicit def anyToMustBe(a: Any) = new {
    def mustBe(b: Any) {
      a should be(b)
    }

    def must_be(b: Any) {
      a should be(b)
    }
  }
}

class SanityTests extends FunSuite with ConductorMethods with ShouldMatchers with MustBeSugar {
  
  test("metronome order") {

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
      s mustBe "ABCDEFGHI" // "Threads were not called in correct order"
    }
  }

  test("init before threads before finish") {
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
      v1.intValue() mustBe 2
      v2.intValue() mustBe 2
    }
  }

  test("wait for tick advances when threads are blocked") {
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
      c.getCount mustBe 1
      waitForTick(2) // advances quickly
      c.getCount mustBe 1
      c.countDown()
    }

    finish {
      c.getCount() mustBe 0
    }
  }

  test("wait for tick blocks thread") {

    logLevel = everything

    var t: Thread = null

    thread {
      t = currentThread
      waitForTick(2)
    }

    thread {
      waitForTick(1)
      t.getState should (be (WAITING) or be (BLOCKED))
    }
  }

  test("thread terminates before finish called") {
    var t1, t2: Thread = null

    thread {
      t1 = Thread.currentThread
    }

    thread {
      t2 = Thread.currentThread
    }

    finish {
      t1.getState mustBe TERMINATED
      t2.getState mustBe TERMINATED
    }
  }

  test("thread methods invoked on different threads") {
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
      t1 should not(be(t2))
    }
  }
}
