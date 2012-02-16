package com.example.test
 
import org.scalatest.FunSuite
import com.example.Widget
 
class WidgetTest extends FunSuite {
 
  test("colour") {
    expect("Blue") { new Widget().colour }
  }
 
  test("disposition") {
    expect("Awesome") { new Widget().disposition }
  }
}
