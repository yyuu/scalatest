package org.scalatest.path

import org.scalatest.Suite
import org.scalatest.OneInstancePerTest
import org.scalatest.Reporter
import org.scalatest.Stopper
import org.scalatest.Filter
import org.scalatest.Tracker
import org.scalatest.Distributor
import org.scalatest.Engine
import org.scalatest.Informer
import org.scalatest.Tag
import org.scalatest.verb.BehaveWord
import scala.collection.immutable.ListSet

trait FunSpec extends org.scalatest.Suite with OneInstancePerTest { thisSuite =>
  
  // This one is no longer used. Disentanglement
  final override def withFixture(test: NoArgTest) {
    throw new UnsupportedOperationException
  }
  
  import FunSpec.getPath  
  private val targetPath: Option[List[Int]] = getPath
  
  private final val engine = new Engine("concurrentSpecMod", "Spec")
  import engine._
  
  // TODO: Should these be private? What are they used for?
  private val sourceFileName = "FunSpec.scala"
  
  private var currentPath = List.empty[Int]
  private var usedPathSet = Set.empty[String]
  
  private def getNextPath() = {
    var next: List[Int] = null
    var count = 0
    while (next == null) {
      val candidate = currentPath ::: List(count)
      if (!usedPathSet.contains(candidate.toList.toString)) {
        next = candidate
        usedPathSet += candidate.toList.toString
      }
      else
        count += 1
    }
    next
  }

  private var testsResultsRegistered = false
  private def ensureTestResultsRegistered() {
    synchronized {
       testsResultsRegistered = true 
    }
  }
  
  /*
   * First time this is instantiated, targetPath will be null. In that case, execute the
   * first test, and each describe clause on the way to the first test (the all zeros path).
   */
  private def isInTargetPath(currentPath: List[Int], targetPath: Option[List[Int]]): Boolean = {
    def allZeros(xs: List[Int]) = xs.count(_ == 0) == xs.length
    if (targetPath.isEmpty)
      allZeros(currentPath)
    else
      targetPath.get.take(currentPath.length) == currentPath
  }

  /**
   * Returns an <code>Informer</code> that during test execution will forward strings (and other objects) passed to its
   * <code>apply</code> method to the current reporter. If invoked in a constructor, it
   * will register the passed string for forwarding later during test execution. If invoked while this
   * <code>FunSpec</code> is being executed, such as from inside a test function, it will forward the information to
   * the current reporter immediately. If invoked at any other time, it will
   * throw an exception. This method can be called safely by any thread.
   */
  implicit protected def info: Informer = atomicInformer.get

  /**
   * Class that, via an instance referenced from the <code>it</code> field,
   * supports test (and shared test) registration in <code>FunSpec</code>s.
   *
   * <p>
   * This class supports syntax such as the following test registration:
   * </p>
   *
   * <pre class="stExamples">
   * it("should be empty")
   * ^
   * </pre>
   *
   * <p>
   * and the following shared test registration:
   * </p>
   *
   * <pre class="stExamples">
   * it should behave like nonFullStack(stackWithOneItem)
   * ^
   * </pre>
   *
   * <p>
   * For more information and examples, see the <a href="FunSpec.html">main documentation for <code>FunSpec</code></a>.
   * </p>
   */
  protected class ItWord {

    /**
     * Register a test with the given spec text, optional tags, and test function value that takes no arguments.
     * An invocation of this method is called an &#8220;example.&#8221;
     *
     * This method will register the test for later execution via an invocation of one of the <code>execute</code>
     * methods. The name of the test will be a concatenation of the text of all surrounding describers,
     * from outside in, and the passed spec text, with one space placed between each item. (See the documenation
     * for <code>testNames</code> for an example.) The resulting test name must not have been registered previously on
     * this <code>FunSpec</code> instance.
     *
     * @param specText the specification text, which will be combined with the descText of any surrounding describers
     * to form the test name
     * @param testTags the optional list of tags for this test
     * @param testFun the test function
     * @throws DuplicateTestNameException if a test with the same name has been registered previously
     * @throws TestRegistrationClosedException if invoked after <code>run</code> has been invoked on this suite
     * @throws NullPointerException if <code>specText</code> or any passed test tag is <code>null</code>
     */
    def apply(specText: String, testTags: Tag*)(testFun: => Unit) {
      val nextPath = getNextPath()
      if (isInTargetPath(nextPath, targetPath)) {
        // Default value of None indicates successful test
        var resultOfRunningTest: Option[Throwable] = None
        
        try { // TODO: add a test that ensures withFixture is called
          testFun
          // If no exception, leave at None to indicate success
        }
        catch {
          case e: Throwable if !Suite.anErrorThatShouldCauseAnAbort(e) =>
            resultOfRunningTest = Some(e)
        }
        val newTestFun = { () =>
          if (resultOfRunningTest.isDefined)
            throw resultOfRunningTest.get
        }
        registerTest(specText, newTestFun, "itCannotAppearInsideAnotherIt", sourceFileName, "apply", testTags: _*)
      }
    }

    /**
     * Supports the registration of shared tests.
     *
     * <p>
     * This method supports syntax such as the following:
     * </p>
     *
     * <pre class="stExamples">
     * it should behave like nonFullStack(stackWithOneItem)
     *    ^
     * </pre>
     *
     * <p>
     * For examples of shared tests, see the <a href="FunSpec.html#SharedTests">Shared tests section</a>
     * in the main documentation for trait <code>FunSpec</code>.
     * </p>
     */
    def should(behaveWord: BehaveWord) = behaveWord

    /**
     * Supports the registration of shared tests.
     *
     * <p>
     * This method supports syntax such as the following:
     * </p>
     *
     * <pre class="stExamples">
     * it must behave like nonFullStack(stackWithOneItem)
     *    ^
     * </pre>
     *
     * <p>
     * For examples of shared tests, see the <a href="FunSpec.html#SharedTests">Shared tests section</a>
     * in the main documentation for trait <code>FunSpec</code>.
     * </p>
     */
    def must(behaveWord: BehaveWord) = behaveWord
  }

  /**
   * Supports test (and shared test) registration in <code>FunSpec</code>s.
   *
   * <p>
   * This field supports syntax such as the following:
   * </p>
   *
   * <pre class="stExamples">
   * it("should be empty")
   * ^
   * </pre>
   *
   * <pre> class="stExamples"
   * it should behave like nonFullStack(stackWithOneItem)
   * ^
   * </pre>
   *
   * <p>
   * For more information and examples of the use of the <code>it</code> field, see the main documentation for this trait.
   * </p>
   */
  protected val it = new ItWord

  /**
   * Register a test to ignore, which has the given spec text, optional tags, and test function value that takes no arguments.
   * This method will register the test for later ignoring via an invocation of one of the <code>execute</code>
   * methods. This method exists to make it easy to ignore an existing test by changing the call to <code>it</code>
   * to <code>ignore</code> without deleting or commenting out the actual test code. The test will not be executed, but a
   * report will be sent that indicates the test was ignored. The name of the test will be a concatenation of the text of all surrounding describers,
   * from outside in, and the passed spec text, with one space placed between each item. (See the documenation
   * for <code>testNames</code> for an example.) The resulting test name must not have been registered previously on
   * this <code>FunSpec</code> instance.
   *
   * @param specText the specification text, which will be combined with the descText of any surrounding describers
   * to form the test name
   * @param testTags the optional list of tags for this test
   * @param testFun the test function
   * @throws DuplicateTestNameException if a test with the same name has been registered previously
   * @throws TestRegistrationClosedException if invoked after <code>run</code> has been invoked on this suite
   * @throws NullPointerException if <code>specText</code> or any passed test tag is <code>null</code>
   */
  protected def ignore(testText: String, testTags: Tag*)(testFun: => Unit) {
    registerIgnoredTest(testText, testFun _, "ignoreCannotAppearInsideAnIt", sourceFileName, "ignore", testTags: _*)
  }
  
  /**
   * Describe a &#8220;subject&#8221; being specified and tested by the passed function value. The
   * passed function value may contain more describers (defined with <code>describe</code>) and/or tests
   * (defined with <code>it</code>). This trait's implementation of this method will register the
   * description string and immediately invoke the passed function.
   */
  protected def describe(description: String)(fun: => Unit) {
    val nextPath = getNextPath()
    // val nextPathZero = if (nextPath.length > 0) nextPath(0) else -1
    // val nextPathOne = if (nextPath.length > 1) nextPath(1) else -1
    // val nextPathTwo = if (nextPath.length > 2) nextPath(2) else -1
    // val isDef = targetPath.isDefined
    // val isInTarget = if (isDef) isInTargetPath(nextPath, targetPath) else false
    // val theTarget = if (isDef) targetPath.get else List()
    // val targetPathZero = if (theTarget.length > 0) theTarget(0) else -1
    // val targetPathOne = if (theTarget.length > 1) theTarget(1) else -1
    // val targetPathTwo = if (theTarget.length > 2) theTarget(2) else -1
    if (isInTargetPath(nextPath, targetPath)) {
      val oldCurrentPath = currentPath
      currentPath = nextPath
      registerNestedBranch(description, None, fun, "describeCannotAppearInsideAnIt", sourceFileName, "describe")
      currentPath = oldCurrentPath
    }
  }

  /**
   * An immutable <code>Set</code> of test names. If this <code>FunSpec</code> contains no tests, this method returns an
   * empty <code>Set</code>.
   *
   * <p>
   * This trait's implementation of this method will return a set that contains the names of all registered tests. The set's
   * iterator will return those names in the order in which the tests were registered. Each test's name is composed
   * of the concatenation of the text of each surrounding describer, in order from outside in, and the text of the
   * example itself, with all components separated by a space. For example, consider this <code>FunSpec</code>:
   * </p>
   *
   * <pre class="stHighlight">
   * import org.scalatest.FunSpec
   *
   * class StackSpec extends FunSpec {
   *   describe("A Stack") {
   *     describe("(when not empty)") {
   *       it("must allow me to pop") {}
   *     }
   *     describe("(when not full)") {
   *       it("must allow me to push") {}
   *     }
   *   }
   * }
   * </pre>
   *
   * <p>
   * Invoking <code>testNames</code> on this <code>FunSpec</code> will yield a set that contains the following
   * two test name strings:
   * </p>
   *
   * <pre class="stExamples">
   * "A Stack (when not empty) must allow me to pop"
   * "A Stack (when not full) must allow me to push"
   * </pre>
   */
  final override def testNames: Set[String] = {
    ensureTestResultsRegistered()
    super.testNames
  }

  final override def expectedTestCount(filter: Filter): Int = {
    ensureTestResultsRegistered()
    super.expectedTestCount(filter)
  }
  /*
   * Will need to implement things like testNames, tags, and expectedTestCount to do the isolation many instance thing, because
   * otherwise, it will not work. Can't just instantiate one of these.
   */
  /**
   * Run a test. This trait's implementation runs the test registered with the name specified by
   * <code>testName</code>. Each test's name is a concatenation of the text of all describers surrounding a test,
   * from outside in, and the test's spec text, with one space placed between each item. (See the documenation
   * for <code>testNames</code> for an example.)
   *
   * @param testName the name of one test to execute.
   * @param reporter the <code>Reporter</code> to which results will be reported
   * @param stopper the <code>Stopper</code> that will be consulted to determine whether to stop execution early.
   * @param configMap a <code>Map</code> of properties that can be used by this <code>FunSpec</code>'s executing tests.
   * @throws NullPointerException if any of <code>testName</code>, <code>reporter</code>, <code>stopper</code>, or <code>configMap</code>
   *     is <code>null</code>.
   */
    final protected override def runTest(testName: String, reporter: Reporter, stopper: Stopper, configMap: Map[String, Any], tracker: Tracker) {
      throw new UnsupportedOperationException
    }

/*  protected override def runTest(testName: String, reporter: Reporter, stopper: Stopper, configMap: Map[String, Any], tracker: Tracker) {

    def dontInvokeWithFixture(theTest: TestLeaf) {
      theTest.testFun()
      /*
      val theConfigMap = configMap
      withFixture(
        new NoArgTest {
          def name = testName
          def apply() { theTest.testFun() }
          def configMap = theConfigMap
        }
      )*/
    }

    runTestImpl(thisSuite, testName, reporter, stopper, configMap, tracker, true, dontInvokeWithFixture)
  }
*/
  /**
   * A <code>Map</code> whose keys are <code>String</code> tag names to which tests in this <code>FunSpec</code> belong, and values
   * the <code>Set</code> of test names that belong to each tag. If this <code>FunSpec</code> contains no tags, this method returns an empty <code>Map</code>.
   *
   * <p>
   * This trait's implementation returns tags that were passed as strings contained in <code>Tag</code> objects passed to 
   * methods <code>test</code> and <code>ignore</code>. 
   * </p>
   */
  override def tags: Map[String, Set[String]] = atomic.get.tagsMap
/*
 * Use Suite.run implementation. Allow overriding nestedSuites and runNestedSuites. implement runTests to do the funky thing.
 */
  final override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
      configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {

    super.run(testName, reporter, stopper, filter, configMap, distributor, tracker)
  }
/* OLD
  override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
      configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {

    runImpl(thisSuite, testName, reporter, stopper, filter, configMap, distributor, tracker, super.run)
  }
*/
  /**
   * Supports shared test registration in <code>FunSpec</code>s.
   *
   * <p>
   * This field supports syntax such as the following:
   * </p>
   *
   * <pre class="stExamples">
   * it should behave like nonFullStack(stackWithOneItem)
   *           ^
   * </pre>
   *
   * <p>
   * For more information and examples of the use of <cod>behave</code>, see the <a href="#SharedTests">Shared tests section</a>
   * in the main documentation for this trait.
   * </p>
   */
  protected val behave = new BehaveWord

  // This guy must check the path. If null, that's the first instance, so go zero zero zero until hit first test, then execute it (if testName is
  // undefined. (If testName is defined, keep going until you hit that chosen test name.) Anyway, after executing test one in the initial instance,
  // need to write the path and return, then. Oh wait, that's not runTests, that just the constructor. How can that work?
  /*
   * I think the first instance, when the path is null, it will need to run the first test. Then the first time a method is called, be it
   * expectedTestCount, testNames, tags, etc., I'll fill in the remaining ones and freeze dry them. Next tiem those things are called, it
   * will use the cached stuff. If run is called again it just returns the old results, because can't rerun the zeros test until I get
   * a new instance.
   */
  final protected override def runTests(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
                             configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
    ensureTestResultsRegistered()
    super.runTests(testName, reporter, stopper, filter, configMap, distributor, tracker)
  }
  
  /* This is the same
  protected override def runTests(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
                             configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
    testName match {
      case Some(tn) => super.runTests(testName, reporter, stopper, filter, configMap, None, tracker)
      case None =>
        for (tn <- testNames) {
          val oneInstance = prepareNewInstanceFor(tn)
          oneInstance.run(Some(tn), reporter, stopper, filter, configMap, None, tracker)
        }
    }
  }
*//*
  final override def prepareNewInstanceFor(testName: String): Suite = {
    FunSpec.setPath(engine.testPath(testName))
    newInstance
  }
  */
}

private[path] object FunSpec {
   private[this] val path = new ThreadLocal[List[Int]]
   // path "None" must be null in this case, because that's the default in any thread

   private def setPath(ints: List[Int]) {
     if (path.get != null)
       throw new IllegalStateException("Path was already defined when setPath was called, as: " + path.get)
     path.set(ints)
   }

   private def getPath(): Option[List[Int]] = {
     val p = path.get
     path.set(null)
     if (p == null) None else Some(p) // Use Option(p) when drop 2.8 support
   }
}