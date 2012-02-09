package org.scalatest.concurrent

trait FutureSoBright[T] {

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
}
