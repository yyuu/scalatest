import sbt._
import Keys._
import Build.data
import org.scalatest.tools.Runner
import scala.collection.mutable.ArrayBuffer

object ScalaTestSbtPlugin extends Plugin
{
	// configuration points, like the built in `version`, `libraryDependencies`, or `compile`
	// by implementing Plugin, these are automatically imported in a user's `build.sbt`
	val stargs = SettingKey[String]("stargs")
        val stTask = InputKey[Unit]("st")

	// a group of settings ready to be added to a Project
	// to automatically add them, do 
	val stSettings = Seq(
		stargs := "", 
                stTask <<= inputTask { (argsTask: TaskKey[Seq[String]]) =>
                  (argsTask, fullClasspath in Test, stargs) map { (args: Seq[String], classPathList, stargs) =>
                    var arrayBuffer = new ArrayBuffer[String]()
                    val runpathList:Seq[String] = classPathList map {attr => attr.data.getAbsolutePath() }
                    arrayBuffer += "-p"
                    arrayBuffer += runpathList.mkString(" ")
                    if(args.length > 0) {
                      val dashIdx = args.indexOf("--")
                      if(dashIdx >= 0) {
                        // -- overrides stargs, let's break the arguments into 2 parts, part 1 for suite names and part 2 for override args.
                        val (suiteNames, otherArgsWithDash) = args.splitAt(dashIdx)
                        val otherArgs = otherArgsWithDash.tail // Get rid of the --
                        
                        // We'll translate suite into existing suite() and passed in together with other stargs
                        arrayBuffer ++= (for(suiteName <- suiteNames) yield { List("-s", suiteName) }).flatten
                        arrayBuffer ++= Runner.parseFriendlyParams(otherArgs.toArray)
                      }
                      else {
                        // Suite name only, will use stargs for other arguments.
                        // We'll translate suite into existing suite() and passed in together with other stargs
                        arrayBuffer ++= (for(arg <- args) yield { List("-s", arg) }).flatten
                        arrayBuffer ++= Runner.parseFriendlyParams(stargs.trim())
                      }
                    }
                    else if(stargs.trim() != "")
                      arrayBuffer ++= Runner.parseFriendlyParams(stargs.trim())
                    Runner.run(arrayBuffer.toArray.filter(!_.equals("")))
                  }
                }
	)

	// alternatively, by overriding `settings`, they could be automatically added to a Project
	// override val settings = Seq(...)
}
