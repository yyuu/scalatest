package org.scalatest.path

import org.scalatest.PrivateMethodTester
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.SharedHelpers
import org.scalatest.Stopper
import org.scalatest.Filter
import org.scalatest.Tracker

class FunSpecSpec extends org.scalatest.FunSpec with ShouldMatchers with SharedHelpers with PrivateMethodTester {

  describe("FunSpec ThreadLocal variable") {
    it("should be set by setPath and clear by getPath") {
      val setPath = PrivateMethod[Unit]('setPath)
      val getPath = PrivateMethod[Option[List[Int]]]('getPath)
      
      FunSpec invokePrivate setPath(List(1, 2, 3))
      FunSpec invokePrivate getPath() should be (Some(List(1, 2, 3)))
      FunSpec invokePrivate getPath() should be (None)
    }
  }
  
  class MyFunSpec extends org.scalatest.path.FunSpec with ShouldMatchers {
    
    import scala.collection.mutable.ListBuffer
    import MyFunSpec._
    
    instanceCount += 1 
    
    describe("An empty list") {
      val list = ListBuffer[Int]() 
      
      describe("when 1 is inserted") {
        it("should have only 1 in it") {
          list += 1 
          list should be (ListBuffer(1)) 
          firstTestCount += 1
          inTest1FirstDescWas = firstDescCount
          inTest1SecondDescWas = secondDescCount
          inTest1OuterDescWas = outerDescCount
        }
        firstDescCount += 1
      }
      
      describe("when 2 is inserted") {
        it("should have only 2 in it") {
          list += 2
          list should be (ListBuffer(2))
          secondTestCount += 1
          inTest2FirstDescWas = firstDescCount
          inTest2SecondDescWas = secondDescCount
          inTest2OuterDescWas = outerDescCount
        }
        secondDescCount += 1
      }
      outerDescCount += 1
    }
    
    override def newInstance = new MyFunSpec
  }
  
  object MyFunSpec {
    import scala.collection.mutable.ListBuffer
    
    var instanceCount = 0
    var firstDescCount = 0
    var secondDescCount = 0
    var outerDescCount = 0
    var firstTestCount = 0
    var secondTestCount = 0
    var inTest1FirstDescWas = 0
    var inTest1SecondDescWas = 0
    var inTest1OuterDescWas = 0
    var inTest2FirstDescWas = 0
    var inTest2SecondDescWas = 0
    var inTest2OuterDescWas = 0
    
    def resetCounts() {
      instanceCount = 0
      firstDescCount = 0
      secondDescCount = 0
      outerDescCount = 0
      firstTestCount = 0
      secondTestCount = 0
      inTest1FirstDescWas = 0
      inTest1SecondDescWas = 0
      inTest1OuterDescWas = 0
      inTest2FirstDescWas = 0
      inTest2SecondDescWas = 0
      inTest2OuterDescWas = 0
    }
  }
  
  describe("A FunSpec") {
    
    it("should create an one instance per test, running each describe clause once plus once per path ") {
      MyFunSpec.resetCounts()
      val mySpec = new MyFunSpec()
      mySpec.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker())
      assert(MyFunSpec.instanceCount === 3)
      assert(MyFunSpec.firstDescCount === 2)
      assert(MyFunSpec.secondDescCount === 2)
      assert(MyFunSpec.outerDescCount === 3)
    }
    
    it("should execute each test once") {
      MyFunSpec.resetCounts()
      val mySpec = new MyFunSpec()
      mySpec.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker())
      assert(MyFunSpec.firstTestCount === 1)
      assert(MyFunSpec.secondTestCount === 1)
    }
    
    it("should execute each test before anything textually after the tests") {
      MyFunSpec.resetCounts()
      val mySpec = new MyFunSpec()
      mySpec.run(None, SilentReporter, new Stopper {}, Filter(), Map(), None, new Tracker())
      assert(MyFunSpec.inTest1FirstDescWas === 1)
      assert(MyFunSpec.inTest1SecondDescWas === 1)
      assert(MyFunSpec.inTest1OuterDescWas === 1)
      assert(MyFunSpec.inTest2FirstDescWas === 2)
      assert(MyFunSpec.inTest2SecondDescWas === 1)
      assert(MyFunSpec.inTest2OuterDescWas === 2)
    }
 
    class AllResultsSpec extends org.scalatest.path.FunSpec {
      it("should succeed") {
        assert(1 + 1 === 2)
      }
      it("should fail") {
        assert(1 + 1 === 3)
      }
      it("should be pending") (pending)
      ignore("should be ignored") {
        assert(1 + 1 === 3)
      }
      // TODO in 2.0, add a canceled test
      override def newInstance = new AllResultsSpec
	}

    it("should report a sucessful/failed/pending/ignored tests correctly") {

      val mySpec = new AllResultsSpec()
      val repo = new EventRecordingReporter
      mySpec.run(None, repo, new Stopper {}, Filter(), Map(), None, new Tracker())
      assert(repo.testSucceededEventsReceived.size === 1)
      assert(repo.testFailedEventsReceived.size === 1)
      assert(repo.testPendingEventsReceived.size === 1)
      assert(repo.testIgnoredEventsReceived.size === 1)
    }
  }
}
