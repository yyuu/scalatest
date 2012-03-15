package org.scalatest.concurrent

import org.scalatest.time.Span
import java.util.concurrent.{TimeUnit, Future => FutureOfJava}

trait JavaFutures {

  /**
   * Implicitly converts a <code>java.util.concurrent.Future[T]</code> to
   * <code>org.scalatest.concurrent.FutureConcept[T]</code>, allowing a Java <code>Future</code> to be passed
   * to the <code>whenReady</code> methods of this trait.
   *
   * @param futureOfJava a <code>java.util.concurrent.Future[T]</code> to convert
   */
  implicit def convertFutureOfJava[T](futureOfJava: FutureOfJava[T]) =
    new FutureConcept[T] {
      def value: Option[Either[Throwable, T]] =
        if (futureOfJava.isDone())
          Some(Right(futureOfJava.get))
        else
          None
      def isExpired: Boolean = false // Java Futures don't support the notion of a timeout
      def isCanceled: Boolean = futureOfJava.isCancelled // Two ll's in Canceled. The verbosity of Java strikes again!
      // TODO: Catch TimeoutException and wrap that in a TFE with ScalaTest's TimeoutException I think.
      def awaitAtMost(span: Span): T = futureOfJava.get(span.totalNanos, TimeUnit.NANOSECONDS)
    }
}
