package org.scalatest.tools

import org.scalatest.Reporter
import org.scalatest.events._
import scala.collection.mutable

class ConsoleProgressBarReporter extends Reporter {
  
  private val WIDTH = 60
  private val RED = "\033[31m"
  private val GREEN = "\033[32m"
  private val BOLD = "\033[1m"
  private val NORMAL = "\033[0m"
    
  private[scalatest] case class ErrorData(suiteClassName: Option[String], formattedName: String, throwable: Option[Throwable])
  private[scalatest] class Duration(val inMilliseconds: Long) {
    lazy val inSeconds = inMilliseconds / 1000
    lazy val inMinutes = inSeconds / 60
  }

  private var total = 0
  private var count = 0
  private var failed = 0
  private var ignored = 0
  private var pending = 0
  private val errors = new mutable.ListBuffer[ErrorData]

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
        updateDisplay()
      }
      case TestFailed(ordinal, message, suiteName, suiteClassName, testName, throwable, duration, formatter, rerunner, payload, threadName, timestamp) => {
        count += 1
        failed += 1
        errors += ErrorData(suiteClassName, "%s: %s".format(suiteName, testName), throwable)
        updateDisplay()
      }
      case TestIgnored (ordinal, suiteName, suiteClassName, testName, formatter, payload, threadName, timeStamp) => 
        count += 1
        ignored += 1
        updateDisplay()
      case TestPending (ordinal, suiteName, suiteClassName, testName, formatter, payload, threadName, timeStamp) => 
        count += 1
        pending += 1
        updateDisplay()
      case RunCompleted(ordinal, duration, summary, formatter, payload, threadName, timestamp) => {
        println()
        if (duration.isDefined)
          println("Finished in " + durationToHuman(new Duration(duration.get)))
        else
          println("Finished, duration not available.")
        println()
        errors.foreach { error =>
          println(RED + "FAILED" + NORMAL + ": " + error.formattedName)
          error.throwable match {
            case Some(errorThrowable) => 
              buildStackTrace(errorThrowable, error.suiteClassName.getOrElse(""), 50).foreach(println)
            case None =>
              // Do nothing, probably not going to happen.
          }
          println()
        }
      }
      case _ =>
    }
  }

  def updateDisplay() {
    val hashes = (WIDTH * count.toDouble / total).toInt
    val bar = (if (failed > 0) RED else GREEN) + ("#" * hashes) + (" " * (WIDTH - hashes)) + NORMAL
    val note = if (failed > 0) "(errors: %d)".format(failed) else ""
    print("\n [%s] %d/%d %s ".format(bar, count, total, note))
  }

  def durationToHuman(x: Duration) = {
    "%d:%02d.%03d".format(x.inMinutes, x.inSeconds % 60, x.inMilliseconds % 1000)
  }

  def buildStackTrace(throwable: Throwable, highlight: String, limit: Int): List[String] = {
    var out = new mutable.ListBuffer[String]
    if (limit > 0) {
      out ++= throwable.getStackTrace.map { elem =>
        val line = "    at %s".format(elem.toString)
        if (line contains highlight) {
          BOLD + line + NORMAL
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