package org.scalatest

class TestFreeSpec extends FreeSpec {
  "my System should" - {
    "be wonderful" in { }
    "be elegant" in { }
  }
  
  "The Scala language" - {
    "provide a && operator" - {
      "returning true for true && true" in { true && true == true } 
      "returning false for true && false" in { true && false == false } 
      "returning false for false && true" in { true && false == false } 
      "returning false for false && false" in { false && false == false } 
    }
  }
}