import sbt._
class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val scalatestplugin = "org.scalatest" % "sbtplugin" % "0.1.0"
}
