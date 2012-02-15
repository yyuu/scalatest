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

import scala.collection.mutable

trait FunSpec extends org.scalatest.Suite with OneInstancePerTest { thisSuite =>
  
  private final val targetPath: Option[List[Int]] = FunSpec.getPath
  private final val isAnInitialInstance = targetPath.isEmpty
  
  private final val engine = FunSpec.getEngine()
  import engine._

  private final val registeredPathSet = FunSpec.getRegisteredPathSet()
  
  // Used in each instance to track the paths of things encountered, so can figure out
  // the next path. Each instance must use their own copies of currentPath and usedPathSet.
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
  
  // Once the target leaf has been reached for an instance, targetLeafHasBeenReached
  // will be set to true. And because of that, the path of the next describe or it encountered will
  // be placed into nextTargetPath. If no other describe or it clause comes along, then nextTargetPath
  // will stay at None, and the while loop will stop.
  @volatile private var targetLeafHasBeenReached = false
  @volatile private var nextTargetPath: Option[List[Int]] = None
  
  @volatile private var testResultsRegistered = false
  private def ensureTestResultsRegistered() {
    synchronized {
      // Only register tests if this is an initial instance (and only if they haven't
      // already been registered.
      if (isAnInitialInstance  && !testResultsRegistered) {
        testResultsRegistered = true
        var currentInstance: FunSpec = this
        while (currentInstance.nextTargetPath.isDefined) {
          FunSpec.setPath(currentInstance.nextTargetPath.get)
          FunSpec.setEngine(engine)
          FunSpec.setRegisteredPathSet(registeredPathSet)
          currentInstance = newInstance  
        }
      }
    }
  }
  
  override def newInstance = this.getClass.newInstance.asInstanceOf[FunSpec]

  /*
   * First time this is instantiated, targetPath will be None. In that case, execute the
   * first test, and each describe clause on the way to the first test (the all zeros path).
   */
  private def isInTargetPath(currentPath: List[Int], targetPath: Option[List[Int]]): Boolean = {
    def allZeros(xs: List[Int]) = xs.count(_ == 0) == xs.length
    if (targetPath.isEmpty)
      allZeros(currentPath)
    else {
      if (currentPath.length < targetPath.get.length)
        targetPath.get.take(currentPath.length) == currentPath // TODO: deal with sibling describes
      else if (currentPath.length > targetPath.get.length)
        (currentPath.take(targetPath.get.length) == targetPath.get) && (!currentPath.drop(targetPath.get.length).exists(_ != 0)) // TODO: deal with sibling describes
      else
        targetPath.get == currentPath
    }
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
        registerTest(specText, newTestFun, "itCannotAppearInsideAnotherIt", "FunSpec.scala", "apply", testTags: _*)
        targetLeafHasBeenReached = true
      }
      else if (targetLeafHasBeenReached && nextTargetPath.isEmpty) {
        nextTargetPath = Some(nextPath)
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
    registerIgnoredTest(testText, testFun _, "ignoreCannotAppearInsideAnIt", "FunSpec.scala", "ignore", testTags: _*)
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
    if (targetLeafHasBeenReached && nextTargetPath.isEmpty) {
      nextTargetPath = Some(nextPath)
    }
    else if (isInTargetPath(nextPath, targetPath)) { // TODO: check if !targetLeafHasBeenReached like it() does. Probably this is empty describe behavior
      val oldCurrentPath = currentPath
      currentPath = nextPath
      if (!registeredPathSet.contains(nextPath)) {
        registerNestedBranch(description, None, fun, "describeCannotAppearInsideAnIt", "FunSpec.scala", "describe")
        registeredPathSet += nextPath
      }
      else {
        navigateToNestedBranch(nextPath, fun, "describeCannotAppearInsideAnIt", "FunSpec.scala", "describe")
      }
      currentPath = oldCurrentPath
    }
  }

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

  // This one is no longer used. Disentanglement
  final override def withFixture(test: NoArgTest) {
    throw new UnsupportedOperationException
  }
  
  final override def testNames: Set[String] = {
    ensureTestResultsRegistered()
    // I'm returning a ListSet here so that they tests will be run in registration order
    ListSet(atomic.get.testNamesList.toArray: _*)
  }

  final override def expectedTestCount(filter: Filter): Int = {
    ensureTestResultsRegistered()
    super.expectedTestCount(filter)
  }

  final protected override def runTest(testName: String, reporter: Reporter, stopper: Stopper, configMap: Map[String, Any], tracker: Tracker) {

    ensureTestResultsRegistered()
    
    def dontInvokeWithFixture(theTest: TestLeaf) {
      theTest.testFun()
    }

    runTestImpl(thisSuite, testName, reporter, stopper, configMap, tracker, true, dontInvokeWithFixture)
  }

  final override def tags: Map[String, Set[String]] = {
    ensureTestResultsRegistered()
    atomic.get.tagsMap
  }
  
  /*
   * Use Suite.run implementation. Allow overriding nestedSuites and runNestedSuites.
   */
  final override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
      configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {

   ensureTestResultsRegistered()
   super.run(testName, reporter, stopper, filter, configMap, distributor, tracker)
  }

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
    runTestsImpl(thisSuite, testName, reporter, stopper, filter, configMap, distributor, tracker, info, true, runTest)
  }

  final protected override def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
  }
  
  // Don't even allow nested suites in here, for starters at least, because they would run *after* the tests run, which 
  // would be inconsistent with the rest of ScalaTest. If you want to do nested suites, can wrap one of these in another
  // one that does nested suites.
  final override def nestedSuites: List[Suite] = Nil
}

private[path] object FunSpec {
  
   private[this] val path = new ThreadLocal[List[Int]]
   // path "None" must be null in this case, because that's the default in any thread
   private[this] val engine = new ThreadLocal[Engine]

   private[this] val registeredPathSet = new ThreadLocal[mutable.Set[List[Int]]]

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

   private def setEngine(en: Engine) {
     if (engine.get != null)
       throw new IllegalStateException("Engine was already defined when setEngine was called")
     engine.set(en)
   }

   private def getEngine(): Engine = {
     val en = engine.get
     engine.set(null)
     if (en == null) (new Engine("concurrentSpecMod", "Spec")) else en
   }

   private def setRegisteredPathSet(rps: mutable.Set[List[Int]]) {
     if (registeredPathSet.get != null)
       throw new IllegalStateException("Registered path set was already defined when setRegisteredPathSet was called")
     registeredPathSet.set(rps)
   }
   

   private def getRegisteredPathSet(): mutable.Set[List[Int]] = {
     val rps = registeredPathSet.get
     registeredPathSet.set(null)
     if (rps == null) (mutable.Set.empty[List[Int]]) else rps
   }
}