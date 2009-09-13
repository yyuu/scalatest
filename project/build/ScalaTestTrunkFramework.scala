import sbt._

object ScalaTestTrunkFramework extends LazyTestFramework
{
   val name = "ScalaTest"

   def testSuperClassName = "org.scalatest.Suite"
   def testSubClassType = ClassType.Class

   def testRunnerClassName = "org.scalatest.sbt.ScalaTestRunner"
}
