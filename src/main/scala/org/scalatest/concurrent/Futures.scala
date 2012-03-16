/*
 * Copyright 2001-2012 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.concurrent

import org.scalatest._
import org.scalatest.StackDepthExceptionHelper.getStackDepthFun
import org.scalatest.Suite.anErrorThatShouldCauseAnAbort
import scala.annotation.tailrec
import org.scalatest.time.Span

/**
 * Trait that provides the <code>whenReady</code> construct, which periodically queries the passed
 * future, until it is ready or the configured timeout has been surpassed, and when ready, passes the future's
 * value to the specified function.
 *
 * <p>
 * To make <code>whenReady</code> more broadly applicable, the type of future it accepts is a <code>FutureConcept[T]</code>,
 * where <code>T</code> is the type of value promised by the future. Passing a future to <code>whenReady</code> requires
 * an implicit conversion from the type of future you wish to pass (the <em>modeled type</em>) to
 * <code>FutureConcept[T]</code>. Subtrait <code>JavaFutures</code> provides an implicit conversion from
 * <code>java.util.concurrent.Future[T]</code> to <code>FutureConcept[T]</code>.
 * </p>
 *
 * <p>
 * For example, the following invocation of <code>whenReady</code> would succeed (not throw an exception):
 * </p>
 *
 * <pre class="stHighlight">
 * import org.scalatest._
 * import matchers.ShouldMatchers._
 * import concurrent.Futures._
 * import java.util.concurrent._
 * 
 * val exec = Executors.newSingleThreadExecutor
 * val task = new Callable[String] { def call() = { Thread.sleep(500); "hi" } }
 * whenReady(exec.submit(task)) { s =&gt;
 *   s should be ("hi")
 * }
 * </pre>
 *
 * <p>
 * However, because the default timeout is one second, the following invocation of
 * <code>whenReady</code> would ultimately produce a <code>TestFailedException</code>:
 * </p>
 *
 * <pre class="stHighlight">
 * val task = new Callable[String] { def call() = { Thread.sleep(5000); "hi" } }
 * whenReady(exec.submit(task)) { s =&gt;
 *   s should be ("hi")
 * }
 * </pre>
 *
 * <p>
 * Assuming the default configuration parameters, a <code>timeout</code> of 1 second and an
 * <code>interval</code> of 10 milliseconds,
 * were passed implicitly to <code>whenReady</code>, the detail message of the thrown
 * <code>TestFailedException</code> would look like:
 * </p>
 *
 * <p>
 * <code>The future passed to whenReady was never ready, so whenReady timed out. Queried 95 times, sleeping 10 milliseconds between each query.</code>
 * </p>
 *
 * <a name="retryConfig"></a><h2>Configuration of <code>whenReady</code></h2>
 *
 * <p>
 * The <code>whenReady</code> methods of this trait can be flexibly configured.
 * The two configuration parameters for <code>whenReady</code> along with their 
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
 * 1 second
 * </td>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * the maximum amount of time to allow unsuccessful queries before giving up and throwing <code>TestFailedException</code>
 * </td>
 * </tr>
 * <tr>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: center">
 * interval
 * </td>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: center">
 * 10 milliseconds
 * </td>
 * <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * the amount of time to sleep between each query
 * </td>
 * </tr>
 * </table>
 *
* <p>
 * The <code>whenReady</code> methods of trait <code>Futures</code> each take an <code>TimeoutConfig</code>
 * object as an implicit parameter. This object provides values for the two configuration parameters. Trait
 * <code>Futures</code> provides an implicit <code>val</code> named <code>retryConfig</code> with each
 * configuration parameter set to its default value. 
 * If you want to set one or more configuration parameters to a different value for all invocations of
 * <code>whenReady</code> in a suite you can override this
 * val (or hide it, for example, if you are importing the members of the <code>Futures</code> companion object rather
 * than mixing in the trait). For example, if
 * you always want the default <code>timeout</code> to be 2 seconds and the default <code>interval</code> to be 5 milliseconds, you
 * can override <code>retryConfig</code>, like this:
 *
 * <pre class="stHighlight">
 * implicit override val retryConfig =
 *   TimeoutConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))
 * </pre>
 *
 * <p>
 * Or, hide it by declaring a variable of the same name in whatever scope you want the changed values to be in effect:
 * </p>
 *
 * <pre class="stHighlight">
 * implicit val retryConfig =
 *   TimeoutConfig(timeout =  Span(2, Seconds), interval = Span(5, Millis))
 * </pre>
 *
 * <p>
 * In addition to taking a <code>TimeoutConfig</code> object as an implicit parameter, the <code>whenReady</code> methods of trait
 * <code>Futures</code> include overloaded forms that take one or two <code>RetryConfigParam</code>
 * objects that you can use to override the values provided by the implicit <code>TimeoutConfig</code> for a single <code>whenReady</code>
 * invocation. For example, if you want to set <code>timeout</code> to 5000 for just one particular <code>whenReady</code> invocation,
 * you can do so like this:
 * </p>
 *
 * <pre class="stHighlight">
 * whenReady (exec.submit(task), timeout(Span(6, Seconds))) { s =&gt;
 *   s should be ("hi")
 * }
 * </pre>
 *
 * <p>
 * This invocation of <code>eventually</code> will use 6000 for <code>timeout</code> and whatever value is specified by the 
 * implicitly passed <code>TimeoutConfig</code> object for the <code>interval</code> configuration parameter.
 * If you want to set both configuration parameters in this way, just list them separated by commas:
 * </p>
 * 
 * <pre class="stHighlight">
 * whenReady (exec.submit(task), timeout(Span(6, Seconds)), interval(Span(500, Millis))) { s =&gt;
 *   s should be ("hi")
 * }
 * </pre>
 *
 * <p>
 * You can also import or mix in the members of <a href="../time/SpanSugar.html"><code>SpanSugar</code></a> if
 * you want a more concise DSL for expressing time spans:
 * </p>
 *
 * <pre class="stHighlight">
 * whenReady (exec.submit(task), timeout(6 seconds), interval(500 millis)) { s =&gt;
 *   s should be ("hi")
 * }
 * </pre>
 *
 * <p>
 * <em>Note: The <code>whenReady</code> construct was in part inspired by the <code>whenDelivered</code> matcher of the 
 * <a href="http://github.com/jdegoes/blueeyes" target="_blank">BlueEyes</a> project, a lightweight, asynchronous web framework for Scala.</em>
 * </p>
 *
 * @author Bill Venners
 */
trait Futures extends TimeoutConfiguration {

  /**
   * Concept trait for futures, instances of which are passed to the <code>whenReady</code>
   * methods of trait <code>Futures</code>.
   *
   * @author Bill Venners
   */
  trait FutureConcept[T] { thisFuture =>

    /**
     * Queries this future for its value.
     *
     * <p>
     * If the future is not ready, this method will return <code>None</code>. If ready, it will either return an exception
     * or a <code>T</code>.
     * </p>
     */
    def value: Option[Either[Throwable, T]]

    /**
     * Indicates whether this future has expired (timed out).
     *
     * <p>
     * The timeout detected by this method is different from the timeout supported by <code>whenReady</code>. This timeout
     * is a timeout of the underlying future. If the underlying future does not support timeouts, this method must always
     * return <code>false</code>.
     * </p>
     */
    def isExpired: Boolean

    /**
     * Indicates whether this future has been canceled.
     *
     * <p>
     * If the underlying future does not support the concept of cancellation, this method must always return <code>false</code>.
     * </p>
     */
    def isCanceled: Boolean
  /*
    def isReadyWithin(span: Span): Boolean = {
      awaitAtMost(span)
      true
    }
  */

    /**
     * Returns the result of this <code>FutureConcept</code>, once it is ready, or throws either the
     * exception returned by the future (<em>i.e.</em>, <code>value</code> returned a <code>Left</code>)
     * or <code>TestFailedException</code>.
     *
     * <p>
     * The maximum amount of time to wait for the future to become ready before giving up and throwing
     * <code>TestFailedException</code> is configured by the value contained in the passed
     * <code>timeout</code> parameter.
     * The interval to sleep between queries of the future (used only if the future is polled) is configured by the value contained in the passed
     * <code>interval</code> parameter.
     * </p>
     *
     * <p>
     * This method invokes the overloaded <code>awaitResult</code> form with only one (implicit) argument
     * list that contains only one argument, a <code>TimeoutConfig</code>, passing a new
     * <code>TimeoutConfig</code> with the <code>Timeout</code> specified as <code>timeout</code> and
     * the <code>Interval</code> specified as <code>interval</code>.
     * </p>
     *
     * @param timeout the <code>Timeout</code> configuration parameter
     * @param interval the <code>Interval</code> configuration parameter
     * @return the result of the future once it is ready, if <code>value</code> is defined as a <code>Right</code>
     * @throws Throwable if once ready, the <code>value</code> of this future is defined as a
     *       <code>Left</code> (in this case, this method throws that same exception)
     * @throws TestFailedException if the future is cancelled, expires, or is still not ready after
     *     the specified timeout has been exceeded
     */
    final def awaitResult(timeout: Timeout, interval: Interval): T =
      awaitResult(TimeoutConfig(timeout.value, interval.value))

    /**
     * Returns the result of this <code>FutureConcept</code>, once it is ready, or throws either the
     * exception returned by the future (<em>i.e.</em>, <code>value</code> returned a <code>Left</code>)
     * or <code>TestFailedException</code>.
     *
     * <p>
     * The maximum amount of time to wait for the future to become ready before giving up and throwing
     * <code>TestFailedException</code> is configured by the value contained in the passed
     * <code>timeout</code> parameter.
     * The interval to sleep between queries of the future (used only if the future is polled) is configured by the value contained in the passed
     * <code>interval</code> parameter.
     * </p>
     *
     * <p>
     * This method invokes the overloaded <code>awaitResult</code> form with only one (implicit) argument
     * list that contains only one argument, a <code>TimeoutConfig</code>, passing a new
     * <code>TimeoutConfig</code> with the <code>Timeout</code> specified as <code>timeout</code> and
     * the <code>Interval</code> specified as <code>interval</code>.
     * </p>
     *
     * @param interval the <code>Interval</code> configuration parameter
     * @param timeout the <code>Timeout</code> configuration parameter
     * @return the result of the future once it is ready, if <code>value</code> is defined as a <code>Right</code>
     * @throws Throwable if once ready, the <code>value</code> of this future is defined as a
     *       <code>Left</code> (in this case, this method throws that same exception)
     * @throws TestFailedException if the future is cancelled, expires, or is still not ready after
     *     the specified timeout has been exceeded
     */
    final def awaitResult(interval: Interval, timeout: Timeout): T =
      awaitResult(TimeoutConfig(timeout.value, interval.value))

    /**
     * Returns the result of this <code>FutureConcept</code>, once it is ready, or throws either the
     * exception returned by the future (<em>i.e.</em>, <code>value</code> returned a <code>Left</code>)
     * or <code>TestFailedException</code>.
     *
     * <p>
     * The maximum amount of time to wait for the future to become ready before giving up and throwing
     * <code>TestFailedException</code> is configured by the value contained in the passed
     * <code>timeout</code> parameter.
     * The interval to sleep between queries of the future (used only if the future is polled) is configured by the <code>interval</code> field of
     * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
     * </p>
     *
     * <p>
     * This method invokes the overloaded <code>awaitResult</code> form with only one (implicit) argument
     * list that contains only one argument, a <code>TimeoutConfig</code>, passing a new
     * <code>TimeoutConfig</code> with the <code>Timeout</code> specified as <code>timeout</code> and
     * the <code>Interval</code> specified as <code>config.interval</code>.
     * </p>
     *
     * @param timeout the <code>Timeout</code> configuration parameter
     * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
     *          <code>interval</code> parameters that are unused by this method
     * @return the result of the future once it is ready, if <code>value</code> is defined as a <code>Right</code>
     * @throws Throwable if once ready, the <code>value</code> of this future is defined as a
     *       <code>Left</code> (in this case, this method throws that same exception)
     * @throws TestFailedException if the future is cancelled, expires, or is still not ready after
     *     the specified timeout has been exceeded
     */
    final def awaitResult(timeout: Timeout)(implicit config: TimeoutConfig): T =
      awaitResult(TimeoutConfig(timeout.value, config.interval))

    /**
     * Returns the result of this <code>FutureConcept</code>, once it is ready, or throws either the
     * exception returned by the future (<em>i.e.</em>, <code>value</code> returned a <code>Left</code>)
     * or <code>TestFailedException</code>.
     *
     * <p>
     * The maximum amount of time to wait for the future to become ready before giving up and throwing
     * <code>TestFailedException</code> is configured by the <code>timeout</code> field of
     * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
     * The interval to sleep between queries of the future (used only if the future is polled) is configured by the value contained in the passed
     * <code>interval</code> parameter.
     * </p>
     *
     * <p>
     * This method invokes the overloaded <code>awaitResult</code> form with only one (implicit) argument
     * list that contains only one argument, a <code>TimeoutConfig</code>, passing a new
     * <code>TimeoutConfig</code> with the <code>Interval</code> specified as <code>interval</code> and
     * the <code>Timeout</code> specified as <code>config.timeout</code>.
     * </p>
     *
     * @param interval the <code>Interval</code> configuration parameter
     * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
     *          <code>interval</code> parameters that are unused by this method
     * @return the result of the future once it is ready, if <code>value</code> is defined as a <code>Right</code>
     * @throws Throwable if once ready, the <code>value</code> of this future is defined as a
     *       <code>Left</code> (in this case, this method throws that same exception)
     * @throws TestFailedException if the future is cancelled, expires, or is still not ready after
     *     the specified timeout has been exceeded
     */
    final def awaitResult(interval: Interval)(implicit config: TimeoutConfig): T =
      awaitResult(TimeoutConfig(config.timeout, interval.value))

    /**
     * Returns the result of this <code>FutureConcept</code>, once it is ready, or throws either the
     * exception returned by the future (<em>i.e.</em>, <code>value</code> returned a <code>Left</code>)
     * or <code>TestFailedException</code>.
     *
     * <p>
     * This trait's implementation of this method queries the future repeatedly until it either is
     * ready, or a configured maximum amount of time has passed, sleeping a configured interval between
     * attempts; and when ready, returns the future's value. For greater efficiency, implementations of
     * this trait may override this method so that it blocks the specified timeout while waiting for
     * the result, if the underlying future supports this.
     * </p>
     *
     * <p>
     * The maximum amount of time to wait for the future to become ready before giving up and throwing
     * <code>TestFailedException</code> is configured by the <code>timeout</code> field of
     * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
     * The interval to sleep between queries of the future (used only if the future is polled) is configured by the <code>interval</code> field of
     * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
     * </p>
     *
     * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
     *          <code>interval</code> parameters that are unused by this method
     * @return the result of the future once it is ready, if <code>value</code> is defined as a <code>Right</code>
     * @throws Throwable if once ready, the <code>value</code> of this future is defined as a
     *       <code>Left</code> (in this case, this method throws that same exception)
     * @throws TestFailedException if the future is cancelled, expires, or is still not ready after
     *     the specified timeout has been exceeded
     */
    def awaitResult(implicit config: TimeoutConfig): T = {

      val methodName = "awaitResult" // Kludge

      val startNanos = System.nanoTime

      @tailrec
      def tryTryAgain(attempt: Int): T = {
        val timeout = config.timeout
        val interval = config.interval
        if (thisFuture.isCanceled)
          throw new TestFailedException(
            sde => Some(Resources("futureWasCanceled")),
            None,
            getStackDepthFun("Futures.scala", methodName)
          )
        if (thisFuture.isExpired)
          throw new TestFailedException(
            sde => Some(Resources("futureExpired", attempt.toString, interval.prettyString)),
            None,
            getStackDepthFun("Futures.scala", methodName)
          )
        thisFuture.value match {
          case Some(Right(v)) => v
          case Some(Left(tpe: TestPendingException)) => throw tpe // TODO: In 2.0 add TestCanceledException here
          case Some(Left(e)) if anErrorThatShouldCauseAnAbort(e) => throw e
          case Some(Left(e)) =>
            throw new TestFailedException(
              sde => Some {
                if (e.getMessage == null)
                  Resources("futureReturnedAnException", e.getClass.getName)
                else
                  Resources("futureReturnedAnExceptionWithMessage", e.getClass.getName, e.getMessage)
              },
              Some(e),
              getStackDepthFun("Futures.scala", methodName)
            )
          case None =>
            val duration = System.nanoTime - startNanos
            if (duration < timeout.totalNanos)
              Thread.sleep(interval.millisPart, interval.nanosPart)
            else {
              throw new TestFailedException(  // Shouldn't this mix in TimeoutException?
                sde => Some(Resources("wasNeverReady")),
                None,
                getStackDepthFun("Futures.scala", methodName)
              )
            }

            tryTryAgain(attempt + 1)
        }
      }
      tryTryAgain(1)
    }
  }

  /**
   * Queries the passed future repeatedly until it either is ready, or a configured maximum
   * amount of time has passed, sleeping a configured interval between attempts; and when ready, passes the future's value
   * to the passed function.
   *
   * <p>
   * The maximum amount of time to tolerate unsuccessful queries before giving up and throwing
   * <code>TestFailedException</code> is configured by the value contained in the passed
   * <code>timeout</code> parameter.
   * The interval to sleep between attempts is configured by the value contained in the passed
   * <code>interval</code> parameter.
   * </p>
   *
   * @param future the future to query
   * @param timeout the <code>Timeout</code> configuration parameter
   * @param interval the <code>Interval</code> configuration parameter
   * @param fun the function to which pass the future's value when it is ready
   * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
   *          <code>interval</code> parameters that are unused by this method
   * @return the result of invoking the <code>fun</code> parameter
   */
  final def whenReady[T, U](future: FutureConcept[T], timeout: Timeout, interval: Interval)(fun: T => U)(implicit config: TimeoutConfig): U = {
    val result = future.awaitResult(TimeoutConfig(timeout.value, interval.value))
    fun(result)
  }
    // whenReady(future)(fun)(TimeoutConfig(timeout.value, interval.value))

  /**
   * Queries the passed future repeatedly until it either is ready, or a configured maximum
   * amount of time has passed, sleeping a configured interval between attempts; and when ready, passes the future's value
   * to the passed function.
   *
   * <p>
   * The maximum amount of time to tolerate unsuccessful queries before giving up and throwing
   * <code>TestFailedException</code> is configured by the value contained in the passed
   * <code>timeout</code> parameter.
   * The interval to sleep between attempts is configured by the value contained in the passed
   * <code>interval</code> parameter.
   * </p>
   *
   * @param future the future to query
   * @param interval the <code>Interval</code> configuration parameter
   * @param timeout the <code>Timeout</code> configuration parameter
   * @param fun the function to which pass the future's value when it is ready
   * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
   *          <code>interval</code> parameters that are unused by this method
   * @return the result of invoking the <code>fun</code> parameter
   */
  final def whenReady[T, U](future: FutureConcept[T], interval: Interval, timeout: Timeout)(fun: T => U)(implicit config: TimeoutConfig): U = {
    val result = future.awaitResult(TimeoutConfig(timeout.value, interval.value))
    fun(result)
  }
    // whenReady(future)(fun)(TimeoutConfig(timeout.value, interval.value))

  /**
   * Queries the passed future repeatedly until it either is ready, or a configured maximum
   * amount of time has passed, sleeping a configured interval between attempts; and when ready, passes the future's value
   * to the passed function.
   *
   * <p>
   * The maximum amount of time in milliseconds to tolerate unsuccessful queries before giving up and throwing
   * <code>TestFailedException</code> is configured by the value contained in the passed
   * <code>timeout</code> parameter.
   * The interval to sleep between attempts is configured by the <code>interval</code> field of
   * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
   * </p>
   *
   * @param future the future to query
   * @param timeout the <code>Timeout</code> configuration parameter
   * @param fun the function to which pass the future's value when it is ready
   * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
   *          <code>interval</code> parameters that are unused by this method
   * @return the result of invoking the <code>fun</code> parameter
   */
  final def whenReady[T, U](future: FutureConcept[T], timeout: Timeout)(fun: T => U)(implicit config: TimeoutConfig): U = {
    val result = future.awaitResult(TimeoutConfig(timeout.value, config.interval))
    fun(result)
  }
    // whenReady(future)(fun)(TimeoutConfig(timeout.value, config.interval))

  /**
   * Queries the passed future repeatedly until it either is ready, or a configured maximum
   * amount of time has passed, sleeping a configured interval between attempts; and when ready, passes the future's value
   * to the passed function.
   *
   * <p>
   * The maximum amount of time in milliseconds to tolerate unsuccessful attempts before giving up is configured by the <code>timeout</code> field of
   * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
   * The interval to sleep between attempts is configured by the value contained in the passed
   * <code>interval</code> parameter.
   * </p>
   *
   * @param future the future to query
   * @param interval the <code>Interval</code> configuration parameter
   * @param fun the function to which pass the future's value when it is ready
   * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
   *          <code>interval</code> parameters that are unused by this method
   * @return the result of invoking the <code>fun</code> parameter
   */
  final def whenReady[T, U](future: FutureConcept[T], interval: Interval)(fun: T => U)(implicit config: TimeoutConfig): U = {
    val result = future.awaitResult(TimeoutConfig(config.timeout, interval.value))
    fun(result)
  }
    // whenReady(future)(fun)(TimeoutConfig(config.timeout, interval.value))

  /**
   * Queries the passed future repeatedly until it either is ready, or a configured maximum
   * amount of time has passed, sleeping a configured interval between attempts; and when ready, passes the future's value
   * to the passed function.
   *
   * <p>
   * The maximum amount of time in milliseconds to tolerate unsuccessful attempts before giving up is configured by the <code>timeout</code> field of
   * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
   * The interval to sleep between attempts is configured by the <code>interval</code> field of
   * the <code>TimeoutConfig</code> passed implicitly as the last parameter.
   * </p>
   *
   *
   * @param future the future to query
   * @param fun the function to which pass the future's value when it is ready
   * @param config an <code>TimeoutConfig</code> object containing <code>timeout</code> and
   *          <code>interval</code> parameters that are unused by this method
   * @return the result of invoking the <code>fun</code> parameter
   */
  final def whenReady[T, U](future: FutureConcept[T])(fun: T => U)(implicit config: TimeoutConfig): U = {

      val result = future.awaitResult(config)
      fun(result)
/*    val startNanos = System.nanoTime

    @tailrec
    def tryTryAgain(attempt: Int): U = {
      val timeout = config.timeout
      val interval = config.interval
      if (future.isCanceled)
        throw new TestFailedException(
          sde => Some(Resources("futureWasCanceled")),
          None,
          getStackDepthFun("Futures.scala", "whenReady")
        )
      if (future.isExpired)
        throw new TestFailedException(
          sde => Some(Resources("futureExpired", attempt.toString, interval.prettyString)),
          None,
          getStackDepthFun("Futures.scala", "whenReady")
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
            getStackDepthFun("Futures.scala", "whenReady")
          )
        case None => 
          val duration = System.nanoTime - startNanos
          if (duration < timeout.totalNanos)
            Thread.sleep(interval.millisPart, interval.nanosPart)
          else {
            throw new TestFailedException(
              sde => Some(Resources("wasNeverReady", attempt.toString, interval.prettyString)),
              None,
              getStackDepthFun("Futures.scala", "whenReady")
            )
          }

          tryTryAgain(attempt + 1)
      }
    }
    tryTryAgain(1)  */
  }
}

/**
 * Companion object that facilitates the importing of <code>Futures</code> members as
 * an alternative to mixing in the trait. One use case is to import <code>Futures</code>'s members so you can use
 * them in the Scala interpreter:
 *
 * <pre class="stREPL">
 * $ scala -cp scalatest-1.8.jar
 * Welcome to Scala version 2.9.1.final (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_29).
 * Type in expressions to have them evaluated.
 * Type :help for more information.
 *
 * scala&gt; import org.scalatest._
 * import org.scalatest._
 *
 * scala&gt; import matchers.ShouldMatchers._
 * import matchers.ShouldMatchers._
 *
 * scala&gt; import concurrent.Futures._
 * import concurrent.Futures._
 *
 * scala&gt; import java.util.concurrent._
 * import java.util.concurrent._
 *
 * scala&gt; val exec = Executors.newSingleThreadExecutor
 * newSingleThreadExecutor   
 * 
 * scala&gt; val task = new Callable[String] { def call() = { Thread.sleep(500); "hi" } }
 * task: java.lang.Object with java.util.concurrent.Callable[String] = $anon$1@e1a973
 * 
 * scala&gt; whenReady(exec.submit(task)) { s =&gt; s shouldBe "hi" }
 * 
 * scala&gt; val task = new Callable[String] { def call() = { Thread.sleep(5000); "hi" } }
 * task: java.lang.Object with java.util.concurrent.Callable[String] = $anon$1@2993dfb0
 * 
 * scala&gt; whenReady(exec.submit(task)) { s =&gt; s shouldBe "hi" }
 * org.scalatest.TestFailedException: The future passed to whenReady was never ready, so whenReady timed out. Queried 95 times, sleeping 10 milliseconds between each query.
 *   at org.scalatest.concurrent.Futures$class.tryTryAgain$1(Futures.scala03)
 *   ...
 * </pre>
 */
object Futures extends Futures
