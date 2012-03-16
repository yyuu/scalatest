package org.scalatest.concurrent

import org.scalatest.time.Span
import java.util.concurrent.{TimeUnit, Future => FutureOfJava}
import org.scalatest.StackDepthExceptionHelper.getStackDepthFun
import org.scalatest.Suite.anErrorThatShouldCauseAnAbort
import org.scalatest.{TestPendingException, TestFailedException, Resources}

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
   * // TODO: Document that for ExecutionException, we pull the cause out and wrap that
   * // in a TFE. So we drop the EE.
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
        val st = Thread.currentThread.getStackTrace
        val callerStackFrame =
          if (!st(2).getMethodName.contains("awaitResult"))
            st(2)
          else
            st(3)

        val methodName =
          if (callerStackFrame.getFileName == "Futures.scala" && callerStackFrame.getMethodName == "whenReady")
            "whenReady"
          else if (callerStackFrame.getFileName == "Futures.scala" && callerStackFrame.getMethodName == "isReadyWithin")
            "isReadyWithin"
          else
            "awaitResult"

        val adjustment =
          if (methodName == "whenReady")
            3
          else
            0

        if (javaFuture.isCanceled)
          throw new TestFailedException(
            sde => Some(Resources("futureWasCanceled")),
            None,
            getStackDepthFun("JavaFutures.scala", methodName, adjustment)
          )
        try {
          javaFuture.get(config.timeout.totalNanos, TimeUnit.NANOSECONDS)
        }
        catch {
          case e: java.util.concurrent.TimeoutException =>
            throw new TestFailedException(
              sde => Some(Resources("wasNeverReady")),
              None,
              getStackDepthFun("JavaFutures.scala", methodName, adjustment)
            ) with TimeoutException {
              def timeout: Span = config.timeout
            }
          case e: java.util.concurrent.ExecutionException =>
            val cause = e.getCause
            val exToReport = if (cause == null) e else cause // TODO: in 2.0 add TestCanceledException here
            if (anErrorThatShouldCauseAnAbort(exToReport) || exToReport.isInstanceOf[TestPendingException]) {
              throw exToReport
            }
            throw new TestFailedException(
              sde => Some {
                if (exToReport.getMessage == null)
                  Resources("futureReturnedAnException", exToReport.getClass.getName)
                else
                  Resources("futureReturnedAnExceptionWithMessage", exToReport.getClass.getName, exToReport.getMessage)
              },
              Some(exToReport),
              getStackDepthFun("JavaFutures.scala", methodName, adjustment)
            )
        }
      }
    }
}
