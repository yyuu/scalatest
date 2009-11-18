import java.io.File
import sbt.Logger
import sbt.FileUtilities.{readString, write}
import sbt.Control.{lazyFold, thread}

object Helper {
  def srcPath(tpe: String) = "src/" + tpe + "/scala/org/scalatest/matchers/"

  val srcBase = "app/"
  val targetBase = srcBase + "must_matchers/"

  def mappings =
    ("<code>must</code>", "<code>I_WAS_must_ORIGINALLY</code>") ::
    ("<!-- PRESERVE -->should", " I_MUST_STAY_SHOULD") ::
    ("<a href=\"MustMatchers.html\"><code>MustMatchers</code></a>",
        "<a href=\"I_WAS_Must_ORIGINALLYMatchers.html\"><code>I_WAS_Must_ORIGINALLYMatchers</code></a>") ::
    ("should", "must") ::
    ("Should", "Must") ::
    ("I_WAS_must_ORIGINALLY", "should") ::
    ("I_MUST_STAY_SHOULD", "should") ::
    ("I_WAS_Must_ORIGINALLY", "Should") ::
    Nil

  def translateShouldToMust(shouldLine: String): String =
    (shouldLine /: mappings) {
      case (s, (key, value)) =>  s.replaceAll(key, value)
    }
  
  def generateFile(tpe: String, srcFileName: String, targetFileName: String, log: Logger) = {
    log.info("srcPath(tpe): " + srcPath(tpe))
    log.info("targetFileName: " + targetFileName)
    log.info("srcFileName: " + srcFileName)
    val mustFile = new File(targetBase + srcPath(tpe) + targetFileName)
    val srcFile = new File(srcBase + srcPath(tpe) + srcFileName)
    log.info("generating: " + mustFile)
    thread(readString(srcFile, log)) { str => write(mustFile, translateShouldToMust(str), log) }
  }
}

import Helper._

object GenMustMatchers {
  def generate(log: Logger) = generateFile("main", "ShouldMatchers.scala", "MustMatchers.scala", log)
}

object GenMustMatchersTests {

  def generate(log: Logger) = {
    val shouldFileNames =
    List(
        "ShouldBehaveLikeSpec.scala",
        "ShouldContainElementSpec.scala",
        "ShouldContainKeySpec.scala",
        "ShouldContainValueSpec.scala",
        "ShouldEqualSpec.scala",
        "ShouldHavePropertiesSpec.scala",
        "ShouldLengthSpec.scala",
        "ShouldOrderedSpec.scala",
        "ShouldSizeSpec.scala",
        // "ShouldStackSpec.scala", now in examples
        // "ShouldStackFlatSpec.scala",
        "ShouldBeASymbolSpec.scala",
        "ShouldBeAnSymbolSpec.scala",
        "ShouldBeMatcherSpec.scala",
        "ShouldBePropertyMatcherSpec.scala",
        "ShouldBeSymbolSpec.scala",
        "ShouldEndWithRegexSpec.scala",
        "ShouldEndWithSubstringSpec.scala",
        "ShouldFullyMatchSpec.scala",
        "ShouldIncludeRegexSpec.scala",
        "ShouldIncludeSubstringSpec.scala",
        "ShouldLogicalMatcherExprSpec.scala",
        "ShouldMatcherSpec.scala",
        "ShouldPlusOrMinusSpec.scala",
        "ShouldSameInstanceAsSpec.scala",
        "ShouldStartWithRegexSpec.scala",
        "ShouldStartWithSubstringSpec.scala",
        "ShouldBeNullSpec.scala"
        )

    lazyFold(shouldFileNames) { shouldFileName =>
      val mustFileName = shouldFileName.replace("Should", "Must")
      generateFile("test", shouldFileName, mustFileName, log)
    }
  }
}
