import sbt._
import Keys._
import Build.data
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

object ScalaTestSbtPlugin extends Plugin
{
	// configuration points, like the built in `version`, `libraryDependencies`, or `compile`
	// by implementing Plugin, these are automatically imported in a user's `build.sbt`
	val stargs = SettingKey[String]("stargs")
        val stTask = InputKey[Unit]("st")

	// a group of settings ready to be added to a Project
	// to automatically add them, do 
	val stSettings = Seq(
		stargs := "", 
        stTask <<= inputTask { (argsTask: TaskKey[Seq[String]]) =>
          (argsTask, fullClasspath in Test, stargs, scalaInstance, taskTemporaryDirectory) map { (args: Seq[String], classPathList, stargs, si, temp) =>
             var arrayBuffer = new ArrayBuffer[String]()
             val runpathList:Seq[String] = classPathList map {attr => attr.data.getAbsolutePath() }
             arrayBuffer += "-p"
             arrayBuffer += runpathList.mkString(" ")
             if(args.length > 0) {
               val dashIdx = args.indexOf("--")
               if(dashIdx >= 0) {
                 // -- overrides stargs, let's break the arguments into 2 parts, part 1 for suite names and part 2 for override args.
                 val (suiteNames, otherArgsWithDash) = args.splitAt(dashIdx)
                 val otherArgs = otherArgsWithDash.tail // Get rid of the --
                        
                 // We'll translate suite into existing suite() and passed in together with other stargs
                 arrayBuffer ++= (for(suiteName <- suiteNames) yield { List("-s", suiteName) }).flatten
                 arrayBuffer ++= parseFriendlyParams(otherArgs.toArray)
               }
               else {
                 // Suite name only, will use stargs for other arguments.
                 // We'll translate suite into existing suite() and passed in together with other stargs
                 arrayBuffer ++= (for(arg <- args) yield { List("-s", arg) }).flatten
                 arrayBuffer ++= parseFriendlyParams(stargs.trim.split(" "))
               }
             }
             else if(stargs.trim() != "")
               arrayBuffer ++= parseFriendlyParams(stargs.trim.split(" "))
             
             val loaderClasspath = classPathList map { cp => new File(cp.data.getAbsolutePath()) }
             val loader = TestFramework.createTestLoader(loaderClasspath, si, IO.createUniqueDirectory(temp)) 
             val runnerClass = loader.loadClass("org.scalatest.tools.Runner")
             val runMethod = runnerClass.getMethod("run", classOf[Array[String]])
             val runArgs = arrayBuffer.toArray.filter(!_.equals(""))
             runMethod.invoke(null, Array(runArgs: _*)) // run method is static.
          }
        }
	  )
  
  // alternatively, by overriding `settings`, they could be automatically added to a Project
  // override val settings = Seq(...)

  private def parseFriendlyParams(friendlyArgs:Array[String]): Array[String] = {
    val (propsList, includesList, excludesList, repoArgsList, concurrentList, memberOnlyList, wildcardList, suiteList, junitList, testngList) = 
      new FriendlyParamsTranslator().parsePropsAndTags(friendlyArgs)
    val arrayBuffer = new ArrayBuffer[String]()
    arrayBuffer ++= propsList ::: includesList ::: excludesList ::: repoArgsList ::: concurrentList ::: memberOnlyList ::: wildcardList :::
                    suiteList ::: junitList ::: testngList
    arrayBuffer.toArray
  }
	
  private class FriendlyParamsTranslator {
  
    private val validConfigMap = Map(
                                     "dropteststarting" -> "N", 
                                     "droptestsucceeded" -> "C", 
                                     "droptestignored" -> "X", 
                                     "droptestpending" -> "E", 
                                     "dropsuitestarting" -> "H", 
                                     "dropsuitecompleted" -> "L", 
                                     "dropinfoprovided" -> "O", 
                                     "nocolor" -> "W", 
                                     "shortstacks" -> "S", 
                                     "fullstacks" -> "F", 
                                     "durations" -> "D"
                                    )
  
    private def extractContentInBracket(raw:String, it:Iterator[String], expected:String):String = {
      if(!raw.startsWith("("))
        throw new IllegalArgumentException("Invalid configuration, example valid configuration: " + expected)
      val withBrackets = if(raw.endsWith(")"))
                           raw
                         else 
                           parseUntilFound(raw, ")", it)
      withBrackets.substring(1, withBrackets.length() - 1)
    }
    
    private def parseUntilFound(value:String, endsWith:String, it:Iterator[String]):String = {
      if(it.hasNext) {
        val next = it.next()
        if(next.endsWith(endsWith))
          value + next
        else
          parseUntilFound(value + next, endsWith, it)
      }
      else
        throw new IllegalArgumentException("Unable to find '" + endsWith + "'")
    }
    
    private def parseCompoundParams(rawParamsStr:String, it:Iterator[String], expected:String):Array[String] = {
      val rawClassArr = extractContentInBracket(rawParamsStr, it, expected).split(",")
      for(rawClass <- rawClassArr) yield {
        val trimmed = rawClass.trim()
        if(trimmed.length() > 1 && trimmed.startsWith("\"") && trimmed.endsWith("\""))
          trimmed.substring(1, trimmed.length() - 1)
        else
          trimmed
      }
    }
    
    private def translateCompoundParams(rawParamsStr:String, it:Iterator[String], expected:String):String = {
      val paramsArr = parseCompoundParams(rawParamsStr, it, expected)
      paramsArr.mkString(" ")
    }
    
    private def parseParams(rawParamsStr:String, it:Iterator[String], validParamSet:Set[String], expected:String):Map[String, String] = {    
      if(rawParamsStr.length() > 0) {
        val paramsStr = extractContentInBracket(rawParamsStr, it, expected)
        val configsArr:Array[String] = paramsStr.split(",")
        val tuples = for(configStr <- configsArr) yield {
          val keyValueArr = configStr.trim().split("=")
          if(keyValueArr.length == 2) {
            // Value config param
            val key:String = keyValueArr(0).trim()
            if(!validParamSet.contains(key))
              throw new IllegalArgumentException("Invalid configuration: " + key)
            val rawValue = keyValueArr(1).trim()
            val value:String = 
              if(rawValue.startsWith("\"") && rawValue.endsWith("\"") && rawValue.length() > 1) 
                rawValue.substring(1, rawValue.length() - 1)
              else
                rawValue
            (key -> value)
          }
          else
            throw new IllegalArgumentException("Invalid configuration: " + configStr)
        }
        Map[String, String]() ++ tuples
      }
      else
        Map[String, String]()
    }
    
    private def translateConfigs(rawConfigs:String):String = {
      val configArr = rawConfigs.split(" ")
      val translatedArr = configArr.map {config => 
        val translatedOpt:Option[String] = validConfigMap.get(config)
        translatedOpt match {
          case Some(translated) => translated
          case None => throw new IllegalArgumentException("Invalid config value: " + config)
        }
      }
      translatedArr.mkString
    }
    
    private def getTranslatedConfig(paramsMap:Map[String, String]):String = {
      val configOpt:Option[String] = paramsMap.get("config")
      configOpt match {
        case Some(configStr) => translateConfigs(configStr)
        case None => ""
      }
    }
  
    // Template method as extension point for subclasses.
    private def validateSupportedPropsAndTags(s:String) {
    
    }
  
    private def translateCompound(inputString:String, friendlyName:String, dash:String, it:Iterator[String]):List[String] = {
      val translatedList = new ListBuffer[String]()
      val elements:Array[String] = parseCompoundParams(inputString.substring(friendlyName.length()), it, friendlyName + "(a, b, c)")
      elements.foreach{ element => 
        translatedList += dash
        translatedList += element
      }
      translatedList.toList
    }
  
    private def parseDashAndArgument(dash:String, replaceDeprecated:String, it:Iterator[String]):List[String] = {
      println(dash + " is deprecated, use " + replaceDeprecated + " instead.")
      val translatedList = new ListBuffer[String]()
      translatedList += dash
      if(it.hasNext)
        translatedList += it.next
      translatedList.toList
    }
  
    private def showDeprecated(inputString:String, replaceDeprecated:String):String = {
      // May be we can use a logger later
      println(inputString + " is deprecated, use " + replaceDeprecated + " instead.")
      inputString
    }
  
    private def translateKeyValue(value:String, elementName:String, translated:String, requiredAttrList:List[String], 
                                  optionalAttrList:List[String], exampleValid:String, it:Iterator[String]):List[String] = {
      val paramsMap:Map[String, String] = parseParams(value.substring(elementName.length()), it, (requiredAttrList ::: optionalAttrList).toSet, exampleValid)
      val translatedList = new ListBuffer[String]()
      translatedList += translated + getTranslatedConfig(paramsMap)
      requiredAttrList.filter(attr => attr != "config").foreach { attr => 
        val option:Option[String] = paramsMap.get(attr)
        option match {
          case Some(value) => translatedList += value
          case None => throw new IllegalArgumentException(elementName + " requires " + attr + " to be specified, example: " + exampleValid)
        }
      }
      optionalAttrList.filter(attr => attr != "config").foreach { attr => 
        val option:Option[String] = paramsMap.get(attr)
        option match {
          case Some(value) => translatedList += value
          case None => // Do nothing since it's optional
        }
      }
      translatedList.toList
    }

    def parsePropsAndTags(args: Array[String]) = {

      val props = new ListBuffer[String]()
      val includes = new ListBuffer[String]()
      val excludes = new ListBuffer[String]()
      val repoArgs = new ListBuffer[String]()
      val concurrent = new ListBuffer[String]()
      val memberOnlys = new ListBuffer[String]()
      val wildcards = new ListBuffer[String]()
      val suites = new ListBuffer[String]()
      val junits = new ListBuffer[String]()
      val testngs = new ListBuffer[String]()

      val it = args.iterator
      while (it.hasNext) {

        val s = it.next

        validateSupportedPropsAndTags(s)
      
        if (s.startsWith("-D")) 
          props += s
        else if (s =="-n") 
          includes ++= parseDashAndArgument(s, "include", it)
        else if (s.startsWith("include")) 
          includes ++= List("-n", translateCompoundParams(s.substring("include".length()), it, "include(a, b, c)"))
        else if (s == "-l") 
          excludes ++= parseDashAndArgument(s, "exclude", it)
        else if (s.startsWith("exclude")) 
          excludes ++= List("-l", translateCompoundParams(s.substring("exclude".length()), it, "exclude(a, b, c)"))
        else if (s.startsWith("-o")) 
          repoArgs += showDeprecated(s, "stdout")
        else if (s.startsWith("stdout")) 
          repoArgs += "-o" + getTranslatedConfig(parseParams(s.substring("stdout".length()), it, Set("config"), "stdout"))
        else if (s.startsWith("-e")) 
          repoArgs += showDeprecated(s, "stderr")
        else if (s.startsWith("stderr")) 
          repoArgs += "-e" + getTranslatedConfig(parseParams(s.substring("stderr".length()), it, Set("config"), "stderr"))
        else if (s.startsWith("-g")) 
          repoArgs += showDeprecated(s, "graphic")
        else if (s.startsWith("graphic")) {
          val paramsMap:Map[String, String] = parseParams(s.substring("graphic".length()), it, Set("config"), "graphic")
          val dashG = "-g" + getTranslatedConfig(paramsMap)
          if(dashG.indexOf("S") >= 0)
            throw new IllegalArgumentException("Cannot specify an 'shortstacks' (present short stack traces) configuration parameter for the graphic reporter (because it shows them anyway): ")
          if(dashG.indexOf("F") >= 0)
            throw new IllegalArgumentException("Cannot specify an 'fullstacks' (present full stack traces) configuration parameter for the graphic reporter (because it shows them anyway): ")
          if(dashG.indexOf("W") >= 0)
            throw new IllegalArgumentException("Cannot specify an 'nocolor' (present without color) configuration parameter for the graphic reporter")
          if(dashG.indexOf("D") >= 0 )
            throw new IllegalArgumentException("Cannot specify an 'durations' (present all durations) configuration parameter for the graphic reporter (because it shows them all anyway)")
          repoArgs += dashG
        }
        else if (s.startsWith("-f")) 
          repoArgs ++= parseDashAndArgument(s, "file(directory=\"xxx\")", it)
        else if (s.startsWith("file")) 
          repoArgs ++= translateKeyValue(s, "file", "-f", List("filename"), List("config"), "file(directory=\"xxx\")", it)
        else if (s.startsWith("-u")) 
          repoArgs ++= parseDashAndArgument(s, "junitxml(directory=\"xxx\")", it)
        else if(s.startsWith("junitxml")) 
          repoArgs ++= translateKeyValue(s, "junitxml", "-u", List("directory"), Nil, "junitxml(directory=\"xxx\")", it)
        else if (s.startsWith("-d")) 
          repoArgs ++= parseDashAndArgument(s, "dashboard(directory=\"xxx\", archive=\"xxx\")", it)
        else if (s.startsWith("-a")) 
          repoArgs ++= parseDashAndArgument(s, "dashboard(directory=\"xxx\", archive=\"xxx\")", it)
        else if (s.startsWith("dashboard")) {
          repoArgs += "-d"
          val paramsMap:Map[String, String] = parseParams(s.substring("dashboard".length()), it, Set("directory", "archive"), "dashboard(directory=\"xxx\", archive=\"xxx\")")
          val directoryOpt:Option[String] = paramsMap.get("directory")
          directoryOpt match {
            case Some(dir) => repoArgs += dir
            case None => throw new IllegalArgumentException("dashboard requires directory to be specified, example: dashboard(directory=\"xxx\", archive=\"xxx\")")
          }
          val archiveOpt:Option[String] = paramsMap.get("archive")
          archiveOpt match {
            case Some(archive) => 
              repoArgs += "-a"
              repoArgs += archive
            case None => 
          }
        }
        // To be enabled when and if native scalatest xml reporter is available
        /*else if (s.startsWith("-x")) 
          repoArgs ++= parseDashAndArgument(s, "xml(directory=\"xxx\")", it)
        else if (s.startsWith("xml")) 
          repoArgs ++= translateKeyValue(s, "xml", "-x", List("directory"), Nil, "xml(directory=\"xxx\")", it)*/
        else if (s.startsWith("-h")) 
          repoArgs ++= parseDashAndArgument(s, "html(filename=\"xxx\")", it)
        else if (s.startsWith("html")) 
          repoArgs ++= translateKeyValue(s, "html", "-h", List("filename"), List("config"), "html(filename=\"xxx\")", it)
        else if (s.startsWith("-r")) 
          repoArgs ++= parseDashAndArgument(s, "reporterclass(classname=\"xxx\")", it)
        else if (s.startsWith("reporterclass")) {
          val paramsMap:Map[String, String] = parseParams(s.substring("reporterclass".length()), it, Set("classname", "config"), "reporterclass(classname=\"xxx\")")
          val classnameOpt:Option[String] = paramsMap.get("classname")
          val classname:String = classnameOpt match {
            case Some(clazzname) => clazzname
            case None => throw new IllegalArgumentException("reporterclass requires classname to be specified, example: reporterclass(classname=\"xxx\")")
          }
          val dashR = "-r" + getTranslatedConfig(paramsMap)
          if(dashR.indexOf("S") >= 0)
            throw new IllegalArgumentException("Cannot specify an 'shortstacks' (present short stack traces) configuration parameter for a custom reporter: " + dashR + " " + classname)
          if(dashR.indexOf("F") >= 0)
            throw new IllegalArgumentException("Cannot specify an 'fullstacks' (present full stack traces) configuration parameter for a custom reporter: " + dashR + " " + classname)
          if(dashR.indexOf("W") >= 0)
            throw new IllegalArgumentException("Cannot specify an 'nocolor' (present without color) configuration parameter for a custom reporter: " + dashR + " " + classname)
          if(dashR.indexOf("D") >= 0 )
            throw new IllegalArgumentException("Cannot specify an 'durations' (present all durations) configuration parameter for a custom reporter: " + dashR + " " + classname)
          repoArgs += dashR
          repoArgs += classname
        }
        else if(s == "-c" || s == "concurrent") 
          concurrent += "-c"
        else if(s == "-m") 
          memberOnlys ++= parseDashAndArgument(s, "membersonly(a, b, c)", it)
        else if(s.startsWith("membersonly")) 
          memberOnlys ++= translateCompound(s, "membersonly", "-m", it)
        else if(s == "-w") 
          wildcards ++= parseDashAndArgument(s, "wildcard(a, b, c)", it)
        else if(s.startsWith("wildcard")) 
          wildcards ++= translateCompound(s, "wildcard", "-w", it)
        else if(s == "-s") 
          suites ++= parseDashAndArgument(s, "suite(a, b, c)", it)
        else if(s.startsWith("suite")) 
          suites ++= translateCompound(s, "suite", "-s", it)
        else if(s == "-j") 
          junits ++= parseDashAndArgument(s, "junit(a, b, c)", it)
        else if(s.startsWith("junit")) 
          junits ++= translateCompound(s, "junit", "-j", it)
        else if(s == "-t")
          testngs ++= parseDashAndArgument(s, "testng(a, b, c)", it)
        else if(s.startsWith("testng")) 
          testngs ++= translateCompound(s, "testng", "-t", it)
        else
          throw new IllegalArgumentException("Unrecognized argument: " + s)
      }
      (props.toList, includes.toList, excludes.toList, repoArgs.toList, concurrent.toList, memberOnlys.toList, wildcards.toList, 
      suites.toList, junits.toList, testngs.toList)
    }
  }
}
