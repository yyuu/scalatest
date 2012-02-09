package org.scalatest.concurrent

import org.scalatest._
import org.scalatest.StackDepthExceptionHelper.getStackDepthFun
import java.util.concurrent.{Future => FutureOfJava}
import java.util.concurrent.TimeUnit
import org.scalatest.Suite.anErrorThatShouldCauseAnAbort
import scala.annotation.tailrec

trait WhenReady extends RetryConfiguration {

  def whenReady[T, U](future: FutureSoBright[T], timeout: Timeout, interval: Interval)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(timeout.value, interval.value))

  def whenReady[T, U](future: FutureSoBright[T], interval: Interval, timeout: Timeout)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(timeout.value, interval.value))

  def whenReady[T, U](future: FutureSoBright[T], timeout: Timeout)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(timeout.value, config.interval))

  def whenReady[T, U](future: FutureSoBright[T], interval: Interval)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(config.timeout, interval.value))

  def whenReady[T, U](future: FutureSoBright[T])(fun: T => U)(implicit config: RetryConfig): U = {

    val startMillis = System.currentTimeMillis

    @tailrec
    def tryTryAgain(attempt: Int): U = {
      val timeout = config.timeout
      val interval = config.interval
      if (future.isCanceled)
        throw new TestFailedException(
          sde => Some(Resources("futureWasCanceled", attempt.toString, interval.toString)),
          None,
          getStackDepthFun("WhenReady.scala", "whenReady")
        )
      if (future.isExpired)
        throw new TestFailedException(
          sde => Some(Resources("futureExpired", attempt.toString, interval.toString)),
          None,
          getStackDepthFun("WhenReady.scala", "whenReady")
        )
      future.value match {
        case Some(Right(v)) => fun(v)
        case Some(Left(tpe: TestPendingException)) => throw tpe // TODO: In 2.0 add TestCanceledException here
        case Some(Left(e)) if anErrorThatShouldCauseAnAbort(e) => throw e
        case Some(Left(e)) =>
          val hasMessage = e.getMessage != null
          throw new TestFailedException(
            sde => Some {
              if (e.getMessage == null)
                Resources("futureReturnedAnException", e.getClass.getName)
              else
                Resources("futureReturnedAnExceptionWithMessage", e.getClass.getName, e.getMessage)
            },
            Some(e),
            getStackDepthFun("WhenReady.scala", "whenReady")
          )
        case None => 
          val duration = System.currentTimeMillis - startMillis
          if (duration < timeout)
            Thread.sleep(interval)
          else {
            throw new TestFailedException(
              sde => Some(Resources("wasNeverReady", attempt.toString, interval.toString)),
              None,
              getStackDepthFun("WhenReady.scala", "whenReady")
            )
          }

          tryTryAgain(attempt + 1)
      }
    }
    tryTryAgain(1)
  }
  
  implicit def convertFutureOfJava[T](futureOfJava: FutureOfJava[T]) =
    new FutureSoBright[T] {
      def value: Option[Either[Throwable, T]] = 
        if (futureOfJava.isDone())
          Some(Right(futureOfJava.get))
        else
          None
      def isExpired: Boolean = false // Java Futures don't support the notion of a timeout
      def isCanceled: Boolean = futureOfJava.isCancelled // Two ll's in Canceled. The verbosity of Java strikes again!
    } 
}

object WhenReady extends WhenReady