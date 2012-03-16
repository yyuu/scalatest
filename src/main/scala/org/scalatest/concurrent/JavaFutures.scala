package org.scalatest.concurrent

import org.scalatest.time.Span
import java.util.concurrent.{TimeUnit, Future => FutureOfJava}
import org.scalatest.{TestFailedException, Resources}
import org.scalatest.StackDepthExceptionHelper.getStackDepthFun

/**
 * Provides an implicit conversion from <code>java.util.concurrent.Future[T]</code> to
 * <code>FutureConcept[T]</code>.
 */
trait JavaFutures extends Futures {

  /**
   * Implicitly converts a <code>java.util.concurrent.Future[T]</code> to
   * <code>FutureConcept[T]</code>, allowing you to invoke the methods
   * defined on <code>FutureConcept</code> on a Java <code>Future</code>, as well as to pass a Java future
   * to the <code>whenReady</code> methods of supertrait <code>Futures</code>.
   *
   * @param javaFuture a <code>java.util.concurrent.Future[T]</code> to convert
   */
  implicit def convertJavaFuture[T](javaFuture: FutureOfJava[T]): FutureConcept[T] =
    new FutureConcept[T] {
      def value: Option[Either[Throwable, T]] =
        if (javaFuture.isDone())
          Some(Right(javaFuture.get))
        else
          None
      def isExpired: Boolean = false // Java Futures don't support the notion of a timeout
      def isCanceled: Boolean = javaFuture.isCancelled // Two ll's in Canceled. The verbosity of Java strikes again!
      // TODO: Catch TimeoutException and wrap that in a TFE with ScalaTest's TimeoutException I think.
      // def awaitAtMost(span: Span): T = javaFuture.get(span.totalNanos, TimeUnit.NANOSECONDS)
      override def awaitResult(implicit config: TimeoutConfig): T = {
        if (javaFuture.isCanceled)
          throw new TestFailedException(
            sde => Some(Resources("futureWasCanceled")),
            None,
            getStackDepthFun("JavaFutures.scala", "awaitResult")
          )
        try {
          javaFuture.get(config.timeout.totalNanos, TimeUnit.NANOSECONDS)
        }
        catch {
          case e: java.util.concurrent.TimeoutException =>
            throw new TestFailedException(  // Shouldn't this mix in TimeoutException?
              sde => Some(Resources("wasNeverReady")),
              None,
              getStackDepthFun("JavaFutures.scala", "awaitResult")
            )
        }
      }
    }
}
