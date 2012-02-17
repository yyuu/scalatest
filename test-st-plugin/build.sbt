name := "A Project"
 
version := "0.1.0"
 
scalaVersion := "2.9.1"

seq( ScalaTestSbtPlugin.stSettings : _*)

stargs := """graphic"""

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.9.1" % "1.7.1"
)
