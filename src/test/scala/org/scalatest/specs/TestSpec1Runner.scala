package org.scalatest.specs

import org.specs.Specification
import org.scalatest.WrapWith

@WrapWith(classOf[Spec1Runner])
class TestSpec1Runner extends Specification {
  "'hello world' has 11 characters" in {
     "hello world".size must_== 11
  }
  "'hello world' matches 'h.* w.*'" in {
     "hello world" must be matching("h.* w.*")
  }
}