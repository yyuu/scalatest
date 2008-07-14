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
 * @author Bill Venners
 * @author Josh Cough
 */
class ScalaTest(runpathList: List[String]) {
  
  ///////////////////////////////////////////////////////////////////////////////////
  // list of fields and their defaults. user can supply values for any.            //
  ///////////////////////////////////////////////////////////////////////////////////

  // suites
  private var suitesList: List[String] = Nil
  
  // includes
  private var includes = Set[String]()

  // excludes
  private var excludes = Set[String]("org.scalatest.Ignore")
  
  // reporters
  private var reporters: List[Reporter] = List(new StandardOutReporter)
  
  // run done listener. default does nothing
  private var runDoneListener = new RunDoneListener{}

  // stopper. default does nothing
  private var stopper = new Stopper{}
  
  // the dispatch reporter. 
  private var dispatchReporter = new DispatchReporter(reporters)
  
  // run concurrently
  private var concurrent = false

  // wildcards
  private var wildcards: List[String] = Nil 

  // members only
  private var membersOnlyList: List[String] = Nil 
  
  // properties
  private var properties: Map[String, String] = Map()  
  
  // TestNG Wrapper Suites
  private var testNGWrappers: List[String] = Nil

  // helper class to help with all class loading activities
  private val loader = new ClassLoaderHelper(runpathList)
  private implicit def helperToClassLoader( clh: ClassLoaderHelper ) = loader.loader
  
  ///////////////////////////////////////////////////////////////////////////////////
  // mutator methods for fields                                                    //
  ///////////////////////////////////////////////////////////////////////////////////
  
  def addSuite(suiteName: String) = suitesList = suiteName :: suitesList
  def setSuites( l: List[String] ) = suitesList = l 
  def setIncludes( in: Set[String] ) = includes = in
  def addInclude( include: String ) = includes += include
  def setExcludes( ex: Set[String] ) = excludes = ex
  def addExclude( exclude: String ) = excludes += exclude
  def setReporters( l: List[Reporter] ) = reporters = l 
  def addReporter(r: Reporter) = {
    reporters = r :: reporters
    dispatchReporter = new DispatchReporter(reporters)
  }
  def setRunDoneListener( l: RunDoneListener ) = runDoneListener = l
  def setStopper( s: Stopper ) = stopper = s
  def setConcurrent( b: boolean ) = concurrent = b
  def addWildcard(wildcard: String) = wildcards = wildcard :: wildcards
  def setWildcards( l: List[String] ) = wildcards = l
  def addMember(wildcard: String) = membersOnlyList = wildcard :: membersOnlyList
  def setMembersOnly( l: List[String]) = membersOnlyList = l
  def setProperties( p: Map[String, String] ) = properties = p
  //def addProperty( k: String, v: String ) = properties += (k=>v)
  def setTestNGWrappers( l: List[String] ) = testNGWrappers = l 

  ///////////////////////////////////////////////////////////////////////////////////
  // methods to run ScalaTest, Suites, and Rerunnables                             //
  ///////////////////////////////////////////////////////////////////////////////////
    
  /**
   * Runs ScalaTest 
   */
  def doRunRunRunADoRunRun(): Unit = {
    
    // check to see if load problems exist.
    // if they do, reporters will be notified during check
    if( this.loadProblemsExist ) return;
    
    try {        
      // load all of the suites using given field values
      val suites: List[Suite] = this.loadSuites
      
      // dispatch run starting
      this.startRun(suites)
      
      // runs all suites
      this.run(suites)
      
      // dispatches run ended event (via runCompleted or runStopped) 
      this.endRun()
    }
    catch {
      case ex: InstantiationException => dispatchRunAborted(getClass.getName(), "cannotInstantiateSuite", ex)
      case ex: IllegalAccessException => dispatchRunAborted(getClass.getName(), "cannotInstantiateSuite", ex)
      case ex: NoClassDefFoundError =>   dispatchRunAborted(getClass.getName(), "cannotLoadClass", ex)
    }
    finally {
      dispatchReporter.dispose()
      runDoneListener.done()
    }
  }
  
  /**
   * Runs ScalaTest 
   */
  def run() = doRunRunRunADoRunRun
  
  /**
   * Starts the run.
   */
  private def startRun( suites: List[Suite] ){
    val expectedTestCount = suites.map( _.expectedTestCount(includes, excludes) ).foldRight(0){ _ + _ }
    dispatchReporter.runStarting(expectedTestCount)
  }

  /**
   * Executes the run.
   */
  private def run( suites: List[Suite] ){
    if (concurrent) this.runConcurrently(suites)
    else this.runConsecutively(suites)
  }

  /**
   * Ends the run.
   */
  private def endRun(){
    if (stopper.stopRequested) dispatchReporter.runStopped()
    else dispatchReporter.runCompleted()
  }
  
  /**
   * Runs the tests concurrently, using a ConcurrentDistributor
   */
  private def runConcurrently( suites: List[Suite] ){
    val distributor = new NewConcurrentDistributor(this)
    distributor.putAll(suites)
    distributor.waitUntilDone()
  }
  
  /**
   * Simply runs the test in order, one after the next.
   */
  private def runConsecutively( suites: List[Suite] ) = suites.foreach( this.run(_) ) 

  /**
   * Run a Suite.
   */
  def run(suite: Suite): Unit = {
    if( stopper.stopRequested ) return
    
    this.dispatchSuiteStarting( suite )
  
    try {
      suite.execute(None, dispatchReporter, stopper, includes, excludes, properties, None)
      this.dispatchSuiteCompleted( suite )
    }
    catch {
      case e: RuntimeException => this.dispatchSuiteAborted(suite, e)
    }
  }
    
  /**
   * Rerun something.
   */
  def rerun( rerunnable: Rerunnable ) = {
    rerunnable.rerun(dispatchReporter, stopper, includes, excludes, properties, None, loader)
  }  

  ///////////////////////////////////////////////////////////////////////////////////
  // methods for loading Suites                                                    //
  ///////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Loads named suites
   * loads members only suites and wildcard suites
   * loads testng wrapper suites
   */
  private def loadSuites() = {
    val namedSuiteInstances: List[Suite] = loader.loadNamedSuites(suitesList)
    val (membersOnlySuiteInstances, wildcardSuiteInstances) = this.getMembersOnlyAndWildcardInstances
    val testNGWrapperSuiteList = this.loadTestNGWrapperSuites
    namedSuiteInstances ::: membersOnlySuiteInstances ::: wildcardSuiteInstances ::: testNGWrapperSuiteList
  }
  
  /**
   * loads testng wrapper suites
   */
  def loadTestNGWrapperSuites = {
    if (!testNGWrappers.isEmpty)
      List(new TestNGWrapperSuite(testNGWrappers))
    else
      Nil
  }
  
  /**
   * loads members only suites and wildcard suites
   */
  private def getMembersOnlyAndWildcardInstances() = {
    val membersOnlyAndBeginsWithListsAreEmpty = membersOnlyList.isEmpty && wildcards.isEmpty // They didn't specify any -m's or -w's on the command line

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
       for (wildcard <- wildcards)
         yield new DiscoverySuite(wildcard, accessibleSuites, true, loader)

     (membersOnlyInstances, wildcardInstances)
  }
  
  /**
   * @returns true if suite classes cannot be found, or if guven suite class names arent really suites ( is this right? )
   */
  private def loadProblemsExist() = {
    try {
      val unassignableList = suitesList.filter(className => !classOf[Suite].isAssignableFrom(loader.loadClass(className)))
      if (!unassignableList.isEmpty) {
        val names = for (className <- unassignableList) yield " " + className
        dispatchRunAborted(getClass.getName(), Resources("nonSuite") + names)
        true
      }
      else {
        false
      }
    }
    catch {
      case e: ClassNotFoundException => {
        dispatchRunAborted(getClass.getName(), "cannotLoadSuite", e)
        true
      }
    }
  }
  
  ///////////////////////////////////////////////////////////////////////////////////
  // methods for dispatching reports                                               //
  ///////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Notifies all reporters that the run has been aborted due to the given problem.
   */
  def dispatchRunAborted( runner: String, message: String ) = {
    dispatchReporter.runAborted(new Report(runner, message))
  }
  
  /**
   * Notifies all reporters that the run has been aborted due to the given exception. 
   */
  def dispatchRunAborted( runner: String, resourceName: String, ex: Throwable ) = {
    dispatchReporter.runAborted(new Report(runner, Resources(resourceName), Some(ex), None))
  }

  /**
   *
   */
  def dispatchSuiteStarting(s: Suite) = {
    dispatchReporter.suiteStarting(buildReport(s, "suiteExecutionStarting", None))
  }
  
  /**
   *
   */  
  def dispatchSuiteCompleted(s: Suite) = {
    dispatchReporter.suiteCompleted(buildReport(s, "suiteCompletedNormally", None))
  }

  /**
   *
   */  
  def dispatchSuiteAborted(s: Suite, t: Throwable) = {
    dispatchReporter.suiteAborted(buildReport(s, "executeException", Some(t)))
  }
  
  /**
   *
   */  
  def buildReport( suite: Suite, resourceName: String, o: Option[Throwable] ) : Report = {
    new Report(suite.suiteName, Resources(resourceName), o, suite.getRerunnable)
  }
  
}