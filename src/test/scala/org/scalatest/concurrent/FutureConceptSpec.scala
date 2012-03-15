package org.scalatest.concurrent

import java.util.concurrent.{Future => FutureOfJava}
import java.util.concurrent.TimeUnit
import org.scalatest.FunSpec
import org.scalatest.time.{Span, Millisecond}

class FutureConceptSpec extends FunSpec with JavaFutures {

  describe("A Future") {

    class SuperFutureOfJava extends FutureOfJava[String] {
      def cancel(mayInterruptIfRunning: Boolean): Boolean = false
      def get: String = "hi"
      def get(timeout: Long, unit: TimeUnit): String = "hi"
      def isCancelled: Boolean = false
      def isDone: Boolean = true
    }

    ignore("can be queried to make sure it is ready within a certain time span") {
      // isReadyWithin(Span): Boolean
      val future = new SuperFutureOfJava
      assert(future.isReadyWithin(Span(1, Millisecond)))
    }
    it("can be asked to wait until readu, but limiting waiting to within a specified time span") {
      // isReadyWithin(Span): Boolean
      val future = new SuperFutureOfJava
      val result = future.awaitAtMost(Span(1, Millisecond))
      assert(result === "hi")
    }
  }
}
