package com.artima.demo
import scala.collection.mutable.ListBuffer

class Stack[T] {

  val MAX = 10
  private var buf = new ListBuffer[T]

  def push(o: T) {
    if (!isFull)
      o +: buf
    else
      throw new IllegalStateException("can't push onto a full stack")
  }

  def pop(): T = {
    if (!isEmpty)
      buf.remove(0)
    else
      throw new IllegalStateException("can't pop an empty stack")
  }

  def peek: T = {
    if (!isEmpty)
      buf(0)
    else
      throw new IllegalStateException("can't pop an empty stack")
  }

  def isFull: Boolean = buf.size == MAX
  def isEmpty: Boolean = buf.size == 0
  def size = buf.size
}
