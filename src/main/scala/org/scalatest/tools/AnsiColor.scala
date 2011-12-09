package org.scalatest.tools

trait AnsiColor {
  // Good reference at http://code.google.com/p/jlibs/wiki/AnsiColoring
  final val ansiReset = "\033[0m"
  val ansiGreen: String
  val ansiCyan: String
  val ansiYellow: String
  val ansiRed: String
}

object NormalAnsiColor extends AnsiColor {
  final val ansiGreen = "\033[0;32m"
  final val ansiCyan = "\033[0;36m"
  final val ansiYellow = "\033[0;33m"
  final val ansiRed = "\033[0;31m"
}

object DarkAnsiColor extends AnsiColor {
  final val ansiGreen = "\033[2;32m"
  final val ansiCyan = "\033[2;36m"
  final val ansiYellow = "\033[2;33m"
  final val ansiRed = "\033[2;31m"
}

// Not used currently
/*object BrightAnsiColor extends AnsiColor {
  final val ansiGreen = "\033[1;32m"
  final val ansiCyan = "\033[1;36m"
  final val ansiYellow = "\033[1;33m"
  final val ansiRed = "\033[1;31m"
}*/