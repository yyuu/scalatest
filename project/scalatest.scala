import sbt._
import Keys._

object ScalatestBuild extends Build {

   val includeTestPackageSet = Set("org.scalatest", 
                                   "org.scalatest.fixture", 
                                   "org.scalatest.concurrent", 
                                   "org.scalatest.testng", 
                                   "org.scalatest.junit", 
                                   "org.scalatest.events", 
                                   "org.scalatest.prop", 
                                   "org.scalatest.tools", 
                                   "org.scalatest.matchers", 
                                   "org.scalatest.suiteprop", 
                                   "org.scalatest.mock", 
                                   "org.scalatest.path")

   val excludedSuiteSet = Set("org.scalatest.BigSuite", 
                              "org.scalatest.events.TestLocationJUnit3Suite", 
                              "org.scalatest.events.TestLocationJUnitSuite", 
                              "org.scalatest.events.TestLocationMethodJUnit3Suite", 
                              "org.scalatest.events.TestLocationMethodJUnitSuite", 
                              "org.scalatest.events.TestLocationMethodTestNGSuite", 
                              "org.scalatest.events.TestLocationTestNGSuite", 
                              "org.scalatest.tools.SomeApiClassRunner")

   lazy val root = Project("scalatest", file(".")) settings(
     organization := "org.scalatest",
     version := "2.0-SNAPSHOT",
     crossScalaVersions := Seq("2.8.1", "2.8.2","2.9.0","2.9.0-1","2.9.1-1-RC1","2.10.0-M2"),
     libraryDependencies ++= simpledependencies,
     libraryDependencies <+= scalaVersion apply {
       //TODO -1.7
       case sv @ ("2.8.2"|"2.8.1") => "org.scala-tools.testing" % ("scalacheck_"+sv) % "1.8"
       case _                      => "org.scala-tools.testing" % "scalacheck_2.9.0" % "1.9"
     },
     sourceGenerators in Compile <+= 
         (baseDirectory, sourceManaged in Compile) map genGenMain,
     sourceGenerators in Test <+= 
         (sourceManaged in Test) map genGenTest,
     sourceGenerators in Compile <+= 
         (sourceManaged in Compile) map genTableMain,
     sourceGenerators in Test <+= 
         (sourceManaged in Test) map genTableTest, 
     testOptions in Test := Seq(Tests.Filter(className => isIncludedTest(className)))
   )

   def isIncludedTest(className: String) = {
     try {
       val packageName = className.substring(0, className.lastIndexOf("."))
       includeTestPackageSet.contains(packageName) && !excludedSuiteSet.contains(className)
     }
     catch {
       case e: Exception => 
         e.printStackTrace()
         false
     }
   }

   def simpledependencies = Seq(
     "org.scala-tools.testing" % "test-interface" % "0.5",  // TODO optional
     "org.easymock" % "easymockclassextension" % "3.1",   // TODO optional
     "org.jmock" % "jmock-legacy" % "2.5.1", // TODO optional
     "org.mockito" % "mockito-all" % "1.9.0", // TODO optional
     "org.testng" % "testng" % "6.3.1",  // TODO optional
     "com.google.inject" % "guice" % "3.0", // TODO optional
     "junit" % "junit" % "4.10", // TODO optional
     "org.apache.ant" % "ant" % "1.7.1", // TODO optional
     "net.sourceforge.cobertura" % "cobertura" % "1.9.1" % "test",
     "commons-io" % "commons-io" % "1.3.2" % "test"
  )

  // TODO - Make sure this directory is really used in the future.
  def genGenMain(basedir: File, dir: File): Seq[File] = {
    val gengenSource = basedir / "project" / "GenGen.scala"
    // dir = mainsrc
    val mainsrc = file("target/generated/src/main/scala/org/scalatest/prop")
    (mainsrc ** "*.scala").get
    
    def results = (mainsrc ** "*.scala").get
    if(results.isEmpty || results.exists(_.lastModified < gengenSource.lastModified))
      GenGen.main(Array.empty)

    results
  }
  def genGenTest(dir: File): Seq[File] = {
    // TODO - Use GenGen to make test files
    val testsrc= file("target/generated/src/test/scala/org/scalatest/prop")
    (testsrc ** "*.scala").get
  }

  // TODO - Make sure this directory is really used in the future.
  def genTableMain(dir: File): Seq[File] = {
    GenTable.main(Array.empty)
    // dir = mainsrc
    val mainsrc = file("target/generated/src/main/scala/org/scalatest/prop")
    (mainsrc ** "Table*.scala").get
  }

  def genTableTest(dir: File): Seq[File] = {
    // TODO - Use GenGen to make test files
    val testsrc= file("target/generated/src/test/scala/org/scalatest/prop")
    (testsrc ** "Table*.scala").get
  }

}