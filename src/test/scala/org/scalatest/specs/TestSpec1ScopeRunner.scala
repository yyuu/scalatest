package org.scalatest.specs

import org.specs.Specification
import org.scalatest.WrapWith

@WrapWith(classOf[Spec1Runner])
class TestSpec1ScopeRunner extends Specification("Scala Spec") {
  
  "my System" should {
    "be wonderful" in { }
    "be elegant" in { }
  }
  
  "The Scala language" should {
  "provide a && operator" >> {
      "returning true for true && true" >> { true && true must beTrue } 
      "returning false for true && false" >> { true && false must beFalse } 
      "returning false for false && true" >> { true && false must beFalse } 
      "returning false for false && false" >> { false && false must beFalse } 
    }
  }
}