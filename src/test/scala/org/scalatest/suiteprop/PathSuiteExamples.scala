package org.scalatest.suiteprop

import org.scalatest._
import prop.Tables

trait PathSuiteExamples extends Tables {

  type FixtureServices

  def emptyPathFunSpec: path.FunSpec with FixtureServices
  def emptyNestedPathFunSpec: path.FunSpec with FixtureServices
  def siblingEmptyNestedPathFunSpec: path.FunSpec with FixtureServices
  def oneTestSiblingEmptyNestedPathFunSpec: path.FunSpec with FixtureServices
  def pathFunSpec: path.FunSpec with FixtureServices
  def nestedPathFunSpec: path.FunSpec with FixtureServices
  def siblingNestedPathFunSpec: path.FunSpec with FixtureServices
  def deeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  def siblingDeeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  def asymetricalDeeplyNestedPathFunSpec: path.FunSpec with FixtureServices
  
  def examples =
  Table(
    "path suite",
    emptyPathFunSpec,
    emptyNestedPathFunSpec,
    siblingEmptyNestedPathFunSpec,
    oneTestSiblingEmptyNestedPathFunSpec,
    pathFunSpec,
    nestedPathFunSpec,
    siblingNestedPathFunSpec,
    deeplyNestedPathFunSpec,
    siblingDeeplyNestedPathFunSpec,
    asymetricalDeeplyNestedPathFunSpec
  )
}