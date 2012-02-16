import sbt._
class ScalaTestPluginProject(info: ProjectInfo) extends PluginProject(info) {
  val scalatest = "org.scalatest" % "scalatest" % "1.1"
}
