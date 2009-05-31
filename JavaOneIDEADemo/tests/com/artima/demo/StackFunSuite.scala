package com.artima.demo
import org.scalatest.FunSuite

/**
 * Created by IntelliJ IDEA.
 * User: bv
 * Date: Dec 9, 2008
 * Time: 6:44:58 PM
 * To change this template use File | Settings | File Templates.
 */

class StackFunSuite extends FunSuite {

  // Fixture
  def emptyStack = new Stack[Int]

  test("isEmpty on an empty stack should return true") {
    assert(emptyStack.isEmpty)
  }

  test("the size of an empty stack should be zero") {
    assert(emptyStack.size === 0)
  }

  test("peek on an empty stack should throw IllegalStateException") {
    intercept[IllegalStateException] {
      emptyStack.peek
    }
  }

  test("pop on an empty stack should throw IllegalStateException") {
    intercept[IllegalStateException] {
      emptyStack.pop
    }
  }

  test("anEmpty should not be full") {
    assert(!emptyStack.isFull)
  }

  test("pushing an element onto an empty stack should work properly") {
    val stack = emptyStack
    stack.push(7)
    assert(stack.size === 1)
    assert(stack.peek === 7)
    assert(!stack.isEmpty)
    assert(!stack.isFull)
  }
}