package org.scalatest.path

import scala.collection.mutable.ListBuffer

class Stack[T] {

  val MAX = 10
  private val buf = new ListBuffer[T]

  def push(o: T) {
    if (!full)
      buf.prepend(o)
    else
      throw new IllegalStateException("can't push onto a full stack")
  }

  def pop(): T = {
    if (!empty)
      buf.remove(0)
    else
      throw new IllegalStateException("can't pop an empty stack")
  }

  def peek: T = {
    if (!empty)
      buf(0)
    else
      throw new IllegalStateException("can't pop an empty stack")
  }

  def full: Boolean = buf.size == MAX
  def empty: Boolean = buf.size == 0
  def size = buf.size

  override def toString = buf.mkString("Stack(", ", ", ")")
}

class StackSpec extends org.scalatest.path.FunSpec {

  import StackSpec._
    
  instanceCount += 1 

  val lastValuePushed = 9

  describe("A Stack") {
    
    val stack = new Stack[Int]
    
    describe("(when empty)") {

      it("should be empty") {
        firstTestCount += 1
        assert(stack.empty)
      }

      it("should complain on peek") {
        secondTestCount += 1
        intercept[IllegalStateException] {
          stack.peek
        }
      }

      it("should complain on pop") {
        thirdTestCount += 1
        intercept[IllegalStateException] {
          stack.pop
        }
      }
    }
  
    describe("(with one item)") {
      
      // val sizeOne = stack.size
      stack.push(9)
      // val sizeTwo = stack.size
      // val outerStack = stack
      
      it("should be non-empty, DUDE!") {
        // val size = stack.size
        // val stacksAreSame = stack == outerStack
        fourthTestCount += 1
        assert(!stack.empty)
      }

      it should behave like nonEmptyStack
      it should behave like nonFullStack
    }

    describe("(with one item less than capacity)") {
      
      for (i <- 1 to 9)
        stack.push(i)

      it should behave like nonEmptyStack
      it should behave like nonFullStack
    }

    describe("(full)") {
      val size = stack.size
/*
      val zero = stack.pop
      val one = stack.pop
      val two = stack.pop
      val three = stack.pop
      val four = stack.pop
      val five = stack.pop
      val six = stack.pop
      val seven = stack.pop
      val eight = stack.pop
      val nine = stack.pop
  */    
      for (i <- 0 until stack.MAX)
        stack.push(i)

      it("should be full") {
        fifthTestCount += 1
        assert(stack.full)
      }

      it should behave like nonEmptyStack

      it("should complain on a push") {
        sixthTestCount += 1
        intercept[IllegalStateException] {
          stack.push(10)
        }
      }
    }
    
    def nonEmptyStack {

      it("should be non-empty") {
        firstSharedTestCount += 1
        assert(!stack.empty)
      }

      it("should return the top item on peek") {
        secondSharedTestCount += 1
        assert(stack.peek === lastValuePushed)
      }

      it("should not remove the top item on peek") {
        thirdSharedTestCount += 1
        val size = stack.size
        assert(stack.peek === lastValuePushed)
        assert(stack.size === size)
      }

      it("should remove the top item on pop") {
        fourthSharedTestCount += 1
        val size = stack.size
        assert(stack.pop === lastValuePushed)
        assert(stack.size === size - 1)
      }
    }

    def nonFullStack {

      it("should not be full") {
        fifthSharedTestCount += 1
        assert(!stack.full)
      }

      it("should add to the top on push") {
        sixthSharedTestCount += 1
        val size = stack.size
        stack.push(7)
        assert(stack.size === size + 1)
        assert(stack.peek === 7)
      }
    }
  }
}

object StackSpec {

  var instanceCount = 0
  var firstDescCount = 0
  var secondDescCount = 0
  var outerDescCount = 0
  var firstTestCount = 0
  var secondTestCount = 0
  var thirdTestCount = 0
  var fourthTestCount = 0
  var fifthTestCount = 0
  var sixthTestCount = 0
  var firstSharedTestCount = 0
  var secondSharedTestCount = 0
  var thirdSharedTestCount = 0
  var fourthSharedTestCount = 0
  var fifthSharedTestCount = 0
  var sixthSharedTestCount = 0
    
  def resetCounts() {
    instanceCount = 0
    firstDescCount = 0
    secondDescCount = 0
    outerDescCount = 0
    firstTestCount = 0
    secondTestCount = 0
  }
}
