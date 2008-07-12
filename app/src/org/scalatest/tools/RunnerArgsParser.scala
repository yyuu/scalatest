package org.scalatest.tools

import scala.collection.mutable.ListBuffer

object RunnerArgsParser {

  private[scalatest] def parseArgs(args: Array[String]) = {
    
    checkArgsForValidity(args) match {
      case Some(s) => {
        println(s)
        exit(1)
      }
      case None =>
    }


    val runpath = new ListBuffer[String]()
    val reporters = new ListBuffer[String]()
    val suites = new ListBuffer[String]()
    val props = new ListBuffer[String]()
    val includes = new ListBuffer[String]()
    val excludes = new ListBuffer[String]()
    val concurrent = new ListBuffer[String]()
    val membersOnly = new ListBuffer[String]()
    val wildcard = new ListBuffer[String]()
    val testNGXMLFiles = new ListBuffer[String]()

    val it = args.elements
    while (it.hasNext) {

      val s = it.next

      if (s.startsWith("-D")) {
         props += s
      }
      else if (s.startsWith("-p")) {
        runpath += s
        if (it.hasNext)
          runpath += it.next
      }
      else if (s.startsWith("-g")) {
        reporters += s
      }
      else if (s.startsWith("-o")) {
        reporters += s
      }
      else if (s.startsWith("-e")) {
        reporters += s
      }
      else if (s.startsWith("-f")) {
        reporters += s
        if (it.hasNext)
          reporters += it.next
      }
      else if (s.startsWith("-n")) {
        includes += s
        if (it.hasNext)
          includes += it.next
      }
      else if (s.startsWith("-x")) {
        excludes += s
        if (it.hasNext)
          excludes += it.next
      }
      else if (s.startsWith("-r")) {

        reporters += s
        if (it.hasNext)
          reporters += it.next
      }
      else if (s.startsWith("-s")) {

        suites += s
        if (it.hasNext)
          suites += it.next
      }
      else if (s.startsWith("-m")) {

        membersOnly += s
        if (it.hasNext)
          membersOnly += it.next
      }
      else if (s.startsWith("-w")) {

        wildcard += s
        if (it.hasNext)
          wildcard += it.next
      }
      else if (s.startsWith("-c")) {

        concurrent += s
      }
      else if (s.startsWith("-t")) {

        testNGXMLFiles += s
        if (it.hasNext)
          testNGXMLFiles += it.next
      }
      else {
        throw new IllegalArgumentException("Unrecognized argument: " + s)
      }
    }
    
 
    (
      parseRunpathArgIntoList(runpath.toList),
      reporters.toList,
      parseSuiteArgsIntoNameStrings(suites.toList, "-s"),
      parsePropertiesArgsIntoMap(props.toList),
      parseCompoundArgIntoSet(includes.toList, "-n"),
      parseCompoundArgIntoSet(excludes.toList, "-x"),
      !concurrent.toList.isEmpty,
      parseSuiteArgsIntoNameStrings(membersOnly.toList, "-m"),
      parseSuiteArgsIntoNameStrings(wildcard.toList, "-w"),
      parseSuiteArgsIntoNameStrings(testNGXMLFiles.toList, "-t"),
    )
  }
 
  
   // Used to parse -s, -m, and -w args, one of which will be passed as a String as dashArg
  private[scalatest] def parseSuiteArgsIntoNameStrings(args: List[String], dashArg: String) = {

    if (args == null)
      throw new NullPointerException("args was null")

    if (args.exists(_ == null))
      throw new NullPointerException("an arg String was null")

    if (dashArg != "-s" && dashArg != "-w" && dashArg != "-m" && dashArg != "-t" && dashArg != "-j")
      throw new NullPointerException("dashArg invalid: " + dashArg)

    val lb = new ListBuffer[String]
    val it = args.elements
    while (it.hasNext) {
      val dashS = it.next
      if (dashS != dashArg)
        throw new IllegalArgumentException("Every other element, starting with the first, must be -s")
      if (it.hasNext) {
        val suiteName = it.next
        if (!suiteName.startsWith("-"))
          lb += suiteName
        else
          throw new IllegalArgumentException("Expecting a Suite class name to follow -s, but got: " + suiteName)
      }
      else
        throw new IllegalArgumentException("Last element must be a Suite class name, not a -s.")
    }
    lb.toList
  }

  private[scalatest] def parseCompoundArgIntoSet(args: List[String], expectedDashArg: String): Set[String] = 
      Set() ++ parseCompoundArgIntoList(args, expectedDashArg)

  private[scalatest] def parseRunpathArgIntoList(args: List[String]): List[String] = parseCompoundArgIntoList(args, "-p")

  private[scalatest] def parseCompoundArgIntoList(args: List[String], expectedDashArg: String): List[String] = {

    if (args == null)
      throw new NullPointerException("args was null")

    if (args.exists(_ == null))
      throw new NullPointerException("an arg String was null")

    if (args.length == 0) {
      List()
    }
    else if (args.length == 2) {
      val dashArg = args(0)
      val runpathArg = args(1)

      if (dashArg != expectedDashArg)
        throw new IllegalArgumentException("First arg must be " + expectedDashArg + ", but was: " + dashArg)

      if (runpathArg.trim.isEmpty)
        throw new IllegalArgumentException("The runpath string must actually include some non-whitespace characters.")

      val tokens = runpathArg.split("\\s")

      tokens.toList
    }
    else {
      throw new IllegalArgumentException("Runpath must be either zero or two args: " + args)
    }
  }

  private[scalatest] def parsePropertiesArgsIntoMap(args: List[String]) = {

    if (args == null)
      throw new NullPointerException("args was null")

    if (args.exists(_ == null))
      throw new NullPointerException("an arg String was null")

    if (args.exists(_.indexOf('=') == -1))
      throw new IllegalArgumentException("A -D arg does not contain an equals sign.")

    if (args.exists(!_.startsWith("-D")))
      throw new IllegalArgumentException("A spice arg does not start with -D.")

    if (args.exists(_.indexOf('=') == 2))
      throw new IllegalArgumentException("A spice arg does not have a key to the left of the equals sign.")

    if (args.exists(arg => arg.indexOf('=') == arg.length - 1))
      throw new IllegalArgumentException("A spice arg does not have a value to the right of the equals sign.")

    val tuples = for (arg <- args) yield {
      val keyValue = arg.substring(2) // Cut off the -D at the beginning
      val equalsPos = keyValue.indexOf('=')
      val key = keyValue.substring(0, equalsPos)
      val value = keyValue.substring(equalsPos + 1)
      (key, value)
    }

    scala.collection.immutable.Map() ++ tuples
  }

  
  private[scalatest] def parseReporterArgsIntoSpecs(args: List[String]) = {

    if (args == null)
      throw new NullPointerException("args was null")

    if (args.exists(_ == null))
      throw new NullPointerException("an arg String was null")

    if (args.exists(_.length < 2)) // TODO: check and print out a user friendly message for this
      throw new IllegalArgumentException("an arg String was less than 2 in length: " + args)

    for (dashX <- List("-g", "-o", "-e")) {
      if (args.toList.count(_.substring(0, 2) == dashX) > 1) // TODO: also check and print a user friendly message for this
        throw new IllegalArgumentException("Only one " + dashX + " allowed")
    }

    // TODO: also check and print a user friendly message for this
    // again here, i had to skip some things, so I had to use an iterator.
    val it = args.elements
    while (it.hasNext) it.next.substring(0, 2) match {
      case "-g" => 
      case "-o" => 
      case "-e" => 
      case "-f" => if (it.hasNext)
                     it.next // scroll past the filename
                   else
                     throw new IllegalArgumentException("-f needs to be followed by a file name arg: ")
      case "-r" => if (it.hasNext)
                    it.next // scroll past the reporter class
                   else
                     throw new IllegalArgumentException("-r needs to be followed by a reporter class name arg: ")
      case arg: String => throw new IllegalArgumentException("An arg started with an invalid character string: " + arg)
    }

    val graphicReporterSpecOption = args.find(arg => arg.substring(0, 2) == "-g") match {
      case Some(dashGString) => Some(new GraphicReporterSpec(parseConfigSet(dashGString)))
      case None => None
    }

    def buildFileReporterSpecList(args: List[String]) = {
      val it = args.elements
      val lb = new ListBuffer[FileReporterSpec]
      while (it.hasNext) {
        val arg = it.next
        arg.substring(0,2) match {
          case "-f" => lb += new FileReporterSpec(parseConfigSet(arg), it.next)
          case _ => 
        }
      }
      lb.toList
    }
    val fileReporterSpecList = buildFileReporterSpecList(args)

    val standardOutReporterSpecOption = args.find(arg => arg.substring(0, 2) == "-o") match {
      case Some(dashOString) => Some(new StandardOutReporterSpec(parseConfigSet(dashOString)))
      case None => None
    }

    val standardErrReporterSpecOption = args.find(arg => arg.substring(0, 2) == "-e") match {
      case Some(dashEString) => Some(new StandardErrReporterSpec(parseConfigSet(dashEString)))
      case None => None
    }

    def buildCustomReporterSpecList(args: List[String]) = {
      val it = args.elements
      val lb = new ListBuffer[CustomReporterSpec]
      while (it.hasNext) {
        val arg = it.next
        arg.substring(0,2) match {
          case "-r" => lb += new CustomReporterSpec(parseConfigSet(arg), it.next)
          case _ => 
        }
      }
      lb.toList
    }
    val customReporterSpecList = buildCustomReporterSpecList(args)

    // Here instead of one loop, i go through the loop several times.
    new ReporterSpecs(
      graphicReporterSpecOption,
      fileReporterSpecList,
      standardOutReporterSpecOption,
      standardErrReporterSpecOption,
      customReporterSpecList
    )
  }

  /**
   * Returns a possibly empty ConfigSet containing configuration
   * objects specified in the passed reporterArg. Configuration
   * options are specified immediately following
   * the reporter option, as in:
   *
   * -oFA
   *
   * If no configuration options are specified, this method returns an
   * empty ConfigSet. This method never returns null.
   */
  private[scalatest] def parseConfigSet(reporterArg: String): ReporterOpts.Set32 = {

    if (reporterArg == null)
      throw new NullPointerException("reporterArg was null")

    if (reporterArg.length < 2)
      throw new IllegalArgumentException("reporterArg < 2")

    // The reporterArg passed includes the initial -, as in "-oFI",
    // so the first config param will be at index 2
    val configString = reporterArg.substring(2)
    val it = configString.elements
    val allConfigs = "YZTFUPBISARG" // G for test ignored
    var mask = 0
    while (it.hasNext) 
      it.next match {
        case 'Y' => mask = mask | ReporterOpts.PresentRunStarting.mask32
        case 'Z' => mask = mask | ReporterOpts.PresentTestStarting.mask32
        case 'T' => mask = mask | ReporterOpts.PresentTestSucceeded.mask32
        case 'F' => mask = mask | ReporterOpts.PresentTestFailed.mask32
        case 'U' => mask = mask | ReporterOpts.PresentSuiteStarting.mask32
        case 'P' => mask = mask | ReporterOpts.PresentSuiteCompleted.mask32
        case 'B' => mask = mask | ReporterOpts.PresentSuiteAborted.mask32
        case 'I' => mask = mask | ReporterOpts.PresentInfoProvided.mask32
        case 'S' => mask = mask | ReporterOpts.PresentRunStopped.mask32
        case 'A' => mask = mask | ReporterOpts.PresentRunAborted.mask32
        case 'R' => mask = mask | ReporterOpts.PresentRunCompleted.mask32
        case 'G' => mask = mask | ReporterOpts.PresentTestIgnored.mask32
        case c: Char => {

          // this should be moved to the checker, and just throw an exception here with a debug message. Or allow a MatchError.
          val msg1 = Resources("invalidConfigOption", String.valueOf(c)) + '\n'
          val msg2 =  Resources("probarg", reporterArg) + '\n'

          throw new IllegalArgumentException(msg1 + msg2)
        }
      }
    ReporterOpts.Set32(mask)
  }
  
  
    // Returns an Option[String]. Some is an error message. None means no error.
  private[scalatest] def checkArgsForValidity(args: Array[String]) = {

    val lb = new ListBuffer[String]
    val it = args.elements
    while (it.hasNext) {
      val s = it.next
      // Style advice
      // If it is multiple else ifs, then make it symetrical. If one needs an open curly brace, put it on all
      // If an if just has another if, a compound statement, go ahead and put the open curly brace's around the outer one
      if (s.startsWith("-p") || s.startsWith("-f") || s.startsWith("-r") || s.startsWith("-n") || s.startsWith("-x") || s.startsWith("-s") || s.startsWith("-m") || s.startsWith("-w") || s.startsWith("-t") || s.startsWith("-j")) {
        if (it.hasNext)
          it.next
      }
      else if (!s.startsWith("-D") && !s.startsWith("-g") && !s.startsWith("-o") && !s.startsWith("-e") && !s.startsWith("-c")) {
        lb += s
      }
    }
    val argsList = lb.toList
    if (argsList.length != 0)
      Some("Unrecognized argument" + (if (argsList.isEmpty) ": " else "s: ") + argsList.mkString("", ", ", "."))
    else
      None
  }
  
  
}
