package org.scalatest.tools

import java.util.regex.Pattern
import collection.mutable.ListBuffer
import org.scalatest._
import java.io.File

private[tools] object ArgParser {

	// Returns an Option[String]. Some is an error message. None means no error.
	def checkArgsForValidity(args: Array[String]) = {

		val lb = new ListBuffer[String]
		val it = args.elements
		while (it.hasNext) {
			val s = it.next
			// Style advice
			// If it is multiple else ifs, then make it symetrical. If one needs an open curly brace, put it on all
			// If an if just has another if, a compound statement, go ahead and put the open curly brace's around the outer one
			if (s.startsWith("-p") || s.startsWith("-f") || s.startsWith("-u") || s.startsWith("-h") || s.startsWith("-r") || s.startsWith("-n") || s.startsWith("-x") || s.startsWith("-l") || s.startsWith("-s") || s.startsWith("-j") || s.startsWith("-m") || s.startsWith("-w") || s.startsWith("-t")) {
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

	//
	// Examines concurrent option arg to see if it contains an optional numeric
	// value representing the number of threads to use, e.g. -c10 for 10 threads.
	//
	// It's possible for user to specify the -c option multiple times on the
	// command line, although it isn't particularly useful.  This method scans
	// through multiples until it finds one with a number appended and uses
	// that.  If none have a number it just returns 0.
	//
	def parseConcurrentNumArg(concurrentList: List[String]):
	Int = {
		val opt = concurrentList.find(_.matches("-c\\d+"))

		opt match {
			case Some(arg) => arg.replace("-c", "").toInt
			case None      => 0
		}
	}

	def parseArgs(args: Array[String]) = {

		val runpath = new ListBuffer[String]()
		val reporters = new ListBuffer[String]()
		val suites = new ListBuffer[String]()
		val junits = new ListBuffer[String]()
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
			else if (s.startsWith("-u")) {
				reporters += s
				if (it.hasNext)
					reporters += it.next
			}
			else if (s.startsWith("-h")) {
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
				System.err.println(Resources("dashXDeprecated"))
				excludes += s.replace("-x", "-l")
				if (it.hasNext)
					excludes += it.next
			}
			else if (s.startsWith("-l")) {
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
			else if (s.startsWith("-j")) {

				junits += s
				if (it.hasNext)
					junits += it.next
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
			runpath.toList,
			reporters.toList,
			suites.toList,
			junits.toList,
			props.toList,
			includes.toList,
			excludes.toList,
			concurrent.toList,
			membersOnly.toList,
			wildcard.toList,
			testNGXMLFiles.toList
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
	def parseConfigSet(reporterArg: String): Set[ReporterConfigParam] = {

		if (reporterArg == null)
			throw new NullPointerException("reporterArg was null")

		if (reporterArg.length < 2)
			throw new IllegalArgumentException("reporterArg < 2")

		// The reporterArg passed includes the initial -, as in "-oFI",
		// so the first config param will be at index 2
		val configString = reporterArg.substring(2)
		val it = configString.elements
		var set = Set[ReporterConfigParam]()
		while (it.hasNext)
			it.next match {
				case 'Y' => // Allow the old ones for the two-release deprecation cycle, starting in 1.0
				case 'Z' => // But they have no effect. After that, drop these cases so these will generate an error.
				case 'T' =>
				// case 'F' => I decided to reuse F already, but not for a filter so it is OK
				case 'U' =>
				case 'P' =>
				case 'B' =>
				case 'I' =>
				case 'S' =>
				case 'A' =>
				case 'R' =>
				case 'G' =>
				case 'N' => set += FilterTestStarting
				case 'C' => set += FilterTestSucceeded
				case 'X' => set += FilterTestIgnored
				case 'E' => set += FilterTestPending
				case 'H' => set += FilterSuiteStarting
				case 'L' => set += FilterSuiteCompleted
				case 'O' => set += FilterInfoProvided
				case 'W' => set += PresentWithoutColor
				case 'F' => set += PresentTestFailedExceptionStackTraces
				case 'D' => set += PresentAllDurations
				case c: Char => {

					// this should be moved to the checker, and just throw an exception here with a debug message. Or allow a MatchError.
					val msg1 = Resources("invalidConfigOption", String.valueOf(c)) + '\n'
					val msg2 =  Resources("probarg", reporterArg) + '\n'

					throw new IllegalArgumentException(msg1 + msg2)
				}
			}
		set
	}

	def parseReporterArgsIntoConfigurations(args: List[String]) = {
		//
		// Checks to see if any args are smaller than two characters in length.
		// Allows a one-character arg if it's a directory-name parameter, to
		// permit use of "." for example.
		//
		def argTooShort(args: List[String]): Boolean = {
			args match {
				case Nil => false

				case "-u" :: directory :: list => argTooShort(list)

				case x :: list =>
					if (x.length < 2) true
					else              argTooShort(list)
			}
		}

		if (args == null)
			throw new NullPointerException("args was null")

		if (args.exists(_ == null))
			throw new NullPointerException("an arg String was null")

		if (argTooShort(args)) // TODO: check and print out a user friendly message for this
			throw new IllegalArgumentException("an arg String was less than 2 in length: " + args)

		for (dashX <- List("-g", "-o", "-e")) {
			if (args.toList.count(_.startsWith(dashX)) > 1) // TODO: also check and print a user friendly message for this
				throw new IllegalArgumentException("Only one " + dashX + " allowed")
		}

		// TODO: also check and print a user friendly message for this
		// again here, i had to skip some things, so I had to use an iterator.
		val it = args.elements
		while (it.hasNext)
			it.next.take(2).toString match {
				case "-g" =>
				case "-o" =>
				case "-e" =>
				case "-f" =>
					if (it.hasNext)
						it.next // scroll past the filename
					else
						throw new IllegalArgumentException("-f needs to be followed by a file name arg: ")
				case "-u" =>
					if (it.hasNext) {
						val directory = it.next
						if (!(new File(directory).isDirectory))
							throw new IllegalArgumentException(
								"arg for -u option is not a directory [" + directory + "]")
						else {}
					}
					else {
						throw new IllegalArgumentException("-u needs to be followed by a directory name arg: ")
					}
				case "-h" =>
					if (it.hasNext)
						it.next // scroll past the filename
					else
						throw new IllegalArgumentException("-h needs to be followed by a file name arg: ")
				case "-r" =>
					if (it.hasNext)
						it.next // scroll past the reporter class
					else
						throw new IllegalArgumentException("-r needs to be followed by a reporter class name arg: ")
				case arg: String =>
					throw new IllegalArgumentException("An arg started with an invalid character string: " + arg)
			}

		val graphicReporterConfigurationOption =
			args.find(arg => arg.startsWith("-g")) match {
				case Some(dashGString) =>
					val configSet = parseConfigSet(dashGString)
					if (configSet.contains(PresentTestFailedExceptionStackTraces))
						throw new IllegalArgumentException("Cannot specify an F (present TestFailedException stack traces) configuration parameter for the graphic reporter (because it shows them anyway): " + dashGString)
					if (configSet.contains(PresentWithoutColor))
						throw new IllegalArgumentException("Cannot specify a W (present without color) configuration parameter for the graphic reporter: " + dashGString)
					if (configSet.contains(PresentAllDurations))
						throw new IllegalArgumentException("Cannot specify a D (present all durations) configuration parameter for the graphic reporter (because it shows them all anyway): " + dashGString)
					Some(new GraphicReporterConfiguration(configSet))
				case None => None
			}

		def buildFileReporterConfigurationList(args: List[String]) = {
			val it = args.elements
			val lb = new ListBuffer[FileReporterConfiguration]
			while (it.hasNext) {
				val arg = it.next
				if (arg.startsWith("-f"))
					lb += new FileReporterConfiguration(parseConfigSet(arg), it.next)
			}
			lb.toList
		}
		val fileReporterConfigurationList = buildFileReporterConfigurationList(args)

		def buildXmlReporterConfigurationList(args: List[String]) = {
			val it = args.elements
			val lb = new ListBuffer[XmlReporterConfiguration]
			while (it.hasNext) {
				val arg = it.next
				if (arg.startsWith("-u"))
					lb += new XmlReporterConfiguration(Set[ReporterConfigParam](),
																						 it.next)
			}
			lb.toList
		}
		val xmlReporterConfigurationList = buildXmlReporterConfigurationList(args)

		def buildHtmlReporterConfigurationList(args: List[String]) = {
			val it = args.elements
			val lb = new ListBuffer[HtmlReporterConfiguration]
			while (it.hasNext) {
				val arg = it.next
				if (arg.startsWith("-h"))
					lb += new HtmlReporterConfiguration(parseConfigSet(arg), it.next)
			}
			lb.toList
		}
		val htmlReporterConfigurationList = buildHtmlReporterConfigurationList(args)

		val standardOutReporterConfigurationOption =
			args.find(arg => arg.startsWith("-o")) match {
				case Some(dashOString) => Some(new StandardOutReporterConfiguration(parseConfigSet(dashOString)))
				case None => None
			}

		val standardErrReporterConfigurationOption =
			args.find(arg => arg.startsWith("-e")) match {
				case Some(dashEString) => Some(new StandardErrReporterConfiguration(parseConfigSet(dashEString)))
				case None => None
			}

		def buildCustomReporterConfigurationList(args: List[String]) = {
			val it = args.elements
			val lb = new ListBuffer[CustomReporterConfiguration]
			while (it.hasNext) {
				val arg = it.next
				if (arg.startsWith("-r")) {
					val dashRString = arg
					val customReporterClassName = it.next
					val configSet = parseConfigSet(dashRString)
					if (configSet.contains(PresentTestFailedExceptionStackTraces))
						throw new IllegalArgumentException("Cannot specify an F (present TestFailedException stack traces) configuration parameter for a custom reporter: " + dashRString + " " + customReporterClassName)
					if (configSet.contains(PresentWithoutColor))
						throw new IllegalArgumentException("Cannot specify a W (without color) configuration parameter for a custom reporter: " + dashRString + " " + customReporterClassName)
					if (configSet.contains(PresentAllDurations))
						throw new IllegalArgumentException("Cannot specify a D (present all durations) configuration parameter for a custom reporter: " + dashRString + " " + customReporterClassName)
					lb += new CustomReporterConfiguration(configSet, customReporterClassName)
				}
			}
			lb.toList
		}
		val customReporterConfigurationList = buildCustomReporterConfigurationList(args)

		// Here instead of one loop, i go through the loop several times.
		new ReporterConfigurations(
			graphicReporterConfigurationOption,
			fileReporterConfigurationList,
			xmlReporterConfigurationList,
			standardOutReporterConfigurationOption,
			standardErrReporterConfigurationOption,
			htmlReporterConfigurationList,
			customReporterConfigurationList
		)
	}

	// Used to parse -s, -j, -m, and -w args, one of which will be passed as a String as dashArg
	def parseSuiteArgsIntoNameStrings(args: List[String], dashArg: String) = {

		if (args == null)
			throw new NullPointerException("args was null")

		if (args.exists(_ == null))
			throw new NullPointerException("an arg String was null")

		if (dashArg != "-j" && dashArg != "-s" && dashArg != "-w" && dashArg != "-m" && dashArg != "-t")
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

	def parseCompoundArgIntoSet(args: List[String], expectedDashArg: String): Set[String] =
			Set() ++ parseCompoundArgIntoList(args, expectedDashArg)

	def parseRunpathArgIntoList(args: List[String]): List[String] = parseCompoundArgIntoList(args, "-p")

	def parseCompoundArgIntoList(args: List[String], expectedDashArg: String): List[String] = {

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

			splitPath(runpathArg)
		}
		else {
			throw new IllegalArgumentException("Runpath must be either zero or two args: " + args)
		}
	}

	//
	// Splits a space-delimited path into its component parts.
	//
	// Spaces within path elements may be escaped with backslashes, e.g.
	// "c:\Documents\ And\ Settings c:\Program\ Files"
	//
	// See comments for isCompleteToken() below for exceptions.
	//
	private val START_TOKEN_PATTERN = Pattern.compile("""^\s*(.*?)(\s|$)""")
	private val FULL_TOKEN_PATTERN  = Pattern.compile("""^\s*(.+?)(((?<=[^\\])\s)|$)""")
	private def splitPath(pathArg: String): List[String] = {
		val path = pathArg.trim

		if (path.isEmpty) Nil
		else {
			val startMatcher = START_TOKEN_PATTERN.matcher(path)

			if (!startMatcher.find())
				throw new RuntimeException("unexpected startMatcher path [" +
																	 path + "]")
			val token = startMatcher.group(1)

			if (isCompleteToken(token)) {
				token :: splitPath(path.substring(startMatcher.end))
			}
			else {
				val fullMatcher = FULL_TOKEN_PATTERN.matcher(path)

				if (!fullMatcher.find())
					throw new RuntimeException("unexpected fullMatcher path [" +
																		 path + "]")
				val fullToken = fullMatcher.group(1).replaceAll("""\\(\s)""", "$1")

				fullToken :: splitPath(path.substring(fullMatcher.end))
			}
		}
	}

	//
	// Determines whether specified token is complete or partial.
	//
	// Tokens are considered partial if they end with a backslash, since
	// backslash is used to escape spaces that would otherwise be
	// treated as delimiters within the path string.
	//
	// Exceptions are cases where the token ends in a backslash
	// but is still considered a complete token because it constitutes
	// a valid representation of a root directory on a windows system,
	// e.g. "c:\" or just "\".
	//
	private val ROOT_DIR_PATTERN = Pattern.compile("""(?i)\\|[a-z]:\\""")
	private def isCompleteToken(token: String): Boolean = {
		val matcher = ROOT_DIR_PATTERN.matcher(token)

		matcher.matches() || (token(token.length - 1) != '\\')
	}

	def parsePropertiesArgsIntoMap(args: List[String]) = {

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

}