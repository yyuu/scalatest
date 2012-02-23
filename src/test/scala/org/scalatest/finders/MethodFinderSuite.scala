package org.scalatest.finders
import org.scalatest.Suite

class MethodFinderSuite extends FinderSuite {
  
  test("MethodFinder should find test name for tests written in test suite that extends org.scalatest.Suite") {
    class TestingSuite extends Suite {
      def testMethod1(aParam: String) {
        
      }
      def testMethod2() {
        
      }
      def testMethod3() {
        def testNested() {
          
        }
      }
    }
    
    val suiteClass = classOf[TestingSuite]
    val suiteConstructor = new ConstructorBlock(suiteClass.getName, Array.empty)
    val testMethod1 = MethodDefinition(suiteClass.getName, suiteConstructor, Array.empty, "testMethod1", "java.lang.String")
    val testMethod2 = MethodDefinition(suiteClass.getName, suiteConstructor, Array.empty, "testMethod2")
    val testMethod3 = MethodDefinition(suiteClass.getName, suiteConstructor, Array.empty, "testMethod3")
    val testNested = MethodDefinition(suiteClass.getName, testMethod3, Array.empty, "testNested")
    
    val finderOpt: Option[Finder] = LocationUtils.getFinder(suiteClass)
    assert(finderOpt.isDefined, "Finder not found for suite that uses org.scalatest.Suite.")
    val finder = finderOpt.get
    assert(finder.getClass == classOf[MethodFinder], "Suite that uses org.scalatest.Suite should use MethodFinder.")
    val selectionMethod1 = finder.find(testMethod1)
    expect(None)(selectionMethod1)
    val selectionMethod2 = finder.find(testMethod2)
    expectSelection(selectionMethod2, suiteClass.getName, suiteClass.getName + ".testMethod2", Array("testMethod2"))
    val selectionMethod3 = finder.find(testMethod3)
    expectSelection(selectionMethod3, suiteClass.getName, suiteClass.getName + ".testMethod3", Array("testMethod3"))
    val selectionNested = finder.find(testNested)
    expectSelection(selectionNested, suiteClass.getName, suiteClass.getName + ".testMethod3", Array("testMethod3"))
  }

}