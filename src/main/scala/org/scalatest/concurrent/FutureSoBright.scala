package org.scalatest.concurrent

trait FutureSoBright[T] {

  /**
   * Returns either an exception to throw, or a value or None, if it isn't ready.
   */
  def value: Option[Either[Throwable, T]]
  def isExpired: Boolean
  def isDropped: Boolean
}
