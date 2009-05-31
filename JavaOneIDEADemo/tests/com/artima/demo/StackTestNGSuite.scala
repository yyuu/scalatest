package com.artima.demo

import org.scalatest.Suite
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.Test

/**
 * Created by IntelliJ IDEA.
 * User: bv
 * Date: Dec 8, 2008
 * Time: 4:39:26 PM
 * To change this template use File | Settings | File Templates.
 */

class StackTestNGSuite extends TestNGSuite {

  // Fixture
  def emptyStack = new Stack[Int]

  @Test def isEmptyOnAnEmptyStackShouldReturnTrue() {
    assert(emptyStack.isEmpty)
  }

  @Test def theSizeOfAnEmptyStackShouldBeZero() {
    assert(emptyStack.size === 0)
  }

  @Test def peekOnAnEmptyStackShouldThrowIllegalStateException() {
    intercept[IllegalStateException] {
      emptyStack.peek
    }
  }

  @Test def popOnAnEmptyStackShouldThrowIllegalStateException() {
    intercept[IllegalStateException] {
      emptyStack.pop()
    }
  }

  @Test def anEmptyStackShouldNotBeFull() {
    assert(!emptyStack.isFull)
  }

  @Test def pushingAnElementOntoAnEmptyStackShouldWorkProperly() {
    val stack = emptyStack
    stack.push(7)
    assert(stack.size === 1)
    assert(stack.peek === 7)
    assert(!stack.isEmpty)
    assert(!stack.isFull)
  }
}