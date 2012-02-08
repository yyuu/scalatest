package org.scalatest.concurrent

trait WhenReady {

  def whenReady[T](future: FutureSoBright[T])(t: T => Unit) {
    
  }
}

object WhenReady extends WhenReady