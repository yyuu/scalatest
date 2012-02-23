package org.scalatest.finders

import org.scalatest.FunSuite
import org.scalatest.Suite
import org.scalatest.Style
import org.scalatest.FeatureSpec
import org.scalatest.fixture.FixtureSuite
import org.scalatest.FreeSpec
import org.scalatest.FlatSpec
import org.scalatest.PropSpec

trait FinderSuite extends FunSuite {
  
  def expectSelection(selectionOpt: Option[Selection], expectedClassName: String, expectedDisplayName: String, expectedTestNames: Array[String]) {
    assert(selectionOpt.getClass == classOf[Some[_]], "Test is None, expected className=" + expectedClassName + ", displayName=" + expectedDisplayName + ", testNames=" + expectedTestNames.deepToString)
    val selection = selectionOpt.get
    expect(expectedClassName)(selection.className)
    expect(expectedDisplayName)(selection.displayName)
    expect(expectedTestNames.deepToString)(selection.testNames.deepToString)
  }
  
}