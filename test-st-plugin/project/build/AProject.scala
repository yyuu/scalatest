import sbt._
class AProject(info: ProjectInfo) extends DefaultProject(info) with org.scalatest.ScalaTestPlugin {
  // val scalatest = "org.scalatest" % "scalatest" % "1.1"
  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.7.1"
  override def stargs = "stdout"
}
