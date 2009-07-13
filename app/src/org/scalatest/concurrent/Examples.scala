package org.scalatest.concurrent

/**
 * <pre>
 * class MTCBoundedBufferTest extends MultithreadedTestCase {
 *    ArrayBlockingQueue<Integer> buf;
 *    @Override public void initialize() {
 *       buf = new ArrayBlockingQueue<Integer>(1);
 *    }
 *
 *    public void thread1() throws InterruptedException {
 *       buf.put(42);
 *       buf.put(17);
 *       assertTick(1);
 *    }
 *
 *    public void thread2() throws InterruptedException {
 *       waitForTick(1);
 *       assertEquals(Integer.valueOf(42), buf.take());
 *       assertEquals(Integer.valueOf(17), buf.take());
 *    }
 *
 *    @Override public void finish() {
 *       assertTrue(buf.isEmpty());
 *    }
 * }
 * </pre>
 *
 * <pre>
 * class MTCBoundedBufferTest extends FunSuite with ConductorMethods with MustMatchers {
 *   test("call to put on a full queue blocks the producer thread"){
 *     val buf = new ArrayBlockingQueue[Int](1)
 *
 *     thread("producer") {
 *       buf put 42
 *       buf put 17
 *       tick mustBe 1
 *     }
 *
 *     thread("consumer") {
 *       waitForTick(1)
 *       buf.take must be(42)
 *       buf.take must be(17)
 *     }
 *
 *     finish {
 *       buf must be('empty)
 *     }
 *   }
 * }
 * </pre>
 *
 * <pre>
 * class MTCCompareAndSet extends MultithreadedTest {
 *    AtomicInteger ai;
 *    @Override public void initialize() {
 *        ai = new AtomicInteger(1);
 *    }
 *
 *    public void thread1() {
 *        while(!ai.compareAndSet(2, 3)) Thread.yield();
 *    }
 *
 *    public void thread2() {
 *        assertTrue(ai.compareAndSet(1, 2));
 *    }
 *
 *    @Override public void finish() {
 *        assertEquals(ai.get(), 3);
 *    }
 * }
 * </pre>
 *
 * <pre>
 * class MTCCompareAndSet extends FunSuite with ConductorMethods with MustMatchers {
 *   test("compare and set") {
 *     val ai = new AtomicInteger(1)
 *
 *     thread {
 *       while(!ai.compareAndSet(2, 3)) Thread.`yield`
 *     }
 *
 *     thread {
 *       ai.compareAndSet(1, 2) must be(true)
 *     }
 *
 *     finish {
 *       ai.ge must be(3)
 *     }
 *   }
 * }
 * </pre>
 *
 * <pre>
 * class MTCInterruptedAcquire extends MultithreadedTestCase {
 *    Semaphore s;
 *    @Override public void initialize() {
 *        s = new Semaphore(0);
 *    }
 *
 *    public void thread1() {
 *        try {
 *            s.acquire();
 *            fail("should throw exception");
 *        } catch(InterruptedException success){ assertTick(1); }
 *    }
 *
 *    public void thread2() {
 *        waitForTick(1);
 *        getThread(1).interrupt();
 *    }
 * }
 * </pre>
 *
 * <pre>
 * class MTCInterruptedAcquire extends FunSuite with ConductorMethods with MustMatchers {
 *   test("interrupted aquire"){
 *     val s = new Semaphore(0)
 *
 *     val nice = thread("nice") {
 *       intercept[InterruptedException] { s.acquire }
 *       tick must be(1)
 *     }
 *
 *     thread("rude") {
 *       waitForTick(1)
 *       nice.interrupt
 *     }
 *   }
 * }
 * </pre>
 *
 * <pre>
 * class MTCThreadOrdering extends MultithreadedTestCase {
 *    AtomicInteger ai;
 *    @Override public void initialize() {
 *        ai = new AtomicInteger(0);
 *    }
 *
 *    public void thread1() {
 *        assertTrue(ai.compareAndSet(0, 1)); // S1
 *        waitForTick(3);
 *        assertEquals(ai.get(), 3);          // S4
 *    }
 *
 *    public void thread2() {
 *        waitForTick(1);
 *        assertTrue(ai.compareAndSet(1, 2)); // S2
 *        waitForTick(3);
 *        assertEquals(ai.get(), 3);          // S4
 *    }
 *
 *    public void thread3() {
 *        waitForTick(2);
 *        assertTrue(ai.compareAndSet(2, 3)); // S3
 *    }
 * }
 * </pre>
 *
 * <pre>
 * class MTCThreadOrdering extends FunSuite with ConductorMethods with MustMatchers {
 *   test("thread ordering"){
 *     val ai = new AtomicInteger(0)
 *
 *     thread {
 *       ai.compareAndSet(0, 1) must be(true)  // S1
 *       waitForTick(3)
 *       ai.get() must be(3)                   // S4
 *     }
 *
 *     thread {
 *       waitForTick(1)
 *       ai.compareAndSet(1, 2) must be(true)  // S2
 *       waitForTick(3)
 *       ai.get must be(3)                     // S4
 *     }
 *
 *     thread {
 *       waitForTick(2)
 *       ai.compareAndSet(2, 3) must be(true)  // S3
 *     }
 *   }
 * }
 * </pre>
 *
 * <pre>
 * class MTCTimedOffer extends MultithreadedTestCase {
 *    ArrayBlockingQueue<Object> q;
 *
 *    @Override public void initialize() {
 *        q = new ArrayBlockingQueue<Object>(2);
 *    }
 *
 *    public void thread1() {
 *        try {
 *            q.put(new Object());
 *            q.put(new Object());
 *
 *            freezeClock();
 *            assertFalse(q.offer(new Object(),
 *                25, TimeUnit.MILLISECONDS));
 *            unfreezeClock();
 *
 *            q.offer(new Object(),
 *                2500, TimeUnit.MILLISECONDS);
 *            fail("should throw exception");
 *        } catch (InterruptedException success){
 *            assertTick(1);
 *        }
 *    }
 *
 *    public void thread2() {
 *        waitForTick(1);
 *        getThread(1).interrupt();
 *    }
 * }
 * </pre>
 *
 * <pre>
 * class MTCTimedOffer extends MultiThreadedFunSuite {
 *   test("timed offer") {
 *     val q = new ArrayBlockingQueue[String](2)
 *
 *     val producer = thread("producer"){
 *       q put "w"
 *       q put "x"
 *
 *       withClockFrozen {
 *         q.offer("y", 25, TimeUnit.MILLISECONDS) mustBe false
 *       }
 *
 *       intercept[InterruptedException] {
 *         q.offer("z", 2500, TimeUnit.MILLISECONDS)
 *       }
 *
 *       tick mustBe 1
 *     }
 *
 *     val consumer = thread("consumer"){
 *       waitForTick(1)
 *       producer.interrupt()
 *     }
 *   }
 * }
 * </pre>
 *
 * <pre>
 * class PimpedReadWriteLockTest extends ConcurrentTest {
 *
 *   val lock = new java.util.concurrent.locks.ReentrantReadWriteLock
 *   import PimpedReadWriteLock._
 *
 *   test("demonstrate various functionality") {
 *     // create 5 named test threads that all do the same thing
 *     5.threads("reader thread") {
 *       lock.read {
 *         logger.debug.around("using read lock") {waitForTick(2)}
 *       }
 *     }
 *
 *     // create 10 test threads that all do the same thing
 *     10 threads {
 *       lock.read {
 *         logger.debug.around("using read lock") {waitForTick(2)}
 *       }
 *     }
 *
 *     // create a single, named thread
 *     thread("writer thread") {
 *       waitForTick(1)
 *       lock.write {
 *         logger.debug.around("using write lock") {tick mustBe 2}
 *       }
 *     }
 *   }
 * }
 * </pre>
 */
class Examples