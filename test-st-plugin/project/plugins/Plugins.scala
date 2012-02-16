import sbt._
class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val scalatest = "org.scalatest" % "scalatest" % "1.1"
  val scalatestplugin = "org.scalatest" % "sbtplugin" % "0.1.0"
}
