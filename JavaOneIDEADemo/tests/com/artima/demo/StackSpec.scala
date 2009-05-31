package com.artima.demo
import org.scalatest.Spec

/**
 * Created by IntelliJ IDEA.
 * User: bv
 * Date: Dec 9, 2008
 * Time: 6:58:43 PM
 * To change this template use File | Settings | File Templates.
 */

class StackSpec extends Spec {

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
        assert(emptyStack.isEmpty)
      }

      it("should have size zero") {
        assert(emptyStack.size === 0)
      }

      it("should throw IllegalStateException on a peek") {
        intercept[IllegalStateException] {
          emptyStack.peek
        }
      }

      it("should throw IllegalStateException on a pop") {
        intercept[IllegalStateException] {
          emptyStack.pop
        }
      }

      it("should not be full") {
        assert(!emptyStack.isFull)
      }

      it("should work properly when an element is pushed") {
        val stack = emptyStack
        stack.push(7)
        assert(stack.size === 1)
        assert(stack.peek === 7)
        assert(!stack.isEmpty)
        assert(!stack.isFull)
      }
    }

    describe("(when full)") {

      it("should be full") {
        assert(fullStack.isFull)
      }

      it("should not be empty") {
        assert(!fullStack.isEmpty)
      }

      it("should return the top item on peek") {
        assert(fullStack.peek === lastValuePushed)
      }

      it("should not remove the top item on peek") {
        val stack = fullStack
        val size = stack.size
        assert(stack.peek === lastValuePushed)
        assert(stack.size === size)
      }

      it("should remove the top item on pop") {
        val stack = fullStack
        val size = stack.size
        assert(stack.pop === lastValuePushed)
        assert(stack.size === size - 1)
      }
    }
  }
}