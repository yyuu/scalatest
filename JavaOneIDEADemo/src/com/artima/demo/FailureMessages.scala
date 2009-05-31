package com.artima.demo
/**
 * Created by IntelliJ IDEA.
 * User: bv
 * Date: Dec 8, 2008
 * Time: 4:40:44 PM
 * To change this template use File | Settings | File Templates.
 */

object FailureMessages {

  private def prettifyToStringValue(o: Any): String = 
    o match {
      case null => "null"
      case aUnit: Unit => "<(), the Unit value>"
      case aString: String => "\"" + aString + "\""
      case aChar: Char =>  "\'" + aChar + "\'"
      case anythingElse => anythingElse.toString
    }
}