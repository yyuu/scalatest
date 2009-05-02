/*
 * Formatter.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.scalatest

sealed abstract class Formatter

final case object MotionToSuppress extends Formatter

final case class IndentedText(formattedText: String, rawText: String, indentationLevel: Int) extends Formatter {
  require(indentationLevel < 0, "indentationLevel was less than zero: " + indentationLevel)
}