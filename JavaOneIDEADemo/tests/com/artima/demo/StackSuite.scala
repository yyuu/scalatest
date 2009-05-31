package com.artima.demo
import org.scalatest.Suite

/**
 * Created by IntelliJ IDEA.
 * User: bv
 * Date: Dec 8, 2008
 * Time: 4:39:26 PM
 * To change this template use File | Settings | File Templates.
 */

class StackSuite extends Suite {

  // Fixture
  def emptyStack = new Stack[Int]

  def testThatIsEmptyOnAnEmptyStackReturnsTrue() {
    assert(emptyStack.isEmpty)
  }

  def testThatTheSizeOfAnEmptyStackIsZero() {
    assert(emptyStack.size === 0)
  }

  def testThatPeekOnAnEmptyStackThrowsIllegalStateException() {
    intercept[IllegalStateException] {
      emptyStack.peek
    }
  }

  def testThatPopOnAnEmptyStackThrowsIllegalStateException() {
    intercept[IllegalStateException] {
      emptyStack.pop()
    }
  }

  def testThatAnEmptyIsNotFull() {
    assert(!emptyStack.isFull)
  }

  def testThatPushingAnElementOntoAnEmptyStackWorksProperly() {
    val stack = emptyStack
    stack.push(7)
    assert(stack.size === 1)
    assert(stack.peek === 7)
    assert(!stack.isEmpty)
    assert(!stack.isFull)
  }
}