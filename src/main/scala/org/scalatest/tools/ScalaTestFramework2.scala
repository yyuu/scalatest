package org.scalatest.tools

import org.scalasbt.testing.Framework
import org.scalasbt.testing.Logger
import org.scalasbt.testing.EventHandler
import org.scalasbt.testing.Fingerprint
import org.scalasbt.testing.Selector
import org.scalasbt.testing.Task
import org.scalatest.Reporter
import SuiteDiscoveryHelper._
import StringReporter.colorizeLinesIndividually
import org.scalatest.Tracker
import org.scalatest.WrapWith
import org.scalatest.Suite
import org.scalatest.Suite.formatterForSuiteStarting
import org.scalatest.Suite.formatterForSuiteCompleted
import org.scalatest.Suite.formatterForSuiteAborted
import org.scalatest.events.SuiteStarting
import org.scalatest.events.TopOfClass
import org.scalatest.Stopper
import org.scalatest.events.SuiteCompleted
import org.scalatest.events.SuiteAborted
import org.scalatest.events.SeeStackDepthException
import org.scalatest.Filter
import org.scalatest.tools.Runner.parsePropertiesArgsIntoMap
import org.scalatest.tools.Runner.parseCompoundArgIntoSet
import org.scalasbt.testing.SkippedEvent
import org.scalasbt.testing.TestSelector
import org.scalasbt.testing.FailureEvent
import org.scalasbt.testing.SuccessEvent
import org.scalasbt.testing.ErrorEvent
import org.scalasbt.testing.SuiteSelector
import org.scalasbt.testing.NestedTestSelector
import org.scalasbt.testing.NestedSuiteSelector
import org.scalatest.DynaTags
import org.scalatest.tools.Runner.SELECTED_TAG
import org.scalatest.tools.Runner.mergeMap
import org.scalatest.DispatchReporter
import org.scalatest.events.RunCompleted
import org.scalatest.events.RunStarting
import org.scalatest.events.Summary

class ScalaTestFramework2 extends Framework {

  def name = "ScalaTest"
    
  def fingerprints = 
    Array(
      new org.scalasbt.testing.SubclassFingerprint {
        def superclassName = "org.scalatest.Suite"
        def isModule = false
      }, 
      new org.scalasbt.testing.AnnotatedFingerprint {
        def annotationName = "org.scalatest.WrapWith"
        def isModule = false
      }, 
      new org.scalasbt.testing.DoNotDiscoverFingerprint {
        def annotationName = "org.scalatest.DoNotDiscover"
      })
      
  class ScalaTestTask(fullyQualifiedName: String, loader: ClassLoader, dispatchReporter: Option[DispatchReporter], tracker: Tracker, eventHandler: EventHandler, 
                      tagsToInclude: Set[String], tagsToExclude: Set[String], selectors: Array[Selector], configMap: Map[String, String], 
                      summaryCounter: SummaryCounter) extends Task {
    
    def tags = {
      Array.empty[String]
    }
    
    def loadSuiteClass = {
      try {
        Class.forName(fullyQualifiedName, true, loader)
      }
      catch {
        case e: Exception => 
          throw new IllegalArgumentException("Unable to load class: " + fullyQualifiedName)
      }
    }
    
    def execute = {
      val suiteClass = loadSuiteClass
      if (isAccessibleSuite(suiteClass) || isRunnable(suiteClass)) {
        val suiteStartTime = System.currentTimeMillis

        val wrapWithAnnotation = suiteClass.getAnnotation(classOf[WrapWith])
        val suite = 
        if (wrapWithAnnotation == null)
          suiteClass.newInstance.asInstanceOf[Suite]
        else {
          val suiteClazz = wrapWithAnnotation.value
          val constructorList = suiteClazz.getDeclaredConstructors()
          val constructor = constructorList.find { c => 
              val types = c.getParameterTypes
              types.length == 1 && types(0) == classOf[java.lang.Class[_]]
            }
          constructor.get.newInstance(suiteClass).asInstanceOf[Suite]
        }
        
        val report = new SbtReporter(suite.suiteId, fullyQualifiedName, eventHandler, dispatchReporter, summaryCounter)
        val formatter = formatterForSuiteStarting(suite)
        
        val filter = 
          if (selectors.length == 0)
            Filter(if (tagsToInclude.isEmpty) None else Some(tagsToInclude), tagsToExclude)
          else {
            var suiteTags = Map[String, Set[String]]()
            var testTags = Map[String, Map[String, Set[String]]]()
            var hasTest = false
            var hasNested = false
            
            selectors.foreach { selector => 
              selector match {
                case suiteSelector: SuiteSelector => 
                  suiteTags = mergeMap[String, Set[String]](List(suiteTags, Map(suite.suiteId -> Set(SELECTED_TAG)))) { _ ++ _ }
                case testSelector: TestSelector =>
                  testTags = mergeMap[String, Map[String, Set[String]]](List(testTags, Map(suite.suiteId -> Map(testSelector.getTestName() -> Set(SELECTED_TAG))))) { (testMap1, testMap2) => 
                    mergeMap[String, Set[String]](List(testMap1, testMap2)) { _ ++ _}
                  }
                  hasTest = true
                case nestedSuiteSelector: NestedSuiteSelector => 
                  suiteTags = mergeMap[String, Set[String]](List(suiteTags, Map(nestedSuiteSelector.getSuiteId -> Set(SELECTED_TAG)))) { _ ++ _ }
                  hasNested = true
                case nestedTestSelector: NestedTestSelector => 
                  testTags = mergeMap[String, Map[String, Set[String]]](List(testTags, Map(nestedTestSelector.getSuiteId -> Map(nestedTestSelector.getTestName -> Set(SELECTED_TAG))))) { (testMap1, testMap2) => 
                    mergeMap[String, Set[String]](List(testMap1, testMap2)) { _ ++ _}
                  }
                  hasNested = true
              }
            }
            // Only exclude nested suites when using -s XXX -t XXXX, same behaviour with Runner.
            val excludeNestedSuites = hasTest && !hasNested 
            new Filter(if (tagsToInclude.isEmpty) Some(Set(SELECTED_TAG)) else Some(tagsToInclude + SELECTED_TAG), tagsToExclude, false, new DynaTags(suiteTags.toMap, testTags.toMap))
          }

        report(SuiteStarting(tracker.nextOrdinal(), suite.suiteName, suite.suiteId, Some(suiteClass.getName), suite.decodedSuiteName, formatter, Some(TopOfClass(suiteClass.getName))))

        try {
          suite.run(None, report, new Stopper {}, filter, configMap, None, tracker)

          val formatter = formatterForSuiteCompleted(suite)

          val duration = System.currentTimeMillis - suiteStartTime

          report(SuiteCompleted(tracker.nextOrdinal(), suite.suiteName, suite.suiteId, Some(suiteClass.getName), suite.decodedSuiteName, Some(duration), formatter, Some(TopOfClass(suiteClass.getName))))

        }
        catch {       
          case e: Exception => {

            // TODO: Could not get this from Resources. Got:
            // java.util.MissingResourceException: Can't find bundle for base name org.scalatest.ScalaTestBundle, locale en_US
            // TODO Chee Seng, I wonder why we couldn't access resources, and if that's still true. I'd rather get this stuff
            // from the resource file so we can later localize.
            val rawString = "Exception encountered when attempting to run a suite with class name: " + suiteClass.getName
            val formatter = formatterForSuiteAborted(suite, rawString)

            val duration = System.currentTimeMillis - suiteStartTime
            report(SuiteAborted(tracker.nextOrdinal(), rawString, suite.suiteName, suite.suiteId, Some(suiteClass.getName), suite.decodedSuiteName, Some(e), Some(duration), formatter, Some(SeeStackDepthException)))
          }
        }
        
        Array.empty[Task]
      }
       else 
         throw new IllegalArgumentException("Class " + fullyQualifiedName + " is neither accessible accesible org.scalatest.Suite nor runnable.")
    }
  }
  
  private[tools] class SummaryCounter {
    var testsSucceededCount = 0
    var testsFailedCount = 0
    var testsIgnoredCount = 0
    var testsPendingCount = 0
    var testsCanceledCount = 0
    var suitesCompletedCount = 0
    var suitesAbortedCount = 0
  }
      
  class ScalaTestRunner(loader: ClassLoader, tagsToInclude: Set[String], tagsToExclude: Set[String], configMap: Map[String, String], 
                        dispatchReporter: Option[DispatchReporter], eventHandler: EventHandler, useSbtLogInfoReporter: Boolean) 
                        extends org.scalasbt.testing.Runner {
    
    var isDone = false
    val tracker = new Tracker
    val summaryCounter = new SummaryCounter
    val runStartTime = System.currentTimeMillis
    dispatchReporter match {
      case Some(dispatchReporter) => 
        dispatchReporter(RunStarting(tracker.nextOrdinal(), 0, configMap))
      case None => 
        // Do nothing.
    }
    
    def task(fullyQualifiedName: String, fingerprint: Fingerprint) = {
      new ScalaTestTask(fullyQualifiedName, loader, dispatchReporter, tracker, eventHandler, tagsToInclude, tagsToExclude, Array.empty, configMap, summaryCounter)
    }
    
    def task(fullyQualifiedName: String, isModule: Boolean, selectors: Array[Selector]) = {
      new ScalaTestTask(fullyQualifiedName, loader, dispatchReporter, tracker, eventHandler, Set(SELECTED_TAG), Set.empty, selectors, configMap, summaryCounter)
    }
    
    def done = {
      if (!isDone) {
        dispatchReporter match {
          case Some(dispatchReporter) => 
            val duration = System.currentTimeMillis - runStartTime
            val summary = new Summary(summaryCounter.testsSucceededCount, summaryCounter.testsFailedCount, summaryCounter.testsIgnoredCount, summaryCounter.testsPendingCount, 
                                      summaryCounter.testsCanceledCount, summaryCounter.suitesCompletedCount, summaryCounter.suitesAbortedCount)
            dispatchReporter(RunCompleted(tracker.nextOrdinal(), Some(duration), Some(summary)))
            dispatchReporter.dispatchDisposeAndWaitUntilDone()
            isDone = true
            useSbtLogInfoReporter
          case None => 
            false
        }
      }
      else
        throw new IllegalStateException("done method is called twice")
    }
  }
      
  def runner(args: Array[String], testClassLoader: ClassLoader, eventHandler: EventHandler, loggers: Array[Logger]) = {
    
    class SbtLogInfoReporter(presentAllDurations: Boolean, presentInColor: Boolean, presentShortStackTraces: Boolean, presentFullStackTraces: Boolean) 
      extends StringReporter(presentAllDurations, presentInColor, presentShortStackTraces, presentFullStackTraces) {
    
      protected def printPossiblyInColor(text: String, ansiColor: String) {
          loggers.foreach { logger =>
            logger.info(if (logger.ansiCodesSupported && presentInColor) colorizeLinesIndividually(text, ansiColor) else text)
          }
      }

      def dispose() = ()
    }
    
    val translator = new SbtFriendlyParamsTranslator()
    val (propertiesArgsList, includesArgsList, excludesArgsList, repoArgsList, concurrentList, memberOnlyList, wildcardList, 
            suiteList, junitList, testngList) = translator.parsePropsAndTags(args.filter(!_.equals("")))
    val configMap: Map[String, String] = parsePropertiesArgsIntoMap(propertiesArgsList)
    val tagsToInclude: Set[String] = parseCompoundArgIntoSet(includesArgsList, "-n")
    val tagsToExclude: Set[String] = parseCompoundArgIntoSet(excludesArgsList, "-l")
    object SbtReporterFactory extends ReporterFactory {
          
      override def createStandardOutReporter(configSet: Set[ReporterConfigParam]) = {
        if (configSetMinusNonFilterParams(configSet).isEmpty)
          new SbtLogInfoReporter(
            configSet.contains(PresentAllDurations),
            !configSet.contains(PresentWithoutColor),
            configSet.contains(PresentShortStackTraces) || configSet.contains(PresentFullStackTraces),
            configSet.contains(PresentFullStackTraces) // If they say both S and F, F overrules
          )
        else
          new FilterReporter(
            new SbtLogInfoReporter(
              configSet.contains(PresentAllDurations),
              !configSet.contains(PresentWithoutColor),
              configSet.contains(PresentShortStackTraces) || configSet.contains(PresentFullStackTraces),
              configSet.contains(PresentFullStackTraces) // If they say both S and F, F overrules
            ),
            configSet
          )
      }
    }
        
    // If no reporters specified, just give them a default stdout reporter
    val fullReporterConfigurations: ReporterConfigurations = Runner.parseReporterArgsIntoConfigurations(if(repoArgsList.isEmpty) "-o" :: Nil else repoArgsList)
    val dispatchReporter = Some(SbtReporterFactory.getDispatchReporter(fullReporterConfigurations, None, None, testClassLoader))
    val useSbtLogInfoReporter = fullReporterConfigurations.find(repConfig => repConfig.isInstanceOf[StandardOutReporterConfiguration]).isDefined
    
    new ScalaTestRunner(testClassLoader, tagsToInclude, tagsToExclude, configMap, dispatchReporter, eventHandler, useSbtLogInfoReporter)
  }
  
  private class SbtReporter(suiteId: String, fullyQualifiedName: String, eventHandler: EventHandler, report: Option[Reporter], summaryCounter: SummaryCounter) extends Reporter {
      
      import org.scalatest.events._
      
      private def getTestSelector(eventSuiteId: String, testName: String) = {
        if (suiteId == eventSuiteId)
          new TestSelector(testName)
        else
          new NestedTestSelector(eventSuiteId, testName)
      }
      
      private def getSuiteSelector(eventSuiteId: String) = {
        if (suiteId == eventSuiteId)
          new SuiteSelector
        else
          new NestedSuiteSelector(eventSuiteId)
      }
      
      override def apply(event: Event) {
        report match {
          case Some(report) => report(event)
          case None =>
        }
        
        event match {
          // the results of running an actual test
          case t: TestPending => 
            summaryCounter.testsPendingCount += 1
            eventHandler.handle(new SkippedEvent(fullyQualifiedName, false, getTestSelector(t.suiteId, t.testName)))
          case t: TestFailed => 
            summaryCounter.testsFailedCount += 1
            eventHandler.handle(new FailureEvent(fullyQualifiedName, false, getTestSelector(t.suiteId, t.testName), t.throwable.getOrElse(null)))
          case t: TestSucceeded => 
            summaryCounter.testsSucceededCount += 1
            eventHandler.handle(new SuccessEvent(fullyQualifiedName, false, getTestSelector(t.suiteId, t.testName)))
          case t: TestIgnored => 
            summaryCounter.testsIgnoredCount += 1
            eventHandler.handle(new SkippedEvent(fullyQualifiedName, false, getTestSelector(t.suiteId, t.testName)))
          case t: TestCanceled =>
            summaryCounter.testsCanceledCount += 1
            eventHandler.handle(new SkippedEvent(fullyQualifiedName, false, getTestSelector(t.suiteId, t.testName)))
          case t: SuiteCompleted => 
            summaryCounter.suitesCompletedCount += 1
          case t: SuiteAborted => 
            summaryCounter.suitesAbortedCount += 1
            eventHandler.handle(new ErrorEvent(fullyQualifiedName, false, getSuiteSelector(t.suiteId), t.throwable.getOrElse(null)))
          case _ => 
        }
      }
    }
}