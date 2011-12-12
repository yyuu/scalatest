/*
 * Copyright 2001-2011 Artima, Inc.
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

// CCS: I made these private[scalatest]. Else they show up in the public API.
// Also, I went ahead and made this an abstract class instead of a trait, since it
// is only used here. And I made it sealed as well. Not a big deal, but both of those
// communicate that this is only used here.
private[scalatest] sealed abstract class AnsiColor {
  // Good reference at http://code.google.com/p/jlibs/wiki/AnsiColoring
  final val ansiReset = "\033[0m"
  val ansiGreen: String
  val ansiCyan: String
  val ansiYellow: String
  val ansiRed: String
}

private[scalatest] object NormalAnsiColor extends AnsiColor {
  final val ansiGreen = "\033[0;32m"
  final val ansiCyan = "\033[0;36m"
  final val ansiYellow = "\033[0;33m"
  final val ansiRed = "\033[0;31m"
}

private[scalatest] object DarkAnsiColor extends AnsiColor {
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
