package org.scalatest.finders
import org.scalatest.FeatureSpec

class FeatureSpecFinderSuite extends FinderSuite {
  
  test("FeatureSpecFinder should find test name for tests written in test suite that extends org.scalatest.FeatureSpec") {
    class TestingFeatureSpec extends FeatureSpec {
      feature("feature 1") {
        scenario("scenario 1") {
          
        }
        scenario("scenario 2") {
          scenario("nested scenario") {
            
          }
        }
      }
      feature("feature 2") {
        println("nested feature 2")
        scenario("scenario 1") {
          
        }
        scenario("scenario 2") {
          
        }
      }
      scenario("scenario with no scope") {
        
      }
    }
    
    val suiteClass = classOf[TestingFeatureSpec]
    val featureSpecConstructor = ConstructorBlock(suiteClass.getName, Array.empty)
    val feature1 = MethodInvocation(suiteClass.getName, null, featureSpecConstructor, Array(), "feature", StringLiteral(suiteClass.getName, null, "feature 1"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val feature1Scenario1 = MethodInvocation(suiteClass.getName, null, feature1, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 1"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val feature1Scenario2 = MethodInvocation(suiteClass.getName, null, feature1, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 2"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val nestedScenario = MethodInvocation(suiteClass.getName, null, feature1Scenario2, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "nested scenario"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    val feature2 = MethodInvocation(suiteClass.getName, null, featureSpecConstructor, Array.empty, "feature", StringLiteral(suiteClass.getName, null, "feature 2"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val nestedFeature2 = MethodInvocation(suiteClass.getName, null, feature2, Array.empty, "println", StringLiteral(suiteClass.getName, null, "nested feature 2"))
    val feature2Scenario1 = MethodInvocation(suiteClass.getName, null, feature2, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 1"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    val feature2Scenario2 = MethodInvocation(suiteClass.getName, null, feature2, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario 2"), ToStringTarget(suiteClass.getName, null, Array.empty, "{}"))
    
    val noScopeScenario = MethodInvocation(suiteClass.getName, null, featureSpecConstructor, Array.empty, "scenario", StringLiteral(suiteClass.getName, null, "scenario with no scope"))
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.FeatureSpec.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[FeatureSpecFinder], "Suite that uses org.scalatest.FeatureSpec should use FeatureSpecFinder.")
    
    val f1s1 = finder.find(feature1Scenario1)                      
    expectSelection(f1s1, suiteClass.getName, "feature 1 Scenario: scenario 1", Array("feature 1 Scenario: scenario 1"))
    val f1s2 = finder.find(feature1Scenario2)
    expectSelection(f1s2, suiteClass.getName, "feature 1 Scenario: scenario 2", Array("feature 1 Scenario: scenario 2"))
    val nsf1s2 = finder.find(nestedScenario)
    expectSelection(nsf1s2, suiteClass.getName, "feature 1 Scenario: scenario 2", Array("feature 1 Scenario: scenario 2"))
    
    val f2s1 = finder.find(feature2Scenario1)
    expectSelection(f2s1, suiteClass.getName, "feature 2 Scenario: scenario 1", Array("feature 2 Scenario: scenario 1"))
    val f2s2 = finder.find(feature2Scenario2)
    expectSelection(f2s2, suiteClass.getName, "feature 2 Scenario: scenario 2", Array("feature 2 Scenario: scenario 2"))
    
    val f1 = finder.find(feature1)
    expectSelection(f1, suiteClass.getName, "feature 1", Array("feature 1 Scenario: scenario 1", "feature 1 Scenario: scenario 2"))
    
    val f2 = finder.find(feature2)
    expectSelection(f2, suiteClass.getName, "feature 2", Array("feature 2 Scenario: scenario 1", "feature 2 Scenario: scenario 2"))
    
    val nsf2 = finder.find(nestedFeature2)
    expectSelection(nsf2, suiteClass.getName, "feature 2", Array("feature 2 Scenario: scenario 1", "feature 2 Scenario: scenario 2"))
    
    val nscope = finder.find(noScopeScenario)
    expectSelection(nscope, suiteClass.getName, "Scenario: scenario with no scope", Array("Scenario: scenario with no scope"))
  }

}