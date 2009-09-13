import sbt._

class ScalaTestProject(info: ProjectInfo) extends ParentProject(info) {

  override def parallelExecution = true
  lazy val main = project("app", "ScalaTest", new Main(_))
  lazy val must = project("app" / "must_matchers", "must_matchers", new MustMatchers(_), main)

  class ScalaTestParentProject(info: ProjectInfo) extends DefaultProject(info) with AutoCompilerPlugins {
    override def crossScalaVersions = Set("2.7.5")
    val sxr = compilerPlugin("org.scala-tools.sxr" %% "sxr" % "0.2.1")
    // puts sbt on the classpath
    override def compileClasspath = super.compileClasspath +++ Path.fromFile(FileUtilities.sbtJar)
  }

  class Main(info: ProjectInfo) extends ScalaTestParentProject(info){
    override def packagePaths = super.packagePaths +++ must.packagePaths
    override def packageAction = super.packageAction dependsOn(must.compile)
    override def testSourceRoots = super.testSourceRoots +++ ("src" / "examples")

    // dependencies
    val junit = "junit" % "junit" % "4.4"
    val testng = "org.testng" % "testng" % "5.7" from
                 "http://repo1.maven.org/maven2/org/testng/testng/5.7/testng-5.7-jdk15.jar"
    val ant = "org.apache.ant" % "ant" % "1.7.1"
    val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.1"
    val scalacheck = "org.scala-tools.testing" % "scalacheck" % "1.5"
    val mockito = "org.mockito" % "mockito-all" % "1.7"
    val jmock = "org.jmock" % "jmock" % "2.5.1"
    val easymock = "org.easymock" % "easymock" % "2.5.1"

   /** cobertura.jar   code coverage ... **/

    // which tests to run
    override def includeTest( name: String ) = {
      def exclude(names:String*): Boolean = ! names.find(name startsWith _ ).isDefined

      exclude("org.scalatest.testng.example",
              "org.scalatest.testng.testpackage",
              "org.scalatest.tools",
              "org.scalatest.junit.helpers") &&
      name.startsWith("org.scalatest.PackageAccess")
    }
  }

  class MustMatchers(info: ProjectInfo) extends ScalaTestParentProject(info) {
    lazy val generateSrc = task { GenMustMatchers.generate(log) }
    override def compileAction = super.compileAction dependsOn(generateSrc)

    lazy val generateTestSrc = task { GenMustMatchersTests.generate(log) }
    override def testCompileAction = super.testCompileAction dependsOn(generateTestSrc)
  }
}