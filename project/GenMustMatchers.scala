/*
 * Copyright 2001-2011 Artima, Inc.
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
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import scala.io.Source
import scala.io.Codec // for 2.8

object ShouldTranslatorHelper {

  implicit val codec = Codec.default // for 2.8
  
  def translateShouldToMust(shouldLine: String): String = {
    shouldLine.replaceAll("<code>must</code>", "<code>I_WAS_must_ORIGINALLY</code>").
      replaceAll("<!-- PRESERVE -->should", " I_MUST_STAY_SHOULD").
      replaceAll(
        "<a href=\"MustMatchers.html\"><code>MustMatchers</code></a>",
        "<a href=\"I_WAS_Must_ORIGINALLYMatchers.html\"><code>I_WAS_Must_ORIGINALLYMatchers</code></a>").
      replaceAll("should", "must").
      replaceAll("Should", "Must").
      replaceAll("I_WAS_must_ORIGINALLY", "should").
      replaceAll("I_MUST_STAY_SHOULD", "should").
      replaceAll("I_WAS_Must_ORIGINALLY", "Should")
  }

  def translateShouldToDeprecatedShould(shouldLine: String): String = {
    shouldLine.replaceAll("Should", "DeprecatedShould")
  }

  def generateFile(srcFile: File, targetFile: File, translator: String => String) = {
    val writer = new BufferedWriter(new FileWriter(targetFile))
    try {
      val shouldLines = Source.fromFile(srcFile).getLines().toList // for 2.8
      for (shouldLine <- shouldLines) {
        val mustLine = translator(shouldLine)
        writer.write(mustLine)
        writer.newLine() // add for 2.8
      }
    }
    finally writer.close()
    targetFile
  }
}

import ShouldTranslatorHelper._

object GenMustMatchers {
  def genCompile(baseDir:File) = {
    val scalatestDir = new File(baseDir, "scala/org/scalatest")
    new File(scalatestDir, "matchers").mkdirs()
    new File(scalatestDir, "junit").mkdirs()
    def src(name:String) = new File("matchers/src/main/scala/org/scalatest/" + name)
    def target(name:String) = new File(scalatestDir, name)
    def translate(in:File, out:File) = generateFile(in, out, translateShouldToMust)
    List(
      translate(src("matchers/ShouldMatchers.scala"), target("matchers/MustMatchers.scala")),
      translate(src("junit/ShouldMatchersForJUnit.scala"), target("junit/MustMatchersForJUnit.scala"))
    )
  }

  def genTest(baseDir:File) = {
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

    val matchersDir = new File(baseDir, "scala/org/scalatest/matchers")
    matchersDir.mkdirs()
    val junitDir = new File(baseDir, "scala/org/scalatest/junit")
    junitDir.mkdirs()

    val matchers = for (shouldFileName <- shouldFileNames) yield generateFile(
      new File("matchers/src/test/scala/org/scalatest/matchers/" + shouldFileName),
      new File(matchersDir, shouldFileName.replace("Should", "Must")),
      translateShouldToMust
    )

    val junitMatchers = generateFile(
      new File("matchers/src/test/scala/org/scalatest/junit/ShouldMatchersForJUnitWordSpec.scala"),
      new File(junitDir, "MustMatchersForJUnitWordSpec.scala"),
      translateShouldToMust
    )

    matchers ++ List(junitMatchers)
  }
}


object GenDeprecatedShouldMatchersTests {

  def genTest(baseDir:File) = {

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

    val matchersDir = new File(baseDir, "scala/org/scalatest/matchers")
    matchersDir.mkdirs()

    for (shouldFileName <- shouldFileNames) yield generateFile(
      new File("matchers/src/test/scala/org/scalatest/matchers/" + shouldFileName),
      new File(matchersDir, shouldFileName.replace("Should", "DeprecatedShould")),
      translateShouldToDeprecatedShould
    )
  }
}
