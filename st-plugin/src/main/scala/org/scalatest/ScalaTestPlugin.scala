package org.scalatest
import sbt._
import scala.collection.mutable.ArrayBuffer
trait ScalaTestPlugin extends DefaultProject {

  private def parseFriendlyParams(friendlyArgs:Array[String]): Array[String] = {
    val (propsList, includesList, excludesList, repoArgsList, concurrentList, memberOnlyList, wildcardList, suiteList, junitList, testngList) = 
      new FriendlyParamsTranslator().parsePropsAndTags(friendlyArgs)
    val arrayBuffer = new ArrayBuffer[String]()
    arrayBuffer ++= propsList ::: includesList ::: excludesList ::: repoArgsList ::: concurrentList ::: memberOnlyList ::: wildcardList :::
                    suiteList ::: junitList ::: testngList
    arrayBuffer.toArray
  }

  private def runScalaTest(args: List[String], loader: ClassLoader[_]) {
    var arrayBuffer = new ArrayBuffer[String]()
    arrayBuffer += "-p"
    arrayBuffer += testCompilePath.toString + " " + mainCompilePath.toString
    val translator = new FriendlyParamsTranslator()
    if(args.length > 0) {
      val dashIdx = args.indexOf("--")
      if(dashIdx >= 0) {
        // -- overrides stargs, let's break the arguments into 2 parts, part 1 for suite names and part 2 for override args.
        val (suiteNames, otherArgsWithDash) = args.splitAt(dashIdx)
        val otherArgs = otherArgsWithDash.tail // Get rid of the --
                        
        // We'll translate suite into existing suite() and passed in together with other stargs
        //arrayBuffer ++= (for(suiteName <- suiteNames) yield { List("-s", suiteName) }).flatten
        for(suiteName <- suiteNames) {
            arrayBuffer += "-s"
            arrayBuffer += suiteName
        }
        arrayBuffer ++= parseFriendlyParams(otherArgs.toArray)
      }
      else {
        // Suite name only, will use stargs for other arguments.
        // We'll translate suite into existing suite() and passed in together with other stargs
        //arrayBuffer ++= (for(arg <- args) yield { List("-s", arg) }).flatten
        for(arg <- args) {
            arrayBuffer += "-s"
            arrayBuffer += arg
        }
        if (stargs.trim.length > 0)
          arrayBuffer ++= parseFriendlyParams(stargs.trim.split(" "))
      }
    }
    else if(stargs.trim() != "") 
      arrayBuffer ++= parseFriendlyParams(stargs.trim.split(" "))

    val runnerClass = loader.loadClass("org.scalatest.tools.Runner")
    val runMethod = runnerClass.getMethod("run", classOf[Array[String]])
    val runArgs = arrayBuffer.toArray.filter(!_.equals(""))
    runMethod.invoke(null, Array(runArgs)) // run method is static.
  }

  val stConf = config("st")
  def stargs = ""
  def stTask(args: Array[String]) = task {
    val (loader, path) = TestFramework.createTestLoader(testClasspath.get, buildScalaInstance0)
    runScalaTest(args.toList, loader)
    None
  }
  lazy val st = task { args => 
    stTask(args) dependsOn(testCompile) describedAs "Runs ScalaTest."
  }
}
