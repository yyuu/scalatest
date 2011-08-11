import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    name := "ScalaTest",
    version := "7.6",
    organization := "org.scalatest",
    scalaVersion := "2.9.0-1"
  )
}

object Dependencies {
  val testng = "org.testng" % "testng" % "5.7" from
    "http://repo1.maven.org/maven2/org/testng/testng/5.7/testng-5.7-jdk15.jar"
  val ant = "org.apache.ant" % "ant" % "1.7.1"
  val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.1"
  val scalacheck = "org.scala-tools.testing" % "scalacheck_2.9.0" % "1.9"
  val mockito = "org.mockito" % "mockito-all" % "1.6"
  val jmock = "org.jmock" % "jmock-legacy" % "2.5.1"
  val easymock = "org.easymock" % "easymockclassextension" % "3.0"
  val junit = "junit" % "junit" % "4.4"
  val cobertura = "net.sourceforge.cobertura" % "cobertura" % "1.9.1"
  val commons_io = "commons-io" % "commons-io" % "1.3.2"
  val cglib = "cglib" % "cglib-nodep" % "2.2"
  val allDeps = Seq(testng, ant, hamcrest, scalacheck, mockito, jmock, easymock,
                    cobertura, commons_io, cglib, junit)
}

object ScalaTestBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val scalatest = Project(
    id        = "scalatest",
    base      = file("."),
    settings  = buildSettings,
    aggregate = Seq(core, mustMatchers)
  )
  
  lazy val core = Project (
    id        = "scalatest-core",
    base      = file ("core"),
    settings  = buildSettings ++ Seq(
    /*settings  = buildSettings ++ Seq(
      (sourceGenerators in Compile) <+= (sourceManaged in Compile) map {
        dir => GenGen.genCompile(dir) ++ GenTable.genCompile(dir)
      },
      (sourceGenerators in Test) <+= (sourceManaged in Test) map {
        dir => GenGen.getTest(dir) ++ GenTable.genTest(dir)
      }*/
    ) ++ Seq (libraryDependencies ++= allDeps)
  )

  lazy val mustMatchers = Project(
    id           = "scalatest-must-matchers",
    base         = file("must-matchers"),
    dependencies = Seq(core),
    settings     = buildSettings ++ Seq(
      (sourceGenerators in Compile) <+= (sourceManaged in Compile) map {
        dir => GenMustMatchers.genCompile(dir)
      },
      (sourceGenerators in Test) <+= (sourceManaged in Test) map {
        dir => GenMustMatchers.genTest(dir) //++
      }
    )
  )

  /**
  val hello = TaskKey[Unit]("hello", "Prints 'Hello World'")
  val helloTask = hello := {
    GenTable.main(Array())
  }

  private val generated = "src" / "generated" / "scala" / "org" / "scalatest"
  private val tableSuite = generated / "prop" / "TableSuite.scala"
  private lazy val genTableSuite = fileTask(tableSuite from ("project" / "GenTable.scala")) {
    None
  }
  **/
}
