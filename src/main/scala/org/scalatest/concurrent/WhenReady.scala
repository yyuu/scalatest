package org.scalatest.concurrent

import org.scalatest._
import org.scalatest.StackDepthExceptionHelper.getStackDepthFun
import java.util.concurrent.{Future => FutureOfJava}
import java.util.concurrent.TimeUnit
import scala.annotation.tailrec

trait WhenReady extends RetryConfiguration {

  def whenReady[T, U](future: FutureSoBright[T])(fun: T => U)(implicit config: RetryConfig) {

    val startMillis = System.currentTimeMillis

    @tailrec
    def tryTryAgain(attempt: Int): U = {
      val timeout = config.timeout
      val interval = config.interval
      future.value match {
        case Some(Right(v)) => fun(v)
        case Some(Left(e)) => throw e // throw e? or wrap it in a TFE?
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