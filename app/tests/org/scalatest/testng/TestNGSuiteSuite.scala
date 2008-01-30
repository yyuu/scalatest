/*
 * Copyright 2001-2008 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.testng {

   import org.scalatest.Suite
   import org.scalatest.fun.FunSuite
   import org.testng.annotations.Test

   //execute(None, new StandardOutReporter, new Stopper {}, Set(), Set(IgnoreAnnotation), Map(), None)
   class TestNGSuiteSuite extends FunSuite {

     test( "Reporter Should Be Notified When Test Passes" ){
    
       val testReporter = new TestReporter

       // when
       new testng.test.SuccessTestNGSuite().runTestNG(testReporter)

       // then
       assert( testReporter.successCount === 1 )
     }
  

     test( "Reporter Should Be Notified When Test Fails" ){
    
       val testReporter = new TestReporter

       // when
       new testng.test.FailureTestNGSuite().runTestNG(testReporter)

       // then
       assert( testReporter.failureCount === 1 )
     }

     
     test( "If a test fails due to an exception, Report should have the exception" ){
       
       val testReporter = new TestReporter

       // when
       new testng.test.FailureTestNGSuite().runTestNG(testReporter)

       // then
       assert( testReporter.report.throwable.get.getMessage === "fail" )
     }
     

     test( "Report should be generated for each invocation" ){
       
       val testReporter = new TestReporter

       // when
       new testng.test.TestNGSuiteWithInvocationCount().runTestNG(testReporter)

       // then
       assert( testReporter.successCount === 10 )
     }

     
     test( "Groups with one method should run" ){ testGroups(Set("runMe"), 1 ) } 
     
     test( "Groups with more than one method should run" ){ testGroups(Set("runMeToo"), 2 ) }     
     
     test( "When specifically specifying to use more than one group, each group given should run" ){ 
       testGroups(Set("runMe, runMeToo"), 3 ) 
     }     

     test( "When groups are not given, all groups should run" ){ testGroups(Set(), 4 ) }    
     
     test( "Groups that doesnt exist should not do anything?" ){ testGroups(Set("groupThatDoesntExist"), 0 ) } 
     
     def testGroups( groups: Set[String], successCount: int ) = {
       // given
       val testReporter = new TestReporter

       // when
       new testng.test.TestNGSuiteWithGroups().runTestNG(testReporter, groups)

       // then
       assert( testReporter.successCount === successCount )
     }
     

     /**
      * This class only exists because I cant get jmock to work with Scala. 
      * Other people seem to do it. Frustrating. 
      */
     class TestReporter extends Reporter{

       var report: Report = null;
       var successCount = 0;
       var failureCount = 0;
       
       override def testSucceeded(report: Report){ 
         successCount = successCount + 1 
         this.report = report;
       }
       
       override def testFailed(report: Report){ 
         failureCount = failureCount + 1 
       	 this.report = report;
       }
     }
  
   }

   package test{
     
     class FailureTestNGSuite extends TestNGSuite {
       @Test def testThatFails() { throw new Exception("fail") }
     }
     
     class SuccessTestNGSuite extends TestNGSuite {
       @Test def testThatPasses() {}
     }
     
     class TestNGSuiteWithInvocationCount extends TestNGSuite {
       @Test{val invocationCount=10} def testThatPassesTenTimes() {}
     }
     
     class TestNGSuiteWithGroups extends TestNGSuite {
       @Test{val groups=Array("runMe")} def testThatRuns() {}
       @Test{val groups=Array("runMeToo")} def testThatRunsInAnotherGroup() {}
       @Test{val groups=Array("runMeToo")} def anotherTestThatRunsInAnotherGroup() {}
       @Test{val groups=Array("runMeThree")} def yetAnotherTestThatRunsInYetAnotherGroup() {}
     }
   }
}


