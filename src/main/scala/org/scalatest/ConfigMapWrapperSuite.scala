package org.scalatest

// TODO: Need to create a separate instance when expectedTestCount is invoked, testNames, nestedSuites, and tags, etc.
// Could memoize that instance I suppose. a lazy one for all non-run method invocations.
/**
 * Wrapper <code>Suite</code> that passes an instance of the config map to the constructor of the
 * wrapped <code>Suite</code> when <code>run</code> is invoked.
 */
final class ConfigMapWrapperSuite(clazz: Class[_ <: Suite]) extends Suite {
  override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter,
      configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
    val constructor = clazz.getConstructor(classOf[Map[_, _]])
    val suite = constructor.newInstance(configMap)
    suite.run(testName, reporter, stopper, filter, configMap, distributor, tracker)
  }
  
  /**
   * Suite style name.
   */
  final override def styleName: String = "ConfigMapWrapperSuite"
}
