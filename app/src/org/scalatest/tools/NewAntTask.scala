package org.scalatest.tools

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.scalatest.Report

class NewAntTask extends Task{

  var runpath = List[String]()
  var suites = List[String]()
  
  override def execute = {
    val scalatest = new ScalaTest(runpath)
    scalatest.setSuites(suites)
    
    val reporter = new AntTaskReporter
    scalatest.addReporter(reporter)
    
    
    scalatest.doRunRunRunADoRunRun
    
    if( ! reporter.failedTests.isEmpty )
      throw new BuildException("tests failed: " + reporter.failedTests)
  }
  
  def setRunpath(path: Path) {
    for( element <- path.list()) runpath = element :: runpath
  }
  
  def addConfiguredRunpath(path: Path) {
    for( element <- path.list() ) runpath = element :: runpath
  }
  
  def addConfiguredSuite(suite: SuiteElement): Unit = {
    suites = suite.getClassName :: suites
  }
  
  def setSuite(suite: String) = suites = List(suite)

  class SuiteElement {
    var className: String = null;
    def setClassName(className: String): Unit = this.className = className
    def getClassName: String = className
  }
  
  class AntTaskReporter extends Reporter {

	var failedTests: List[Report] = Nil
	
	override def testFailed( report: Report ) = {
	  failedTests = report :: failedTests 
	}
  }
  
}



