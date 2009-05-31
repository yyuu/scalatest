package com.artima.demo

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class StackShouldSpec extends Spec with ShouldMatchers {

  def emptyStack = new Stack[Int]
  def fullStack = {
    val stack = new Stack[Int]
    for (i <- 0 until stack.MAX)
      stack.push(i)
    stack
  }
  val lastValuePushed = 9

  describe("A Stack") {

    describe("(when empty)") {

      it("should be empty") {
        emptyStack should be ('empty)
      }

      it("should have size zero") {
        emptyStack.size should equal (0)
      }

      it("should throw IllegalStateException on a peek") {
        intercept[IllegalStateException] {
          emptyStack.peek
        }
      }

      it("should throw IllegalStateException on a pop") {
        intercept[IllegalStateException] {
          emptyStack.pop()
        }
      }

      it("should not be full") {
        emptyStack should not { be ('full) }
      }

      it("should work properly when an element is pushed") {
        val stack = emptyStack
        stack.push(7)
        stack.size should equal (1)
        stack.peek should equal (7)
        stack should not { be ('empty) }
        stack should not { be ('full) }
      }
    }

    describe("(when full)") {

      it("should be full") {
        fullStack should be ('full)
      }

      it("should not be empty") {
        fullStack should not { be ('empty) }
      }

      it("should return the top item on peek") {
        fullStack.peek should equal (lastValuePushed)
      }

      it("should not remove the top item on peek") {
        val stack = fullStack
        val size = stack.size
        stack.peek should equal (lastValuePushed)
        stack.size should equal (size)
      }

      it("should remove the top item on pop") {
        val stack = fullStack
        val size = stack.size
        stack.pop() should equal (lastValuePushed)
        stack.size should equal (size - 1)
      }
    }
  }
}