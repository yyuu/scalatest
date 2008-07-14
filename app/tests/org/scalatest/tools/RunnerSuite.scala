/*
 * Copyright 2001-2008 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.tools

class RunnerSuite() extends Suite {

  def testParseArgsIntoLists() {

    // this is how i solved the problem of wanting to reuse these val names, runpathList, reportersList, etc.
    // by putting them in a little verify method, it gets reused each time i call that method
    def verify(
      args: Array[String],
      expectedRunpathList: List[String],
      expectedReporterList: List[String],
      expectedSuitesList: List[String],
      expectedPropsList: List[String],
      expectedIncludesList: List[String],
      expectedExcludesList: List[String],
      expectedConcurrentList: List[String],
      expectedMemberOfList: List[String],
      expectedBeginsWithList: List[String],
      expectedTestNGList: List[String]
    ) = {

      val (
        runpathList,
        reportersList,
        suitesList,
        propsList,
        includesList,
        excludesList,
        concurrentList,
        memberOfList,
        beginsWithList,
        testNGList
      ) = RunnerArgsParser.parseArgs(args)
      
      assert(runpathList === expectedRunpathList)
      assert(reportersList === expectedReporterList)
      assert(suitesList === expectedSuitesList)
      assert(propsList === expectedPropsList)
      assert(includesList === expectedIncludesList)
      assert(excludesList === expectedExcludesList)
      assert(concurrentList === expectedConcurrentList)
      assert(memberOfList === expectedMemberOfList)
      assert(beginsWithList === expectedBeginsWithList)
      assert(testNGList === expectedTestNGList)
    }

    verify(
      Array("-g", "-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188", "-p",
            "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-g", "-f", "file.out", "-p"),
      List("-p", "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-p"),
      List("-g", "-g", "-f", "file.out"),
      Nil,
      List("-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188"),
      Nil,
      Nil,
      Nil,
      Nil,
      Nil,
      Nil
    )

    verify(
      Array("-g", "-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188", "-p",
            "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-g", "-f", "file.out",
            "-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-p", "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\""),
      List("-g", "-g", "-f", "file.out"),
      List("-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188"),
      Nil,
      Nil,
      Nil,
      Nil,
      Nil,
      Nil
    )

    verify(
      Array(),
      Nil,
      Nil,
      Nil,
      Nil,
      Nil,
      Nil,
      Nil,
      Nil,
      Nil,
      Nil
    )

    verify(
      Array("-g", "-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188", "-p",
            "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-g", "-f", "file.out",
            "-n", "JustOne", "-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-p", "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\""),
      List("-g", "-g", "-f", "file.out"),
      List("-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188"),
      List("-n", "JustOne"),
      Nil,
      Nil,
      Nil,
      Nil,
      Nil
    )

    verify(
      Array("-g", "-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188", "-p",
            "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-g", "-f", "file.out",
            "-n", "One Two Three", "-x", "SlowTests", "-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-p", "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\""),
      List("-g", "-g", "-f", "file.out"),
      List("-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188"),
      List("-n", "One Two Three"),
      List("-x", "SlowTests"),
      Nil,
      Nil,
      Nil,
      Nil
    )

    verify(
      Array("-c", "-g", "-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188", "-p",
            "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-g", "-f", "file.out",
            "-n", "One Two Three", "-x", "SlowTests", "-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-p", "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\""),
      List("-g", "-g", "-f", "file.out"),
      List("-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188"),
      List("-n", "One Two Three"),
      List("-x", "SlowTests"),
      List("-c"),
      Nil,
      Nil,
      Nil
    )

    verify(
      Array("-c", "-g", "-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188", "-p",
          "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-g", "-f", "file.out",
          "-n", "One Two Three", "-x", "SlowTests", "-s", "SuiteOne", "-s", "SuiteTwo", "-m", "com.example.webapp",
          "-w", "com.example.root"),
      List("-p", "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\""),
      List("-g", "-g", "-f", "file.out"),
      List("-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188"),
      List("-n", "One Two Three"),
      List("-x", "SlowTests"),
      List("-c"),
      List("-m", "com.example.webapp"),
      List("-w", "com.example.root"),
      Nil
    )
    // Try a TestNGSuite
    verify(
      Array("-c", "-g", "-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188", "-p",
          "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\"", "-g", "-f", "file.out",
          "-n", "One Two Three", "-x", "SlowTests", "-s", "SuiteOne", "-s", "SuiteTwo", "-m", "com.example.webapp",
          "-w", "com.example.root", "-t", "some/path/file.xml"),
      List("-p", "\"serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar\""),
      List("-g", "-g", "-f", "file.out"),
      List("-s", "SuiteOne", "-s", "SuiteTwo"),
      List("-Dincredible=whatshername", "-Ddbname=testdb", "-Dserver=192.168.1.188"),
      List("-n", "One Two Three"),
      List("-x", "SlowTests"),
      List("-c"),
      List("-m", "com.example.webapp"),
      List("-w", "com.example.root"),
      List("-t", "some/path/file.xml")
    )
  }

  def testParseCompoundArgIntoSet() {
    expect(Set("Cat", "Dog")) {
      RunnerArgsParser.parseCompoundArgIntoSet(List("-n", "Cat Dog"), "-n")
    }
  }

  def testParseConfigSet() {

    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseConfigSet(null)
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseConfigSet("-fX")
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseConfigSet("-oYZTFUPBISARG-")
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseConfigSet("-")
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseConfigSet("")
    }

    expect(ReporterOpts.Set32(ReporterOpts.PresentRunStarting.mask32)) {
      RunnerArgsParser.parseConfigSet("-oY")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentTestStarting.mask32)) {
      RunnerArgsParser.parseConfigSet("-oZ")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentTestSucceeded.mask32)) {
      RunnerArgsParser.parseConfigSet("-oT")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentTestFailed.mask32)) {
      RunnerArgsParser.parseConfigSet("-oF")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentSuiteStarting.mask32)) {
      RunnerArgsParser.parseConfigSet("-oU")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentSuiteCompleted.mask32)) {
      RunnerArgsParser.parseConfigSet("-oP")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentSuiteAborted.mask32)) {
      RunnerArgsParser.parseConfigSet("-oB")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentInfoProvided.mask32)) {
      RunnerArgsParser.parseConfigSet("-oI")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentRunStopped.mask32)) {
      RunnerArgsParser.parseConfigSet("-oS")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentRunAborted.mask32)) {
      RunnerArgsParser.parseConfigSet("-oA")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentRunCompleted.mask32)) {
      RunnerArgsParser.parseConfigSet("-oR")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentTestIgnored.mask32)) {
      RunnerArgsParser.parseConfigSet("-oG")
    }
    expect(ReporterOpts.Set32(0)) {
      RunnerArgsParser.parseConfigSet("-f")
    }

    expect(ReporterOpts.Set32(ReporterOpts.PresentRunStarting.mask32 | ReporterOpts.PresentTestStarting.mask32)) {
      RunnerArgsParser.parseConfigSet("-oZY")
    }
    expect(ReporterOpts.Set32(ReporterOpts.PresentRunStarting.mask32 | ReporterOpts.PresentTestStarting.mask32)) {
      RunnerArgsParser.parseConfigSet("-oYZ") // Just reverse the order of the params
    }
    val allOpts = ReporterOpts.Set32(
      ReporterOpts.PresentRunStarting.mask32 |
      ReporterOpts.PresentTestStarting.mask32 |
      ReporterOpts.PresentTestSucceeded.mask32 |
      ReporterOpts.PresentTestFailed.mask32 |
      ReporterOpts.PresentSuiteStarting.mask32 |
      ReporterOpts.PresentSuiteCompleted.mask32 |
      ReporterOpts.PresentSuiteAborted.mask32 |
      ReporterOpts.PresentInfoProvided.mask32 |
      ReporterOpts.PresentRunStopped.mask32 |
      ReporterOpts.PresentRunAborted.mask32 |
      ReporterOpts.PresentRunCompleted.mask32 |
      ReporterOpts.PresentTestIgnored.mask32
    )
    expect(allOpts) {
      RunnerArgsParser.parseConfigSet("-oYZTFUPBISARG")
    }
  }

  def testParseReporterArgsIntoSpecs() {
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(null)
    }
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("Hello", null, "World"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("Hello", "-", "World"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("Hello", "", "World"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-g", "-x", "-o"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("Hello", " there", " world!"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-g", "-o", "-g", "-e"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-o", "-o", "-g", "-e"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-e", "-o", "-g", "-e"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-f")) // Can't have -f last, because need a file name
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-r")) // Can't have -r last, because need a reporter class
    }
    expect(new ReporterSpecs(None, Nil, None, None, Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(Nil)
    }
    expect(new ReporterSpecs(Some(new GraphicReporterSpec(ReporterOpts.Set32(0))), Nil, None, None, Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-g"))
    }
    expect(new ReporterSpecs(Some(new GraphicReporterSpec(ReporterOpts.Set32(ReporterOpts.PresentTestFailed.mask32))), Nil, None, None, Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-gF"))
    }
    expect(new ReporterSpecs(None, Nil, Some(new StandardOutReporterSpec(ReporterOpts.Set32(0))), None, Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-o"))
    }
    expect(new ReporterSpecs(None, Nil, Some(new StandardOutReporterSpec(ReporterOpts.Set32(ReporterOpts.PresentTestFailed.mask32))), None, Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-oF"))
    }
    expect(new ReporterSpecs(None, Nil, None, Some(new StandardErrReporterSpec(ReporterOpts.Set32(0))), Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-e"))
    }
    expect(new ReporterSpecs(None, Nil, None, Some(new StandardErrReporterSpec(ReporterOpts.Set32(ReporterOpts.PresentTestFailed.mask32))), Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-eF"))
    }
    expect(new ReporterSpecs(None, List(new FileReporterSpec(ReporterOpts.Set32(0), "theFilename")), None, None, Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-f", "theFilename"))
    }
    expect(new ReporterSpecs(None, List(new FileReporterSpec(ReporterOpts.Set32(ReporterOpts.PresentTestFailed.mask32), "theFilename")), None, None, Nil)) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-fF", "theFilename"))
    }
    expect(new ReporterSpecs(None, Nil, None, None, List(new CustomReporterSpec(ReporterOpts.Set32(0), "the.reporter.Class")))) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-r", "the.reporter.Class"))
    }
    expect(new ReporterSpecs(None, Nil, None, None, List(new CustomReporterSpec(ReporterOpts.Set32(ReporterOpts.PresentTestFailed.mask32), "the.reporter.Class")))) {
      RunnerArgsParser.parseReporterArgsIntoSpecs(List("-rF", "the.reporter.Class"))
    }
  }

  def testParseSuiteArgsIntoClassNameStrings() {
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseSuiteArgsIntoNameStrings(null, "-s")
    }
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseSuiteArgsIntoNameStrings(List("-s", null, "-s"), "-s")
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseSuiteArgsIntoNameStrings(List("-s", "SweetSuite", "-s"), "-s")
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseSuiteArgsIntoNameStrings(List("-s", "SweetSuite", "-s", "-s"), "-s")
    }
    expect(List("SweetSuite", "OKSuite")) {
      RunnerArgsParser.parseSuiteArgsIntoNameStrings(List("-s", "SweetSuite", "-s", "OKSuite"), "-s")
    }
    expect(List("SweetSuite", "OKSuite", "SomeSuite")) {
      RunnerArgsParser.parseSuiteArgsIntoNameStrings(List("-s", "SweetSuite", "-s", "OKSuite", "-s", "SomeSuite"), "-s")
    }
  }

  def testParseRunpathArgIntoList() {
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseRunpathArgIntoList(null)
    }
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p", null))
    }
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parseRunpathArgIntoList(List(null, "serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p", "bla", "bla"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-pX", "bla"))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p", "  "))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p", "\t"))
    }
    expect(List("bla")) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p", "bla"))
    }
    expect(List("bla", "bla", "bla")) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p", "bla bla bla"))
    }
    expect(List("serviceuitest-1.1beta4.jar", "myjini", "http://myhost:9998/myfile.jar")) {
      RunnerArgsParser.parseRunpathArgIntoList(List("-p", "serviceuitest-1.1beta4.jar myjini http://myhost:9998/myfile.jar"))
    }
  }

  def testParsePropertiesArgsIntoMap() {
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parsePropertiesArgsIntoMap(null)
    }
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.parsePropertiesArgsIntoMap(List("-Da=b", null))
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parsePropertiesArgsIntoMap(List("-Dab")) // = sign missing
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parsePropertiesArgsIntoMap(List("ab")) // needs to start with -D
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parsePropertiesArgsIntoMap(List("-D=ab")) // no key
    }
    intercept(classOf[IllegalArgumentException]) {
      RunnerArgsParser.parsePropertiesArgsIntoMap(List("-Dab=")) // no value
    }
    expect(Map("a" -> "b", "cat" -> "dog", "Glorp" -> "Glib")) {
      RunnerArgsParser.parsePropertiesArgsIntoMap(List("-Da=b", "-Dcat=dog", "-DGlorp=Glib"))
    }
  }

  def testCheckArgsForValidity() {
    intercept(classOf[NullPointerException]) {
      RunnerArgsParser.checkArgsForValidity(null)
    }
    expect(None) {
      RunnerArgsParser.checkArgsForValidity(Array("-Ddbname=testdb", "-Dserver=192.168.1.188", "-p", "serviceuitest-1.1beta4.jar", "-g", "-eFBA", "-s", "MySuite"))
    }
    assert(RunnerArgsParser.checkArgsForValidity(Array("-Ddbname=testdb", "-Dserver=192.168.1.188", "-z", "serviceuitest-1.1beta4.jar", "-g", "-eFBA", "-s", "MySuite")) != None)
    expect(None) {
      RunnerArgsParser.checkArgsForValidity(Array("-Ddbname=testdb", "-Dserver=192.168.1.188", "-p", "serviceuitest-1.1beta4.jar", "-g", "-eFBA", "-s", "MySuite", "-c"))
    }
  }

/*
  def testRunpathPropertyAddedToPropertiesMap() {
    val a = new Suite {
      var theProperties: Map[String, Any] = Map()
      override def execute(testName: Option[String], reporter: Reporter, stopper: Stopper, includes: Set[String], excludes: Set[String],
          properties: Map[String, Any], distributor: Option[Distributor]) {
        theProperties = properties
      }
    }

    val dispatchReporter = new DispatchReporter(Nil, System.out)
    val suitesList = List("org.scalatest.usefulstuff.RunpathPropCheckerSuite")

    // Runner.doRunRunRunADoRunRun(new DispatchReporter)
    // Runner.doRunRunRunADoRunRun(dispatchReporter, suitesList, new Stopper {}, Set(), Set(), Map(), false,
         List(), List(), runpath: "build_tests", loader: ClassLoader,
      doneListener: RunDoneListener) = {

    ()
  }
}

package org.scalatest.usefulstuff {

  class RunpathPropCheckerSuite extends Suite {
    var theProperties: Map[String, Any] = Map()
    override def execute(testName: Option[String], reporter: Reporter, stopper: Stopper, includes: Set[String], excludes: Set[String],
        properties: Map[String, Any], distributor: Option[Distributor]) {
      theProperties = properties
    }
  }
*/
}
