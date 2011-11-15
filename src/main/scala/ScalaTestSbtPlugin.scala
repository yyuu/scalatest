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
                    if(args.length > 0)
                      arrayBuffer ++= Runner.parseFriendlyParams(args.toArray)
                    else if(stargs.trim() != "")
                      arrayBuffer ++= Runner.parseFriendlyParams(stargs.trim())
                    Runner.run(arrayBuffer.toArray.filter(!_.equals("")))
                  }
                }
	)

	// alternatively, by overriding `settings`, they could be automatically added to a Project
	// override val settings = Seq(...)
}
