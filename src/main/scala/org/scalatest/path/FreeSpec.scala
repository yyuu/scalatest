/*package org.scalatest.path

trait FreeSpec {

}*/

package org.scalatest.path

import org.scalatest.Suite
import org.scalatest.OneInstancePerTest
import org.scalatest.Reporter
import org.scalatest.Stopper
import org.scalatest.Filter
import org.scalatest.Tracker
import org.scalatest.Distributor
import org.scalatest.PathEngine
import org.scalatest.Informer
import org.scalatest.Tag
import org.scalatest.verb.BehaveWord
import scala.collection.immutable.ListSet
import org.scalatest.PathEngine.isInTargetPath
import org.scalatest.PendingNothing

/**
 * A sister trait to <code>org.scalatest.FreeSpec</code> that isolates each test by running each test in its own
 * instance of the test class, and for each test, only executing the <em>path</em> leading to that test.
 *
 * <p>
 * Trait <code>path.FreeSpec</code> behaves similarly to trait <code>org.scalatest.FreeSpec</code>, except that tests
 * are isolated based on their path. The purpose of <code>path.FreeSpec</code> is to facilitate writing
 * specification-style tests for mutable objects in a clear, boilerpate-free way. To test mutable objects, you need to
 * mutate them. Using a path trait, you can make a statement in text, then make that statement in code (including
 * mutating state), and nest and combine these test/code statement pairs in any way you wish. Each test will only see
 * the side effects of code that is in blocks that enclose the test. Here's an example:
 * </p>
 *
 * <pre class="stHighlight">
 * import org.scalatest.path
 * import org.scalatest.matchers.ShouldMatchers
 * import scala.collection.mutable.ListBuffer
 *
 * class ExampleSpec extends path.FreeSpec with ShouldMatchers {
 *
 *   "A ListBuffer" - {
 *
 *     val buf = ListBuffer.empty[Int]
 *
 *     "should be empty when created" in {
 *
 *       // This test sees:
 *       //   val buf = ListBuffer.empty[Int]
 *       // So buf is: ListBuffer()
 *
 *       buf should be ('empty)
 *     }
 *
 *     "when 1 is appended" - {
 *
 *       buf += 1
 *
 *       "should contain 1" in {
 *
 *         // This test sees:
 *         //   val buf = ListBuffer.empty[Int]
 *         //   buf += 1
 *         // So buf is: ListBuffer(1)
 *
 *         buf.remove(0) should equal (1)
 *         buf should be ('empty)
 *       }
 *
 *       "when 2 is appended" - {
 *
 *         buf += 2
 *
 *         "should contain 1 and 2" in {
 *
 *           // This test sees:
 *           //   val buf = ListBuffer.empty[Int]
 *           //   buf += 1
 *           //   buf += 2
 *           // So buf is: ListBuffer(1, 2)
 *
 *           buf.remove(0) should equal (1)
 *           buf.remove(0) should equal (2)
 *           buf should be ('empty)
 *         }
 *
 *         "when 2 is removed" - {
 *
 *           buf -= 2
 *
 *           "should contain only 1 again" in {
 *
 *             // This test sees:
 *             //   val buf = ListBuffer.empty[Int]
 *             //   buf += 1
 *             //   buf += 2
 *             //   buf -= 2
 *             // So buf is: ListBuffer(1)
 *
 *             buf.remove(0) should equal (1)
 *             buf should be ('empty)
 *           }
 *         }
 *
 *         "when 3 is appended" - {
 *
 *           buf += 3
 *
 *           "should contain 1, 2, and 3" in {
 *
 *             // This test sees:
 *             //   val buf = ListBuffer.empty[Int]
 *             //   buf += 1
 *             //   buf += 2
 *             //   buf += 3
 *             // So buf is: ListBuffer(1, 2, 3)
 *
 *             buf.remove(0) should equal (1)
 *             buf.remove(0) should equal (2)
 *             buf.remove(0) should equal (3)
 *             buf should be ('empty)
 *           }
 *         }
 *       }
 *
 *       "when 88 is appended" - {
 *
 *         buf += 88
 *
 *         "should contain 1 and 88" in {
 *
 *           // This test sees:
 *           //   val buf = ListBuffer.empty[Int]
 *           //   buf += 1
 *           //   buf += 88
 *           // So buf is: ListBuffer(1, 88)
 *
 *           buf.remove(0) should equal (1)
 *           buf.remove(0) should equal (88)
 *           buf should be ('empty)
 *         }
 *       }
 *     }
 *
 *     "should have size 0 when created" in {
 *
 *       // This test sees:
 *       //   val buf = ListBuffer.empty[Int]
 *       // So buf is: ListBuffer()
 *
 *       buf should have size 0
 *     }
 *   }
 * }
 * </pre>
 *
 * <p>
 * Note that the above class is organized by writing a bit of specification text that opens a new block followed
 * by, at the top of the new block, some code that performs what is described in the text. This is repeated as
 * the mutable object (a <code>ListBuffer</code>, is prepared for the enclosed tests. For example:
 * <p>
 *
 * <pre class="stHighlight">
 * "A ListBuffer" - {
 *   val buf = ListBuffer.empty[Int]
 * </pre>
 *
 * <p>
 * Or:
 * </p>
 *
 * <pre class="stHighlight">
 * "when 2 is appended" - {
 *   buf += 2
 * </pre>
 *
 * <p>
 * Note also that although each test mutates the <code>ListBuffer</code>, none of the other tests observe those
 * side effects:
 * <p>
 *
 * <pre class="stHighlight">
 * "should contain 1" in {
 *
 *   buf.remove(0) should equal (1)
 *   // ...
 * }
 *
 * "when 2 is appended" - {
 *
 *   buf += 2
 *
 *   "should contain 1 and 2" in {
 *
 *     // This test does not see the buf.remove(0) from the previous test,
 *     // so the first element in the ListBuffer is again 1
 *     buf.remove(0) should equal (1)
 *     buf.remove(0) should equal (2)
 * </pre>
 *
 * <p>
 * This kind of isolation of tests from each other is a consequence of running each test in its own instance of the test
 * class, and can also be achieved by simply mixing <code>OneInstancePerTest</code> into a regular
 * <code>org.scalatest.FreeSpec</code>. However, <code>path.FreeSpec</code> takes isolation one step further: a test
 * in a <code>path.FreeSpec</code> does not observe side effects performed outside tests in earlier blocks that do not
 * enclose it. Here's an example:
 * </p>
 *
 * <pre class="stHighlight">
 * "when 2 is removed" - {
 *
 *   buf -= 2
 *
 *   // ...
 * }
 *
 * "when 3 is appended" - {
 *
 *   buf += 3
 *
 *   "should contain 1, 2, and 3" in {
 *
 *     // This test does not see the buf -= 2 from the earlier "when 2 is removed" block,
 *     // because that block does not enclose this test, so the second element in the
 *     // ListBuffer is still 2
 *     buf.remove(0) should equal (1)
 *     buf.remove(0) should equal (2)
 *     buf.remove(0) should equal (3)
 * </pre>
 *
 * <p>
 * Running the above <code>ExampleSpec</code> in the Scala interpeter would give you:
 * </p>
 *
 * <pre class="stREPL">
 * scala> import org.scalatest._
 * import org.scalatest._
 *
 * scala> run(new ExampleSpec)
 * <span class="stGreen">ExampleSpec:
 * A ListBuffer
 * - should be empty when created
 * &nbsp; when 1 is appended
 * &nbsp; - should contain 1
 * &nbsp;   when 2 is appended
 * &nbsp;   - should contain 1 and 2
 * &nbsp;     when 2 is removed
 * &nbsp;     - should contain only 1 again
 * &nbsp;     when 3 is appended
 * &nbsp;     - should contain 1, 2, and 3
 * &nbsp;   when 88 is appended
 * &nbsp;   - should contain 1 and 88
 * - should have size 0 when created</span>
 * </pre>
 *
 * <p>
 * <em>Note: trait <code>path.FreeSpec</code>'s approach to isolation was inspired in part by the
 * <a href="https://github.com/orfjackal/specsy">specsy</a> framework, written by Esko Luontola.</em>
 * </p>
 *
 * <a name="sharedFixtures"></a><h2>Shared fixtures</h2>
 *
 * <p>
 * A test <em>fixture</em> is objects or other artifacts (such as files, sockets, database
 * connections, <em>etc.</em>) used by tests to do their work.
 * If a fixture is used by only one test, then the definitions of the fixture objects can
 * be local to the method. If multiple tests need to share an immutable fixture, you can simply
 * assign them to instance variables. If multiple tests need to share mutable fixture objects or <code>var</code>s,
 * there's one and only one way to do it in a <code>path.FreeSpec</code>: place the mutable objects lexically before
 * the test. Any mutations needed by the test must be placed lexically before and/or after the test.
 * As used here, "Lexically before" means that the code needs to be executed during construction of that test's
 * instance of the test class to <em>reach</em> the test (or put another way, the
 * code is along the "path to the test.") "Lexically after" means that the code needs to be executed to exit the
 * constructor after the test has been executed.
 * </p>
 *
 * <p>
 * The reason lexical placement is the one and only one way to share fixtures in a <code>path.FreeSpec</code> is because
 * all of its lifecycle methods are overridden and declared <code>final</code>. Thus you can't override
 * <code>withFixture</code>, because it is <code>final</code>, or mix in <code>BeforeAndAfter</code> or
 * </code>BeforeAndAfterEach</code>, because both override <code>runTest</code>, which is <code>final</code> in
 * a <code>path.FreeSpec</code>. In short:
 * </p>
 *
 * <p>
 * <table style="border-collapse: collapse; border: 1px solid black; width: 70%; margin: auto">
 * <tr>
 * <th style="background-color: #CCCCCC; border-width: 1px; padding: 15px; text-align: left; border: 1px solid black; font-size: 125%; font-weight: bold">
 * In a <code>path.FreeSpec</code>, if you need some code to execute before a test, place that code lexically before
 * the test. If you need some code to execute after a test, place that code lexically after the test.
 * </th>
 * </tr>
 * </table>
 * </p>
 *
 * <p>
 * The reason the life cycle methods are final, by the way, is to prevent users from attempting to combine
 * a <code>path.FreeSpec</code>'s approach to isolation with other ways ScalaTest provides to share fixtures or
 * execute tests, because doing so could make the resulting test code hard to reason about. A
 * <code>path.FreeSpec</code>'s execution model is a bit magical, but because it executes in one and only one
 * way, users should be able to reason about the code.
 * To help you visualize how a <code>path.FreeSpec</code> is executed, consider the following variant of
 * <code>ExampleSpec</code> that includes print statements:
 * </p>
 *
 * <pre class="stHighlight">
 * import org.scalatest.path
 * import org.scalatest.matchers.ShouldMatchers
 * import scala.collection.mutable.ListBuffer
 *
 * class ExampleSpec extends path.FreeSpec with ShouldMatchers {
 *
 *   println("Start of: ExampleSpec")
 *   "A ListBuffer" - {
 *
 *     println("Start of: A ListBuffer")
 *     val buf = ListBuffer.empty[Int]
 *
 *     "should be empty when created" in {
 *
 *       println("In test: should be empty when created; buf is: " + buf)
 *       buf should be ('empty)
 *     }
 *
 *     "when 1 is appended" - {
 *
 *       println("Start of: when 1 is appended")
 *       buf += 1
 *
 *       "should contain 1" in {
 *
 *         println("In test: should contain 1; buf is: " + buf)
 *         buf.remove(0) should equal (1)
 *         buf should be ('empty)
 *       }
 *
 *       "when 2 is appended" - {
 *
 *         println("Start of: when 2 is appended")
 *         buf += 2
 *
 *         "should contain 1 and 2" in {
 *
 *           println("In test: should contain 1 and 2; buf is: " + buf)
 *           buf.remove(0) should equal (1)
 *           buf.remove(0) should equal (2)
 *           buf should be ('empty)
 *         }
 *
 *         "when 2 is removed" - {
 *
 *           println("Start of: when 2 is removed")
 *           buf -= 2
 *
 *           "should contain only 1 again" in {
 *
 *             println("In test: should contain only 1 again; buf is: " + buf)
 *             buf.remove(0) should equal (1)
 *             buf should be ('empty)
 *           }
 *
 *           println("End of: when 2 is removed")
 *         }
 *
 *         "when 3 is appended" - {
 *
 *           println("Start of: when 3 is appended")
 *           buf += 3
 *
 *           "should contain 1, 2, and 3" in {
 *
 *             println("In test: should contain 1, 2, and 3; buf is: " + buf)
 *             buf.remove(0) should equal (1)
 *             buf.remove(0) should equal (2)
 *             buf.remove(0) should equal (3)
 *             buf should be ('empty)
 *           }
 *           println("End of: when 3 is appended")
 *         }
 *
 *         println("End of: when 2 is appended")
 *       }
 *
 *       "when 88 is appended" - {
 *
 *         println("Start of: when 88 is appended")
 *         buf += 88
 *
 *         "should contain 1 and 88" in {
 *
 *           println("In test: should contain 1 and 88; buf is: " + buf)
 *           buf.remove(0) should equal (1)
 *           buf.remove(0) should equal (88)
 *           buf should be ('empty)
 *         }
 *
 *         println("End of: when 88 is appended")
 *       }
 *
 *       println("End of: when 1 is appended")
 *     }
 *
 *     "should have size 0 when created" in {
 *
 *       println("In test: should have size 0 when created; buf is: " + buf)
 *       buf should have size 0
 *     }
 *
 *     println("End of: A ListBuffer")
 *   }
 *   println("End of: ExampleSpec")
 *   println()
 * }
 * </pre>
 *
 * <p>
 * Running the above version of <code>ExampleSpec</code> in the Scala interpreter will give you output similar to:
 * </p>
 *
 * <pre class="stREPL">
 * scala> import org.scalatest._
 * import org.scalatest._
 *
 * scala> run(new ExampleSpec)
 * <span class="stGreen">ExampleSpec:</span>
 * Start of: ExampleSpec
 * Start of: A ListBuffer
 * In test: should be empty when created; buf is: ListBuffer()
 * End of: A ListBuffer
 * End of: ExampleSpec
 *
 * Start of: ExampleSpec
 * Start of: A ListBuffer
 * Start of: when 1 is appended
 * In test: should contain 1; buf is: ListBuffer(1)
 * ExampleSpec:
 * End of: when 1 is appended
 * End of: A ListBuffer
 * End of: ExampleSpec
 *
 * Start of: ExampleSpec
 * Start of: A ListBuffer
 * Start of: when 1 is appended
 * Start of: when 2 is appended
 * In test: should contain 1 and 2; buf is: ListBuffer(1, 2)
 * End of: when 2 is appended
 * End of: when 1 is appended
 * End of: A ListBuffer
 * End of: ExampleSpec
 *
 * Start of: ExampleSpec
 * Start of: A ListBuffer
 * Start of: when 1 is appended
 * Start of: when 2 is appended
 * Start of: when 2 is removed
 * In test: should contain only 1 again; buf is: ListBuffer(1)
 * End of: when 2 is removed
 * End of: when 2 is appended
 * End of: when 1 is appended
 * End of: A ListBuffer
 * End of: ExampleSpec
 *
 * Start of: ExampleSpec
 * Start of: A ListBuffer
 * Start of: when 1 is appended
 * Start of: when 2 is appended
 * Start of: when 3 is appended
 * In test: should contain 1, 2, and 3; buf is: ListBuffer(1, 2, 3)
 * End of: when 3 is appended
 * End of: when 2 is appended
 * End of: when 1 is appended
 * End of: A ListBuffer
 * End of: ExampleSpec
 *
 * Start of: ExampleSpec
 * Start of: A ListBuffer
 * Start of: when 1 is appended
 * Start of: when 88 is appended
 * In test: should contain 1 and 88; buf is: ListBuffer(1, 88)
 * End of: when 88 is appended
 * End of: when 1 is appended
 * End of: A ListBuffer
 * End of: ExampleSpec
 *
 * Start of: ExampleSpec
 * Start of: A ListBuffer
 * In test: should have size 0 when created; buf is: ListBuffer()
 * End of: A ListBuffer
 * End of: ExampleSpec
 *
 * <span class="stGreen">A ListBuffer
 * - should be empty when created
 *   when 1 is appended
 * &nbsp; - should contain 1
 * &nbsp;   when 2 is appended
 * &nbsp;   - should contain 1 and 2
 * &nbsp;     when 2 is removed
 * &nbsp;     - should contain only 1 again
 * &nbsp;     when 3 is appended
 * &nbsp;     - should contain 1, 2, and 3
 * &nbsp;   when 88 is appended
 * &nbsp;   - should contain 1 and 88
 * - should have size 0 when created</span>
 * </pre>
 *
 * <p>
 * Note that each test is executed in order of appearance in the <code>path.FreeSpec</code>, and that only
 * those <code>println</code> statements residing in blocks that enclose the test being run are executed. Any
 * <code>println</code> statements in blocks that do not form the "path" to a test are not executed in the
 * instance of the class that executes that test.
 * </p>
 *
 * <h2>How it executes</h2>
 *
 * <p>
 * To provide its special brand of test isolation, <code>path.FreeSpec</code> executes quite differently from its
 * sister trait in <code>org.scalatest</code>. An <code>org.scalatest.FreeSpec</code>
 * registers tests during construction and executes them when <code>run</code> is invoked. An
 * <code>org.scalatest.path.FreeSpec</code>, by contrast, runs each test in its own instance <em>while that
 * instance is being constructed</em>. During construction, it registers not the tests to run, but the results of
 * running those tests. When <code>run</code> is invoked on a <code>path.FreeSpec</code>, it reports the registered
 * results and does not run the tests again. If <code>run</code> is invoked a second or third time, in fact,
 * a <code>path.FreeSpec</code> will each time report the same results registered during construction. If you want
 * to run the tests of a <code>path.FreeSpec</code> anew, you'll need to create a new instance and invoke
 * <code>run</code> on that.
 * <p>
 *
 * <p>
 * A <code>path.FreeSpec</code> will create one instance for each "leaf" node it contains. The main kind of leaf node is
 * a test, such as:
 * </p>
 *
 * <pre class="stHighlight">
 * // One instance will be created for each test
 * "should be empty when created" in {
 *   buf should be ('empty)
 * }
 * </pre>
 *
 * <p>
 * However, an empty scope (a scope that contains no tests or nested scopes) is also a leaf node:
 * </p>
 *
 * <pre class="stHighlight">
 *  // One instance will be created for each empty scope
 * "when 99 is added" - {
 *   // A scope is "empty" and therefore a leaf node if it has no
 *   // tests or nested scopes, though it may have other code (which
 *   // will be executed in the instance created for that leaf node)
 *   buf += 99
 * }
 * </pre>
 *
 * <p>
 * The tests will be executed sequentially, in the order of appearance. The first test (or empty scope,
 * if that is first) will be executed when a class that mixes in <code>path.FreeSpec</code> is
 * instantiated. Only the first test will be executed during this initial instance, and of course, only
 * the path to that test. Then, the first time that instance is used (by invoking one of <code>run</code>,
 * <code>expectedTestsCount</code>, <code>runTest</code>, <code>tags</code>, or <code>testNames</code>), it will,
 * before doing anything else, ensure that any remaining tests are executed, each in its own instance.
 * </p>
 *
 * <p>
 * To ensure that the correct path is taken in each instance, and to register its test results, the initial
 * <code>path.FreeSpec</code> instance must communicate with the other instances it creates for running any subsequent
 * leaf nodes. It does so by setting a thread-local variable prior to creating each instance (a technique
 * suggested by Esko Luontola). Each instance
 * of <code>path.FreeSpec</code> checks the thread-local variable. If the thread-local is not set, it knows it
 * is an initial instance and therefore executes every block it encounters until it discovers, and executes the
 * first test (or empty scope, if that's the first leaf node). It then discovers, but does not execute the next
 * leaf node, or discovers there are no other leaf nodes remaining to execute. It communicates the path to the next
 * leaf node, if any, and the result of running the test it did execute, if any, back to the initial instance. The
 * initial instance repeats this process until all leaf nodes have been executed.
 * </p>
 *
 * <a name="ignoredTests" />
 * <h2>Ignored tests</h2>
 *
 * <p>
 * You mark a test as ignored in an <code>org.scalatest.path.FreeSpec</code> in the same manner as in
 * an <code>org.scalatest.FreeSpec</code>. Please see the <a href="../FreeSpec.html#ignoredTests">Ignored tests</a> section
 * in its documentation for more information.
 * </p>
 *
 * <a name="informers" />
 * <h2>Informers</h2>
 *
 * <p>
 * You output information using <code>Informer</code>s in an <code>org.scalatest.path.FreeSpec</code> in the same manner
 * as in an <code>org.scalatest.FreeSpec</code>. Please see the <a href="../FreeSpec.html#informers">Informers</a>
 * section in its documentation for more information.
 * </p>
 *
 * <a name="pendingTests" />
 * <h2>Pending tests</h2>
 *
 * <p>
 * You mark a test as pending in an <code>org.scalatest.path.FreeSpec</code> in the same manner as in
 * an <code>org.scalatest.FreeSpec</code>. Please see the <a href="../FreeSpec.html#pendingTests">Pending tests</a>
 * section in its documentation for more information.
 * </p>
 *
 * <a name="taggingTests" />
 * <h2>Tagging tests</h2>
 *
 * <p>
 * You can place tests into groups by tagging them in an <code>org.scalatest.path.FreeSpec</code> in the same manner
 * as in an <code>org.scalatest.FreeSpec</code>. Please see the <a href="../FreeSpec.html#taggingTests">Tagging tests</a>
 * section in its documentation for more information.
 * </p>
 *
 * <a name="SharedTests"></a><h2>Shared tests</h2>
 * <p>
 * You can factor out shared tests in an <code>org.scalatest.path.FreeSpec</code> in the same manner as in
 * an <code>org.scalatest.FreeSpec</code>. Please see the <a href="../FreeSpec.html#SharedTests">Shared tests</a>
 * section in its documentation for more information.
 * </p>
 *
 * @author Bill Venners
 * @author Chua Chee Seng
 */
trait FreeSpec extends org.scalatest.Suite with OneInstancePerTest { thisSuite =>
  
  private final val engine = PathEngine.getEngine()
  import engine._

  override def newInstance = this.getClass.newInstance.asInstanceOf[FreeSpec]

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
   * Register a test with the given spec text, optional tags, and test function value that takes no arguments.
   * An invocation of this method is called an &#8220;example.&#8221;
   *
   * This method will register the test for later execution via an invocation of one of the <code>execute</code>
   * methods. The name of the test will be a concatenation of the text of all surrounding describers,
   * from outside in, and the passed spec text, with one space placed between each item. (See the documenation
   * for <code>testNames</code> for an example.) The resulting test name must not have been registered previously on
   * this <code>FreeSpec</code> instance.
   *
   * @param specText the specification text, which will be combined with the descText of any surrounding describers
   * to form the test name
   * @param testTags the optional list of tags for this test
   * @param testFun the test function
   * @throws DuplicateTestNameException if a test with the same name has been registered previously
   * @throws TestRegistrationClosedException if invoked after <code>run</code> has been invoked on this suite
   * @throws NullPointerException if <code>specText</code> or any passed test tag is <code>null</code>
   */
  private def registerTestToRun(specText: String, testTags: List[Tag], testFun: () => Unit) {
    // TODO: This is what was being used before but it is wrong
    handleTest(thisSuite, specText, testFun, "itCannotAppearInsideAnotherIt", "FunSpec.scala", "apply", testTags: _*)
    // registerTest(specText, testFun, "itCannotAppearInsideAnotherIt", "FreeSpec.scala", "it", None, testTags: _*)
  }

  /**
   * Register a test to ignore, which has the given spec text, optional tags, and test function value that takes no arguments.
   * This method will register the test for later ignoring via an invocation of one of the <code>execute</code>
   * methods. This method exists to make it easy to ignore an existing test by changing the call to <code>it</code>
   * to <code>ignore</code> without deleting or commenting out the actual test code. The test will not be executed, but a
   * report will be sent that indicates the test was ignored. The name of the test will be a concatenation of the text of all surrounding describers,
   * from outside in, and the passed spec text, with one space placed between each item. (See the documenation
   * for <code>testNames</code> for an example.) The resulting test name must not have been registered previously on
   * this <code>FreeSpec</code> instance.
   *
   * @param specText the specification text, which will be combined with the descText of any surrounding describers
   * to form the test name
   * @param testTags the optional list of tags for this test
   * @param testFun the test function
   * @throws DuplicateTestNameException if a test with the same name has been registered previously
   * @throws TestRegistrationClosedException if invoked after <code>run</code> has been invoked on this suite
   * @throws NullPointerException if <code>specText</code> or any passed test tag is <code>null</code>
   */
  private def registerTestToIgnore(specText: String, testTags: List[Tag], testFun: () => Unit) {

    // TODO: This is how these were, but it needs attention. Mentions "it".
    handleIgnoredTest(specText, testFun, "ignoreCannotAppearInsideAnIt", "FreeSpec.scala", "ignore", testTags: _*)
  }

  /**
   * Class that supports the registration of tagged tests.
   *
   * <p>
   * Instances of this class are returned by the <code>taggedAs</code> method of 
   * class <code>FreeSpecStringWrapper</code>.
   * </p>
   *
   * @author Bill Venners
   */
  protected final class ResultOfTaggedAsInvocationOnString(specText: String, tags: List[Tag]) {

    /**
     * Supports tagged test registration.
     *
     * <p>
     * For example, this method supports syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * "complain on peek" taggedAs(SlowTest) in { ... }
     *                                       ^
     * </pre>
     *
     * <p>
     * For more information and examples of this method's use, see the <a href="FreeSpec.html">main documentation</a> for trait <code>FreeSpec</code>.
     * </p>
     */
    def in(testFun: => Unit) {
      registerTestToRun(specText, tags, testFun _)
    }

    /**
     * Supports registration of tagged, pending tests.
     *
     * <p>
     * For example, this method supports syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * "complain on peek" taggedAs(SlowTest) is (pending)
     *                                       ^
     * </pre>
     *
     * <p>
     * For more information and examples of this method's use, see the <a href="FreeSpec.html">main documentation</a> for trait <code>FreeSpec</code>.
     * </p>
     */
    def is(testFun: => PendingNothing) {
      registerTestToRun(specText, tags, testFun _)
    }

    /**
     * Supports registration of tagged, ignored tests.
     *
     * <p>
     * For example, this method supports syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * "complain on peek" taggedAs(SlowTest) ignore { ... }
     *                                       ^
     * </pre>
     *
     * <p>
     * For more information and examples of this method's use, see the <a href="FreeSpec.html">main documentation</a> for trait <code>FreeSpec</code>.
     * </p>
     */
    def ignore(testFun: => Unit) {
      registerTestToIgnore(specText, tags, testFun _)
    }
  }       

  /**
   * A class that via an implicit conversion (named <code>convertToFreeSpecStringWrapper</code>) enables
   * methods <code>in</code>, <code>is</code>, <code>taggedAs</code> and <code>ignore</code>,
   * as well as the dash operator (<code>-</code>), to be invoked on <code>String</code>s.
   *
   * @author Bill Venners
   */
  protected final class FreeSpecStringWrapper(string: String) {

    /**
     * Register some text that may surround one or more tests. The passed
     * passed function value may contain surrounding text registrations (defined with dash (<code>-</code>)) and/or tests
     * (defined with <code>in</code>). This trait's implementation of this method will register the
     * text (passed to the contructor of <code>FreeSpecStringWrapper</code> and immediately invoke the passed function.
     */
    def - (fun: => Unit) {
      // TODO: Fix the resource name and method name
      
      handleNestedBranch(string, None, fun, "itCannotAppearInsideAnIt", "FreeSpec.scala", "it")
      // registerNestedBranch(string, None, fun, "describeCannotAppearInsideAnIt", "FreeSpec.scala", "describe")
    }

    /**
     * Supports test registration.
     *
     * <p>
     * For example, this method supports syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * "complain on peek" in { ... }
     *                    ^
     * </pre>
     *
     * <p>
     * For more information and examples of this method's use, see the <a href="FreeSpec.html">main documentation</a> for trait <code>FreeSpec</code>.
     * </p>
     */
    def in(f: => Unit) {
      registerTestToRun(string, List(), f _)
    }

    /**
     * Supports ignored test registration.
     *
     * <p>
     * For example, this method supports syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * "complain on peek" ignore { ... }
     *                    ^
     * </pre>
     *
     * <p>
     * For more information and examples of this method's use, see the <a href="FreeSpec.html">main documentation</a> for trait <code>FreeSpec</code>.
     * </p>
     */
    def ignore(f: => Unit) {
      registerTestToIgnore(string, List(), f _)
    }

    /**
     * Supports pending test registration.
     *
     * <p>
     * For example, this method supports syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * "complain on peek" is (pending)
     *                    ^
     * </pre>
     *
     * <p>
     * For more information and examples of this method's use, see the <a href="FreeSpec.html">main documentation</a> for trait <code>FreeSpec</code>.
     * </p>
     */
    def is(f: => PendingNothing) {
      registerTestToRun(string, List(), f _)
    }

    /**
     * Supports tagged test registration.
     *
     * <p>
     * For example, this method supports syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * "complain on peek" taggedAs(SlowTest) in { ... }
     *                    ^
     * </pre>
     *
     * <p>
     * For more information and examples of this method's use, see the <a href="FreeSpec.html">main documentation</a> for trait <code>FreeSpec</code>.
     * </p>
     */
    def taggedAs(firstTestTag: Tag, otherTestTags: Tag*) = {
      val tagList = firstTestTag :: otherTestTags.toList
      new ResultOfTaggedAsInvocationOnString(string, tagList)
    }
  }

  /**
   * Implicitly converts <code>String</code>s to <code>FreeSpecStringWrapper</code>, which enables
   * methods <code>in</code>, <code>is</code>, <code>taggedAs</code> and <code>ignore</code>,
   * as well as the dash operator (<code>-</code>), to be invoked on <code>String</code>s.
   */
  protected implicit def convertToFreeSpecStringWrapper(s: String) = new FreeSpecStringWrapper(s)

  /**
   * Supports shared test registration in <code>FreeSpec</code>s.
   *
   * <p>
   * This field enables syntax such as the following:
   * </p>
   *
   * <pre class="stHighlight">
   * behave like nonFullStack(stackWithOneItem)
   * ^
   * </pre>
   *
   * <p>
   * For more information and examples of the use of <cod>behave</code>, see the <a href="#SharedTests">Shared tests section</a>
   * in the main documentation for this trait.
   * </p>
   */
  protected val behave = new BehaveWord

  // paste stopped here
  /*
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
      handleTest(thisSuite, specText, testFun _, "itCannotAppearInsideAnotherIt", "FunSpec.scala", "apply", testTags: _*)
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
    handleNestedBranch(description, None, fun, "describeCannotAppearInsideAnIt", "FunSpec.scala", "describe")
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
*/
  // This one is no longer used. Disentanglement
  final override def withFixture(test: NoArgTest) {
    throw new UnsupportedOperationException
  }
  
  final override def testNames: Set[String] = {
    ensureTestResultsRegistered(thisSuite)
    // I'm returning a ListSet here so that they tests will be run in registration order
    ListSet(atomic.get.testNamesList.toArray: _*)
  }

  final override def expectedTestCount(filter: Filter): Int = {
    ensureTestResultsRegistered(thisSuite)
    super.expectedTestCount(filter)
  }

  final protected override def runTest(testName: String, reporter: Reporter, stopper: Stopper, configMap: Map[String, Any], tracker: Tracker) {

    ensureTestResultsRegistered(thisSuite)
    
    def dontInvokeWithFixture(theTest: TestLeaf) {
      theTest.testFun()
    }

    runTestImpl(thisSuite, testName, reporter, stopper, configMap, tracker, true, dontInvokeWithFixture)
  }

  final override def tags: Map[String, Set[String]] = {
    ensureTestResultsRegistered(thisSuite)
    atomic.get.tagsMap
  }
  
  /*
   * Use Suite.run implementation. Allow overriding nestedSuites and runNestedSuites.
   */
  final override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
      configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {

    ensureTestResultsRegistered(thisSuite)
    runPathTestsImpl(thisSuite, testName, reporter, stopper, filter, configMap, distributor, tracker, info, true, runTest)
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
    throw new UnsupportedOperationException
    // ensureTestResultsRegistered(isAnInitialInstance, this)
    // runTestsImpl(thisSuite, testName, reporter, stopper, filter, configMap, distributor, tracker, info, true, runTest)
  }

  final protected override def runNestedSuites(reporter: Reporter, stopper: Stopper, filter: Filter,
                                configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
  }
  
  // Don't even allow nested suites in here, for starters at least, because they would run *after* the tests run, which 
  // would be inconsistent with the rest of ScalaTest. If you want to do nested suites, can wrap one of these in another
  // one that does nested suites.
  final override def nestedSuites: List[Suite] = Nil
}

