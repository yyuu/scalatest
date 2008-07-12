package org.scalatest.tools

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.scalatest.Report

/**
 * <p>
 * An ant task to run scalatest.  Instructions on how to specify various
 * options are below.  See the javadocs for the Runner class for a description
 * of what each of the options does.
 * </p>
 *
 * <p>
 * Define task in your ant file using taskdef, e.g.:
 * </p>
 *
 * <pre>
 *  &lt;path id="scalatest.classpath"&gt;
 *    &lt;pathelement location="${lib}/scalatest.jar"/&gt;
 *    &lt;pathelement location="${lib}/scala-library-2.6.1-final.jar"/&gt;
 *  &lt;/path&gt;
 *
 *  &lt;target name="main" depends="dist"&gt;
 *    &lt;taskdef name="scalatest" classname="org.scalatest.tools.ScalaTestTask"&gt;
 *      &lt;classpath refid="scalatest.classpath"/&gt;
 *    &lt;/taskdef&gt;
 *
 *    &lt;scalatest ...
 *  &lt;/target&gt;
 * </pre>
 *
 * <p>
 * Specify user-defined properties using nested &lt;property&gt; elements,
 * e.g.:
 * </p>
 *
 * <pre>
 *   &lt;scalatest&gt;
 *     &lt;property name="dbname" value="testdb"/&gt;
 *     &lt;property name="server" value="192.168.1.188"/&gt;
 * </pre>
 *
 * <p>
 * Specify a runpath using either a 'runpath' attribute and/or nested
 * &lt;runpath&gt; elements, using standard ant path notation, e.g.:
 * </p>
 *
 * <pre>
 *   &lt;scalatest runpath="serviceuitest-1.1beta4.jar:myjini"&gt;
 * </pre>
 *
 * or
 *
 * <pre>
 *   &lt;scalatest&gt;
 *     &lt;runpath&gt;
 *       &lt;pathelement location="serviceuitest-1.1beta4.jar"/&gt;
 *       &lt;pathelement location="myjini"/&gt;
 *     &lt;/runpath&gt;
 * </pre>
 *
 * <p>
 * To add a url to your runpath, use a &lt;runpathurl&gt; element
 * (since ant paths don't support url's), e.g.:
 * </p>
 *
 * <pre>
 *   &lt;scalatest&gt;
 *     &lt;runpathurl url="http://foo.com/bar.jar"/&gt;
 * </pre>
 *
 * <p>
 * Specify reporters using nested &lt;reporter&gt; elements, where the 'type'
 * attribute must be one of the following:
 * </p>
 *
 * <ul>
 *   <li>  graphic          </li>
 *   <li>  file             </li>
 *   <li>  stdout           </li>
 *   <li>  stderr           </li>
 *   <li>  reporterclass    </li>
 * </ul>
 *
 * <p>
 * Each may include a config attribute to specify the reporter configuration.
 * Types 'file' and 'reporterclass' require additional attributes 'filename'
 * and 'classname', respectively.  E.g.:
 * </p>
 *
 * <pre>
 *   &lt;scalatest&gt;
 *     &lt;reporter type="stdout"        config="FAB"/&gt;
 *     &lt;reporter type="file"          filename="test.out"/&gt;
 *     &lt;reporter type="reporterclass" classname="my.ReporterClass"/&gt;
 * </pre>
 *
 * <p>
 * Specify group includes and excludes using &lt;includes&gt; and
 * &lt;excludes&gt; elements.  E.g.:
 * </p>
 *
 * <pre>
 *   &lt;scalatest&gt;
 *     &lt;includes&gt;
 *         CheckinTests
 *         FunctionalTests
 *     &lt;/includes&gt;
 *
 *     &lt;excludes&gt;
 *         SlowTests
 *         NetworkTests
 *     &lt;/excludes&gt;
 * </pre>
 *
 * <p>
 * Specify suites using either a 'suite' attribute or nested
 * &lt;suite&gt; elements.  E.g.:
 * </p>
 *
 * <pre>
 *   &lt;scalatest suite="com.artima.serviceuitest.ServiceUITestkit"&gt;
 * </pre>
 *
 * <p>
 * or
 * </p>
 *
 * <pre>
 *   &lt;scalatest&gt;
 *     &lt;suite classname="com.artima.serviceuitest.ServiceUITestkit"/&gt;
 * </pre>
 *
 * <p>
 * To specify suites using members-only or wildcard package names, use
 * either the membersonly or wildcard attributes, or nested
 * &lt;membersonly&gt; or &lt;wildcard&gt; elements.  E.g.:
 * </p>
 *
 * <pre>
 *   &lt;scalatest membersonly="com.artima.serviceuitest"&gt;
 * </pre>
 *
 * <p>
 * or
 * </p>
 *
 * <pre>
 *   &lt;scalatest wildcard="com.artima.joker"&gt;
 * </pre>
 *
 * <p>
 * or
 * </p>
 *
 * <pre>
 *   &lt;scalatest&gt;
 *     &lt;membersonly package="com.artima.serviceuitest"/&gt;
 *     &lt;wildcard package="com.artima.joker"/&gt;
 * </pre>
 *
 * @author George Berger
 * @author Josh Cough
 */
class NewAntTask extends Task {

  var runpath = List[String]()
  var suites =  List[String]()
  var membersonly = List[String]()
  
  /**
   * Sets up ScalaTest by using the given inputs
   * Runs ScalaTest
   * Fails the build if any tests fail.
   */
  override def execute = {
    // this reporter will keep track of failed tests
    // in order to fail the build later on if needed
    val reporter = new AntTaskReporter
    
    // sets up ScalaTest by using the given inputs 
    val scalatest = buildScalaTestInstance(reporter)
    
    // run scalatest!
    scalatest.doRunRunRunADoRunRun
    
    // check for test failures, throw if needed
    this.finish(reporter)
  }
  
  /**
   * Sets up ScalaTest by using the given inputs
   */
  def buildScalaTestInstance( reporter: AntTaskReporter ) = {
    // create the scalatest instance with the runpath
    val scalatest = new ScalaTest(runpath)
    // set its suites field
    scalatest.setSuites(suites)
    // set the members only field
    scalatest.setMembersOnly(membersonly)
    
    scalatest.addReporter(reporter)
  
    scalatest
  }
  
  /**
   * check for test failures, throw if needed
   */
  def finish( reporter: AntTaskReporter ) = {
    // weird weird timing issue.
    // FIXME
    Thread.sleep(500)
    
    // throw BuildException if there were any failing tests.
    if( ! reporter.failedTests.isEmpty )
      throw new BuildException("tests failed: " + reporter.failedTests)
    else 
      println("All tests passed.")
  }
  
  /////////////////////////////////////////////
  // input related methods                   //
  /////////////////////////////////////////////
  
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

  def addConfiguredMembersOnly(member: PackageElement) = {
    membersonly = member.packageName :: membersonly
  }
}


// keeps track of all failed tests
class AntTaskReporter extends Reporter {
  var failedTests: List[Report] = Nil
  override def testFailed( report: Report ) = {
    failedTests = report :: failedTests 
  }
}

case class SuiteElement {
  var className: String = null
  def setClassName(className: String): Unit = this.className = className
  def getClassName: String = className
}

case class TextElement {
  var text: String = null
  def addText(text:String) = this.text = text
  def getText() = text
}

case class PackageElement {
  var packageName: String = null
  def setPackage(packageName: String) = this.packageName = packageName
  def getPackage = packageName
}