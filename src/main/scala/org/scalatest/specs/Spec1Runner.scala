package org.scalatest.specs
import org.specs.Specification
import org.scalatest.Suite
import org.scalatest.Filter
import org.scalatest.Stopper
import org.scalatest.Reporter
import org.scalatest.Distributor
import org.scalatest.Tracker
import org.scalatest.Resources
import org.scalatest.events.InfoProvided
import org.scalatest.events.NameInfo
import org.specs.runner.NotifierRunner
import org.specs.specification.Example
import org.specs.specification.Sus

class Spec1Runner(specificationClass: Class[_ <: Specification]) extends Suite { thisSuite =>
  
  val spec = specificationClass.newInstance()
  
  def getSpecTestCount(theSpec: Specification): Int = {
    theSpec.systems.foldLeft(0)(_ + getSusTestCount(_)) +
    theSpec.subSpecifications.foldLeft(0)(_ + getSpecTestCount(_))
  }
  
  def getSusTestCount(sus: Sus): Int = {
    val count = sus.examples.foldLeft(0)(_ + getExampleTestCount(_))
    count
  }
  
  def getExampleTestCount(example: Example): Int = {
    if(example.hasSubExamples)
      example.examples.map(getExampleTestCount(_)).foldLeft(0)(_ + _)
    else
      example.ownExpectationsNb
  }
  
  override def expectedTestCount(filter: Filter): Int = getSpecTestCount(spec)//spec.firstLevelExamplesNb
  
  override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
              configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
    if (testName == null)
      throw new NullPointerException("testName was null")
    if (reporter == null)
      throw new NullPointerException("reporter was null")
    if (stopper == null)
      throw new NullPointerException("stopper was null")
    if (filter == null)
      throw new NullPointerException("filter was null")
    if (configMap == null)
      throw new NullPointerException("configMap was null")
    if (distributor == null)
      throw new NullPointerException("distributor was null")
    if (tracker == null)
      throw new NullPointerException("tracker was null")

    val stopRequested = stopper
    val report = wrapReporterIfNecessary(reporter)
    
    runSpec(Some(spec), tracker, reporter)
    
    if (stopRequested()) {
      val rawString = Resources("executeStopping")
      report(InfoProvided(tracker.nextOrdinal(), rawString, Some(NameInfo(thisSuite.suiteName, Some(thisSuite.getClass.getName), testName))))
    }
  }
  
  def runSpec(specification: Option[Specification], tracker: Tracker, reporter: Reporter): Option[Specification] = {
    def testInterfaceRunner(s: Specification) = new ScalaTestNotifierRunner(s, new ScalaTestNotifier(spec, tracker, reporter))
    specification.map(testInterfaceRunner(_).reportSpecs)
    specification match {
      case Some(s: org.specs.runner.File) => s.reportSpecs
      case _ => ()
    }
    specification
  }
}