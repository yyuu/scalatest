package org.scalatest.specs
import org.specs.Specification
import org.specs.runner.Notifier
import org.scalatest.Reporter
import org.scalatest.Tracker
import org.scalatest.events.IndentedText
import org.scalatest.events.SuiteStarting
import org.scalatest.events.InfoProvided
import org.scalatest.events.NameInfo
import org.scalatest.events.MotionToSuppress
import org.scalatest.events.SuiteCompleted
import org.scalatest.events.SuiteAborted
import org.scalatest.events.TestStarting
import org.scalatest.events.TestSucceeded
import org.scalatest.events.TestFailed
import org.scalatest.events.TestPending
import org.scalatest.Suite

class ScalaTestNotifier(theSpec: Specification, theTracker: Tracker, reporter: Reporter) extends ScalaTestAbstractNotifier {
  val spec = theSpec
  val tracker = theTracker
  val report = reporter
  
  var systemStart: Long = 0
  var exampleStart: Long = 0
  var indentLevel: Int = 0
  
  def runStarting(examplesCount: Int) { }
  
  def scopeOpened(name: String) { 
    indentLevel += 1
    val formatter = Suite.getIndentedTextForInfo(name, indentLevel, false, false)
    report(InfoProvided(tracker.nextOrdinal(), name, Some(NameInfo(name, Some(spec.getClass.getName), Some(name))), None, None, Some(formatter)))
  }
  
  def scopeClosed(name: String) { 
    indentLevel -= 1
  }
  
  def systemStarting(systemName: String) {
    systemStart = System.currentTimeMillis
    val formatter = Suite.getIndentedTextForInfo(systemName, indentLevel, false, false)
    report(InfoProvided(tracker.nextOrdinal(), systemName, Some(NameInfo(systemName, Some(spec.getClass.getName), Some(systemName))), None, None, Some(formatter)))
  }
  
  def systemCompleted(systemName: String) { }
  
  def systemFailed(name: String, e: Throwable) { }
  
  def systemError(name: String, e: Throwable) { }
  
  def systemSkipped(name: String) { }
  
  def systemSucceeded(name: String) { }
  
  def exampleStarting(exampleName: String) {
    exampleStart = System.currentTimeMillis()
    report(TestStarting(tracker.nextOrdinal(), spec.getClass.getSimpleName, Some(spec.getClass.getName), exampleName, Some(MotionToSuppress), None))
  }
  
  def exampleSucceeded(testName: String) {
    val duration = System.currentTimeMillis() - exampleStart
    val formatter = Suite.getIndentedText(testName, indentLevel + 1, true)
    report(TestSucceeded(tracker.nextOrdinal(), spec.getClass.getSimpleName, Some(spec.getClass.getName), testName, Some(duration), Some(formatter), None))
  }
  
  def exampleFailed(testName: String, e: Throwable) {
    val duration = System.currentTimeMillis() - exampleStart
    val formatter = Suite.getIndentedText(testName, indentLevel + 1, true)
    report(TestFailed(tracker.nextOrdinal(), e.getMessage, spec.getClass.getSimpleName, Some(spec.getClass.getName), testName, Some(e), Some(duration), Some(formatter), None))
  }
  
  def exampleError(testName: String, e: Throwable) {
    val duration = System.currentTimeMillis() - exampleStart
    val formatter = Suite.getIndentedText(testName, indentLevel + 1, true)
    report(TestFailed(tracker.nextOrdinal(), e.getMessage, spec.getClass.getSimpleName, Some(spec.getClass.getName), testName, Some(e), Some(duration), Some(formatter), None))
  }
  
  def exampleSkipped(testName: String) {
    val formatter = Suite.getIndentedText(testName, indentLevel + 1, true)
    report(TestPending(tracker.nextOrdinal(), spec.getClass.getSimpleName, Some(spec.getClass.getName), testName, Some(formatter)))
  }
  
  def exampleCompleted(exampleName: String) { }
  
}