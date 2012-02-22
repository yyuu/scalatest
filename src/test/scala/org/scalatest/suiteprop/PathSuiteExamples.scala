package org.scalatest.suiteprop

import org.scalatest._
import prop.Tables

trait PathSuiteExamples extends Tables {

  type FixtureServices

  def emptyPathFunSpec: path.FunSpec with FixtureServices
  def emptyNestedPathFunSpec: path.FunSpec with FixtureServices
  def siblingEmptyNestedPathFunSpec: path.FunSpec with FixtureServices
  def oneTestSiblingEmptyNestedPathFunSpec: path.FunSpec with FixtureServices
  def oneTestSiblingEmptyDeeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  def pathFunSpec: path.FunSpec with FixtureServices
  def nestedPathFunSpec: path.FunSpec with FixtureServices
  def siblingNestedPathFunSpec: path.FunSpec with FixtureServices
  def deeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  def siblingDeeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  def asymetricalDeeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  def emptyPathFreeSpec: path.FreeSpec with FixtureServices
  def emptyNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def siblingEmptyNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def oneTestSiblingEmptyNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def oneTestSiblingEmptyDeeplyNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def pathFreeSpec: path.FreeSpec with FixtureServices
  def nestedPathFreeSpec: path.FreeSpec with FixtureServices
  def siblingNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def deeplyNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def siblingDeeplyNestedPathFreeSpec: path.FreeSpec with FixtureServices
  def asymetricalDeeplyNestedPathFreeSpec: path.FreeSpec with FixtureServices
 
  def examples =
  Table(
    "path suite",
    emptyPathFunSpec,
    emptyNestedPathFunSpec,
    siblingEmptyNestedPathFunSpec,
    oneTestSiblingEmptyNestedPathFunSpec,
    oneTestSiblingEmptyDeeplyNestedPathFunSpec,
    pathFunSpec,
    nestedPathFunSpec,
    siblingNestedPathFunSpec,
    deeplyNestedPathFunSpec,
    siblingDeeplyNestedPathFunSpec,
    asymetricalDeeplyNestedPathFunSpec,
    emptyPathFreeSpec,
    emptyNestedPathFreeSpec,
    siblingEmptyNestedPathFreeSpec,
    oneTestSiblingEmptyNestedPathFreeSpec,
    oneTestSiblingEmptyDeeplyNestedPathFreeSpec,
    pathFreeSpec,
    nestedPathFreeSpec,
    siblingNestedPathFreeSpec,
    deeplyNestedPathFreeSpec,
    siblingDeeplyNestedPathFreeSpec,
    asymetricalDeeplyNestedPathFreeSpec
  )
}