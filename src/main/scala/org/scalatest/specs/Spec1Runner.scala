package org.scalatest.specs

import org.specs.runner.ScalaTestSuite
import org.specs.Specification
import org.scalatest.Filter
import org.scalatest.Stopper
import org.scalatest.Reporter
import org.scalatest.Distributor
import org.scalatest.Tracker
import org.scalatest.events.InfoProvided
import org.scalatest.events.NameInfo
import org.scalatest.events.Event
import org.scalatest.Suite
import org.scalatest.SuiteRerunner
import org.scalatest.events.SuiteStarting
import org.scalatest.Resources
import org.scalatest.events.SuiteCompleted
import org.scalatest.events.SuiteAborted
import org.scalatest.events.Formatter
import org.scalatest.events.IndentedText
import org.scalatest.events.MotionToSuppress
import org.specs.specification.Example
import org.scalatest.events.TestStarting
import org.scalatest.events.TestIgnored
import org.scalatest.events.TestFailed
import org.scalatest.events.TestSucceeded
import org.scalatest.events.Ordinal
import java.util.HashMap
import java.util.HashSet

class Spec1Runner(specification: Specification) extends Suite { thisSuite =>
  
  val spec = specification
  
  override def nestedSuites: List[Suite] = Nil
      
  override def runTest(testName: java.lang.String,
                         reporter: org.scalatest.Reporter,
                         stopper: Stopper,
                         properties: Map[java.lang.String, Any],
                         tracker: Tracker): Unit = {
    spec.examples.find(_.description == testName).map(e => runExample(e, reporter, spec.description, properties))
  }
  
  private[this] def runExample(e: Example, reporter: org.scalatest.Reporter, suiteName: String, properties: Map[java.lang.String, Any]): Unit = {
    def planOnly = properties.keySet.contains("plan")
    if (planOnly)
      reporter(TestStarting(current.next, suiteName, None, "- " + e.description))
    else
      reporter(TestStarting(current.next, suiteName, None, e.statusAsText + " " + e.description))
    if (!planOnly) {
      e.skipped foreach { skipped =>
        reporter(TestIgnored(current.next, suiteName, None, e.description + ": " + skipped.message))
      }
      e.failures foreach { f =>
        reporter(TestFailed(current.next, f.getMessage, suiteName, None, e.description, Some(f)))
      }
      e.errors foreach { error =>
        reporter(TestFailed(current.next, error.getMessage, suiteName, None, e.description, Some(error)))
      }
      if (e.failures.isEmpty && e.errors.isEmpty && e.skipped.isEmpty)
        reporter(TestSucceeded(current.next, suiteName, None, e.description))
      e.examples foreach { sub => runExample(sub, reporter, suiteName, properties) }
    }
    else
      reporter(TestSucceeded(current.next, suiteName, None, e.description))
  }
  
  override def testNames: Set[java.lang.String] = {
    spec.examples.map(_.description).toSet
  }
  private def current: Ordinal = new Ordinal(0)
  
  override def runTests(testName: Option[java.lang.String],
                         reporter: org.scalatest.Reporter,
                         stopper: Stopper,
                         filter: Filter,
                         properties: Map[java.lang.String, Any],
                         distributor: Option[Distributor],
                         tracker: Tracker): Unit = {
      val testTags = tags
      def isIncluded(name: String): Boolean = {
        val tagsForName = testTags.get(name).getOrElse(Set())
        val r = filter.tagsToInclude.isEmpty && 
                !filter.tagsToExclude.exists(tagsForName.contains(_)) ||
                !filter.tagsToInclude.isEmpty && 
                 filter.tagsToInclude.get.exists(tagsForName.contains(_)) &&
                !filter.tagsToExclude.exists(tagsForName.contains(_))
        r
      }
      testName filter(isIncluded(_)) map { name => 
        runTest(name, reporter, stopper, properties, tracker) 
      } getOrElse {
        testNames filter(isIncluded(_)) map { name => runTest(name, reporter, stopper, properties, tracker) }
      }
    }

  override def tags: Map[String, Set[String]] = {
    var exampleNames: Map[String, Set[String]] = Map[String, Set[String]]()
    for {e <- spec.examples
         tag <- e.tagNames } {
        val exampleTags: Set[String] = exampleNames.get(e.description) match {
          case None => Set()
          case Some(set) => set
        }
        exampleNames  = exampleNames  + (e.description -> (exampleTags + tag))
    }
    exampleNames
  }
}