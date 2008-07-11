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
package org.scalatest.tools

import org.scalatest.Reporter
import org.scalatest.Suite
import org.scalatest.Stopper
import org.scalatest.testng.TestNGWrapperSuite

/**
 * Main entry point into ScalaTest.
 */
class ScalaTest(runpathList: List[String]) {
    
  val loader = new ClassLoaderHelper(runpathList)
  implicit def helperToClassLoader( clh: ClassLoaderHelper ) = loader.loader
  
  // suites
  private var suitesList: List[String] = Nil
  def addSuite(suiteName: String) = suitesList = suiteName :: suitesList
  def setSuites( l: List[String]) = suitesList = l 
  
  // reporters
  private var reporters: List[Reporter] = Nil
  def addReporter(r: Reporter) = reporters = r :: reporters
  def setReporters( l: List[Reporter]) = reporters = l 
  
  // run done listener
  private var runDoneListener = new RunDoneListener{}
  def setRunDoneListener( l: RunDoneListener) = runDoneListener = l

  // gets the dispatch reporter
  private def dispatchReporter = new DispatchReporter(reporters)
  
  // currently unused vals - need to wire them in still
  private val stopper = new Stopper{}
  private val includes = Set[String]()
  private val excludes = Set[String]()
  private val concurrent = false
  private val wildcardList: List[String] = Nil
  private val membersOnlyList: List[String] = Nil
  private val propertiesMap: Map[String, String] = Map()
  private val testNGList: List[String] = Nil
  
  /**
   * Runs ScalaTest 
   */
  private[scalatest] def doRunRunRunADoRunRun(): Unit = {
    
    if( this.loadProblemsExist ) return;
    
    try {        
      val suites: List[Suite] = this.loadSuites
      this.startRun(suites)
      this.run(suites)
      this.endRun()
    }
    catch {
      case ex: InstantiationException => abort("cannotInstantiateSuite", ex)
      case ex: IllegalAccessException => abort("cannotInstantiateSuite", ex)
      case ex: NoClassDefFoundError =>   abort("cannotLoadClass", ex)
    }
    finally {
      dispatchReporter.dispose()
      runDoneListener.done()
    }
  }

  /**
   * Executes the run.
   */
  private def run( suites: List[Suite] ){
      if (concurrent)
        this.runConcurrently(suites)
      else
        this.runConsecutively(suites)
  }

  
  /**
   * Starts the run.
   */
  private def startRun( suites: List[Suite] ){
      val expectedTestCount = suites.map( _.expectedTestCount(includes, excludes) ).foldRight(0){ _ + _ }
      dispatchReporter.runStarting(expectedTestCount)
  }

  /**
   * Ends the run.
   */
  private def endRun(){
  if (stopper.stopRequested)
        dispatchReporter.runStopped()
      else
        dispatchReporter.runCompleted()
  }
  
  

  /**
   * Runs the tests concurrently, using a ConcurrentDistributor
   */
  private def runConcurrently( suites: List[Suite] ){
    val distributor = new ConcurrentDistributor(dispatchReporter, stopper, includes, excludesWithIgnore(excludes), propertiesMap)
    suites.foreach( suite => distributor.put(suite) )
    distributor.waitUntilDone()
  }
  
  /**
   * Simply runs the test in order, one after the next.
   */
  private def runConsecutively( suites: List[Suite] ){
    for (suite <- suites) {
      new SuiteRunner(suite, dispatchReporter, stopper, includes, excludesWithIgnore(excludes),propertiesMap, None).run()
    }
  }
  
  /**
   *
   */
  private def loadSuites() = {
    val namedSuiteInstances: List[Suite] = loader.loadNamedSuites(suitesList)
    val (membersOnlySuiteInstances, wildcardSuiteInstances) = this.getMembersOnlyAndWildcardInstances
    val testNGWrapperSuiteList: List[TestNGWrapperSuite] =
      if (!testNGList.isEmpty)
        List(new TestNGWrapperSuite(testNGList))
      else
        Nil
    namedSuiteInstances ::: membersOnlySuiteInstances ::: wildcardSuiteInstances ::: testNGWrapperSuiteList
  }
  
  /**
   *
   */
  private def getMembersOnlyAndWildcardInstances() = {
    val membersOnlyAndBeginsWithListsAreEmpty = membersOnlyList.isEmpty && wildcardList.isEmpty // They didn't specify any -m's or -w's on the command line

    if (membersOnlyAndBeginsWithListsAreEmpty && !suitesList.isEmpty) {
      (Nil, Nil) // No DiscoverySuites in this case. Just run Suites named with -s
    }
    
    val accessibleSuites = (new SuiteDiscoveryHelper).discoverSuiteNames(runpathList, loader)

    if (membersOnlyAndBeginsWithListsAreEmpty && suitesList.isEmpty) {
      // In this case, they didn't specify any -w, -m, or -s on the command line, so the default
      // is to run any accessible Suites discovered on the runpath
      (Nil, List(new DiscoverySuite("", accessibleSuites, true, loader)))
    }
     
    val membersOnlyInstances =
      for (membersOnlyName <- membersOnlyList)
        yield new DiscoverySuite(membersOnlyName, accessibleSuites, false, loader)

     val wildcardInstances =
       for (wildcardName <- wildcardList)
         yield new DiscoverySuite(wildcardName, accessibleSuites, true, loader)

     (membersOnlyInstances, wildcardInstances)
  }
  
  /**
   *
   */
  private def loadProblemsExist() = {
    try {
      val unassignableList = suitesList.filter(className => !classOf[Suite].isAssignableFrom(loader.loadClass(className)))
      if (!unassignableList.isEmpty) {
        val names = for (className <- unassignableList) yield " " + className
        abort(Resources("nonSuite") + names)
        true
      }
      else {
        false
      }
    }
    catch {
      case e: ClassNotFoundException => {
        abort("cannotLoadSuite", e)
        true
      }
    }
  }
  
  /**
   *
   */
  private def abort( resourceName: String, ex: Throwable ) = {
    val report = new Report("org.scalatest.tools.Runner", Resources(resourceName), Some(ex), None)
    dispatchReporter.runAborted(report)
  }
  
  /**
   *
   */
  private def abort( message: String ) = {
    val report = new Report("org.scalatest.tools.Runner", message)
    dispatchReporter.runAborted(report)
  }
  
  /**
   *
   */
  private def excludesWithIgnore(excludes: Set[String]) = excludes + "org.scalatest.Ignore"

}