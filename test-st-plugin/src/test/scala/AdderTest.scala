package com.example.test
 
import org.scalatest.FunSuite
import com.example.Widget
import com.example.Adder
import org.scalatest.Tag

object SlowTest extends Tag("SlowTest")
 
class AdderTest extends FunSuite {
 
  test("1 + 1 = 2", SlowTest) {
    expect(2) { new Adder().add(1 ,1) }
  }
}
