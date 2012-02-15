package org.scalatest.tools

import org.scalatest.Reporter
import org.scalatest.events._
import scala.collection.mutable

class ConsoleProgressBarReporter extends Reporter {
  
  private val WIDTH = 60
   
  private[scalatest] class Duration(val inMilliseconds: Long) {
    lazy val inSeconds = inMilliseconds / 1000
    lazy val inMinutes = inSeconds / 60
  }

  private var total = 0
  private var count = 0
  private var failed = 0
  private var ignored = 0
  private var pending = 0
  private var dotCount = 1
  
  def apply(event: Event) {
    event match {
      case RunStarting(ordinal, testCount, configMap, formatter, payload, threadName, timestamp) => {
        total = testCount
        count = 0
        failed = 0
        println()
        println("Running %d tests:".format(total))
      }
      case TestSucceeded(ordinal, suiteName, suiteClassName, testName, duration, formatter, rerunner, payload, threadName, timestamp) => {
        count += 1
        updateDisplay(PrintReporter.ansiGreen)
      }
      case TestFailed(ordinal, message, suiteName, suiteClassName, testName, throwable, duration, formatter, rerunner, payload, threadName, timestamp) => {
        count += 1
        failed += 1
        printErrorStackTrace("FAILED: " + "%s: %s".format(suiteName, testName), throwable, suiteClassName)
      }
      case TestIgnored (ordinal, suiteName, suiteClassName, testName, formatter, payload, threadName, timeStamp) => 
        count += 1
        ignored += 1
        updateDisplay(PrintReporter.ansiYellow)
      case TestPending (ordinal, suiteName, suiteClassName, testName, formatter, payload, threadName, timeStamp) => 
        count += 1
        pending += 1
        updateDisplay(PrintReporter.ansiYellow)
      case RunCompleted(ordinal, duration, summary, formatter, payload, threadName, timestamp) => {
        if (dotCount % WIDTH != 0)
          printProgressBar
        if (duration.isDefined)
          println("Finished in " + durationToHuman(new Duration(duration.get)))
        else
          println("Finished, duration not available.")
      }
      case SuiteAborted(ordinal, message, suiteName, suiteClassName, throwable, duration, formatter, rerunner, payload, threadName, timeStamp) => 
        printErrorStackTrace("SUITE ABORTED: ", throwable, suiteClassName)
      case RunAborted(ordinal, message, throwable, duration, summary, formatter, payload, threadName, timeStamp) => 
        printErrorStackTrace("RUN ABORTED: ", throwable, None)
      case RunStopped(ordinal, duration, summary, formatter, payload, threadName, timeStamp) => 
        printErrorStackTrace("RUN STOPPED", None, None)
      case _ =>
    }
  }
  
  def printErrorStackTrace(errorMessage: String, throwableOpt: Option[Throwable], suiteClassNameOpt: Option[String]) {
    println()
    println(PrintReporter.ansiRed + errorMessage)
    throwableOpt match {
      case Some(t) => 
        buildStackTrace(t, suiteClassNameOpt.getOrElse(""), 50).foreach(println)
      case None => 
        // Do nothing, probably not going to happen.
    }
    print(PrintReporter.ansiReset)
    dotCount = 1
  }

  def updateDisplay(color: String) {
    if (dotCount % WIDTH == 0)
      printProgressBar()
    else {
      print(color + "." + PrintReporter.ansiReset)
      System.out.flush() // Make sure it get flushed to output.
      dotCount += 1
    }
  }
  
  def printProgressBar() {
    val hashes = (WIDTH * count.toDouble / total).toInt
    val bar = (if (failed > 0) PrintReporter.ansiRed else PrintReporter.ansiGreen) + ("#" * hashes) + (" " * (WIDTH - hashes)) + PrintReporter.ansiReset
    val note = if (failed > 0) "(errors: %d)".format(failed) else ""
    println()
    print(" [%s] %d/%d %s ".format(bar, count, total, note))
    println()
    dotCount = 1
  }

  def durationToHuman(x: Duration) = {
    "%d:%02d.%03d".format(x.inMinutes, x.inSeconds % 60, x.inMilliseconds % 1000)
  }

  def buildStackTrace(throwable: Throwable, highlight: String, limit: Int): List[String] = {
    var out = new mutable.ListBuffer[String]
    out += throwable.getMessage
    if (limit > 0) {
      out ++= throwable.getStackTrace.map { elem =>
        val line = "    at %s".format(elem.toString)
        if (line contains highlight) {
          PrintReporter.ansiBold + line + PrintReporter.ansiReset + PrintReporter.ansiRed
        } else {
          line
        }
      }
      if (out.length > limit) {
        out.trimEnd(out.length - limit)
        out += "    (...more...)"
      }
    }
    if ((throwable.getCause ne null) && (throwable.getCause ne throwable)) {
      out += "Caused by %s".format(throwable.getCause.toString)
      out ++= buildStackTrace(throwable.getCause, highlight, limit)
    }
    out.toList
  }

}