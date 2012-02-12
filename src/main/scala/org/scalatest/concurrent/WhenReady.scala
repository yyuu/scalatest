package org.scalatest.concurrent

import org.scalatest._
import org.scalatest.StackDepthExceptionHelper.getStackDepthFun
import java.util.concurrent.{Future => FutureOfJava}
import java.util.concurrent.TimeUnit
import org.scalatest.Suite.anErrorThatShouldCauseAnAbort
import scala.annotation.tailrec

/**
 * Trait that provides the <code>whenReady</code> construct, which periodically queries a passed
 * future, until it is ready or the configured timeout has been surpassed, and if ready, passes the future's
 * value to the specified function.
 *
 * <p>
 * The by-name parameter "succeeds" if it returns a result. It "fails" if it throws any exception that
 * would normally cause a test to fail. (These are any exceptions except <a href="TestPendingException"><code>TestPendingException</code></a> and
 * <code>Error</code>s listed in the
 * <a href="Suite.html#errorHandling">Treatment of <code>java.lang.Error</code>s</a> section of the
 * documentation of trait <code>Suite</code>.)
 * </p>
 *
 * <p>
 * For example, the following invocation of <code>eventually</code> would succeed (not throw an exception):
 * </p>
 *
 * <pre class="stHighlight">
 * val xs = 1 to 125
 * val it = xs.iterator
 * eventually { it.next should be (3) }
 * </pre>
 *
 * <p>
 * However, because the default timeout one second, the following invocation of
 * <code>eventually</code> would ultimately produce a <code>TestFailedException</code>:
 * </p>
 *
 * <pre class="stHighlight">
 * val xs = 1 to 125
 * val it = xs.iterator
 * eventually { Thread.sleep(999); it.next should be (110) }
 * </pre>
 *
 * <p>
 * Assuming the default configuration parameters, <code>timeout</code> 1000 milliseconds and <code>interval</code> 10 milliseconds,
 * were passed implicitly to <code>eventually</code>, the detail message of the thrown
 * <code>TestFailedException</code> would look like:
 * </p>
 *
 * <p>
 * <code>The code passed to eventually never returned normally. Attempted 2 times, sleeping 10 milliseconds between each attempt.</code>
 * </p>
 *
 * <a name="retryConfig"></a><h2>Configuration of <code>eventually</code></h2>
 *
 * <p>
 * The <code>eventually</code> methods of this trait can be flexibly configured.
 * The two configuration parameters for <code>eventually</code> along with their 
 * default values and meanings are described in the following table:
 * </p>
 *
 * <table style="border-collapse: collapse; border: 1px solid black">
 * <tr>
 * <th style="background-color: #CCCCCC; border-width: 1px; padding: 3px; text-align: center; border: 1px solid black">
 * <strong>Configuration Parameter</strong>
 * </th>
 * <th style="background-color: #CCCCCC; border-width: 1px; padding: 3px; text-align: center; border: 1px solid black">
 * <strong>Default Value</strong>
 * </th>
 * <th style="background-color: #CCCCCC; border-width: 1px; padding: 3px; text-align: center; border: 1px solid black">
 * <strong>Meaning</strong>
 * </th>
 * </tr>
 * <tr>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: center">
 * timeout
 * </td>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: center">
 * 1000
 * </td>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * the maximum amount of time in milliseconds to allow unsuccessful attempts before giving up and throwing <code>TestFailedException</code>
 * </td>
 * </tr>
 * <tr>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: center">
 * interval
 * </td>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: center">
 * 10
 * </td>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * the number of milliseconds to sleep between each attempt
 * </td>
 * </tr>
 * </table>
 *
* <p>
 * The <code>eventually</code> methods of trait <code>Eventually</code> each take an <code>RetryConfig</code>
 * object as an implicit parameter. This object provides values for the two configuration parameters. Trait
 * <code>Eventually</code> provides an implicit <code>val</code> named <code>retryConfig</code> with each
 * configuration parameter set to its default value. 
 * If you want to set one or more configuration parameters to a different value for all invocations of
 * <code>eventually</code> in a suite you can override this
 * val (or hide it, for example, if you are importing the members of the <code>Eventually</code> companion object rather
 * than mixing in the trait). For example, if
 * you always want the default <code>timeout</code> to be 2 seconds and the default <code>interval</code> to be 5 milliseconds, you
 * can override <code>retryConfig</code>, like this:
 *
 * <pre class="stHighlight">
 * implicit override val retryConfig =
 *   RetryConfig(timeout = 2000, interval = 5)
 * </pre>
 *
 * <p>
 * Or, hide it by declaring a variable of the same name in whatever scope you want the changed values to be in effect:
 * </p>
 *
 * <pre class="stHighlight">
 * implicit val retryConfig =
 *   RetryConfig(timeout = 2000, interval = 5)
 * </pre>
 *
 * <p>
 * In addition to taking a <code>RetryConfig</code> object as an implicit parameter, the <code>eventually</code> methods of trait
 * <code>Eventually</code> include overloaded forms that take one or two <code>RetryConfigParam</code>
 * objects that you can use to override the values provided by the implicit <code>RetryConfig</code> for a single <code>eventually</code>
 * invocation. For example, if you want to set <code>timeout</code> to 5000 for just one particular <code>eventually</code> invocation,
 * you can do so like this:
 * </p>
 *
 * <pre class="stHighlight">
 * eventually (timeout(5000)) { Thread.sleep(10); it.next should be (110) }
 * </pre>
 *
 * <p>
 * This invocation of <code>eventually</code> will use 5000 for <code>timeout</code> and whatever value is specified by the 
 * implicitly passed <code>RetryConfig</code> object for the <code>interval</code> configuration parameter.
 * If you want to set both configuration parameters in this way, just list them separated by commas:
 * </p>
 * 
 * <pre class="stHighlight">
 * eventually (timeout(5000), interval(5)) { it.next should be (110) }
 * </pre>
 *
 * @author Bill Venners
 */
trait WhenReady extends RetryConfiguration {

  def whenReady[T, U](future: FutureConcept[T], timeout: Timeout, interval: Interval)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(timeout.value, interval.value))

  def whenReady[T, U](future: FutureConcept[T], interval: Interval, timeout: Timeout)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(timeout.value, interval.value))

  def whenReady[T, U](future: FutureConcept[T], timeout: Timeout)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(timeout.value, config.interval))

  def whenReady[T, U](future: FutureConcept[T], interval: Interval)(fun: T => U)(implicit config: RetryConfig): U =
    whenReady(future)(fun)(RetryConfig(config.timeout, interval.value))

  def whenReady[T, U](future: FutureConcept[T])(fun: T => U)(implicit config: RetryConfig): U = {

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
    new FutureConcept[T] {
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
