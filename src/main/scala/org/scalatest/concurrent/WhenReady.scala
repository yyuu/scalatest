package org.scalatest.concurrent

import java.util.concurrent.{Future => FutureOfJava}
import java.util.concurrent.TimeUnit
import scala.annotation.tailrec

trait WhenReady {

  def whenReady[T, U](future: FutureSoBright[T])(fun: T => U)/*(implicit config: EventuallyConfig)*/ {
   
    @tailrec
    def tryTryAgain {
      future.value match {
        case Some(Right(v)) => () // fun(v)
        case Some(Left(e)) => () // throw e? or wrap it in a TFE?
        case None => tryTryAgain
      }
    }
    
    tryTryAgain 
    /*
    val startMillis = System.currentTimeMillis
    def makeAValiantAttempt(): Either[Throwable, T] = {
      try {
        Right(fun)
      }
      catch {
        case tpe: TestPendingException => throw tpe
        case e: Throwable if !anErrorThatShouldCauseAnAbort(e) => Left(e)
      }
    }

    @tailrec
    def tryTryAgain(attempt: Int): T = {
      val timeout = config.timeout
      val interval = config.interval
      makeAValiantAttempt() match {
        case Right(result) => result
        case Left(e) => 
          val duration = System.currentTimeMillis - startMillis
          if (duration < timeout)
            Thread.sleep(interval)
          else {
            def msg =
              if (e.getMessage == null)
                Resources("didNotEventuallySucceed", attempt.toString, interval.toString)
              else
                Resources("didNotEventuallySucceedBecause", attempt.toString, interval.toString, e.getMessage)
            throw new TestFailedException(
              sde => Some(msg),
              Some(e),
              getStackDepthFun("Eventually.scala", "eventually")
            )
          }

          tryTryAgain(attempt + 1)
      }
    }
    tryTryAgain(1) */
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