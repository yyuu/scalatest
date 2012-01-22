package org.scalatest

import WantsConfigMapSuite.instantiated

@WrapWith(classOf[ConfigMapWrapperSuite])
class WantsConfigMapSuite(configMap: Map[String, Any]) extends FunSuite {
  instantiated = true
}

object WantsConfigMapSuite {
  private var instantiated = false
  def wasInstantiated = instantiated
}