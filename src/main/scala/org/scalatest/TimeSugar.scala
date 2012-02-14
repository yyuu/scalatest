/*
 * Copyright 2001-2009 Artima, Inc.
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
package org.scalatest

/**
 * Trait providing two implicit conversions that allow you to specify <code>Long</code> durations of time
 * with units such as <code>millis</code>, <code>seconds</code>, and <code>minutes</code>.
 */
trait TimeSugar {

  // Not calling this Duration because everyone else does, so avoids name clash
  /**
   * Class containing methods that return a <code>Long</code> time value calculated from the
   * value passed to the <code>GrainOfTime</code> constructor.
   * 
   * @param value the value to be converted
   */
  class GrainOfTime(value: Long) {
    def millisecond: Long = value
    def milliseconds: Long = value
    def millis: Long = value
    def second: Long = value * 1000    
    def seconds: Long = value * 1000    
    def minute: Long = value * 1000 * 60   
    def minutes: Long = value * 1000 * 60
    def hour: Long = value * 1000 * 60 * 60  
    def hours: Long = value * 1000 * 60 * 60
    def day: Long = value * 1000 * 60 * 60 * 24 
    def days: Long = value * 1000 * 60 * 60 * 24
  }
  
  implicit def convertIntToGrainOfTime(i: Int) = new GrainOfTime(i)
  implicit def convertLongToGrainOfTime(i: Long) = new GrainOfTime(i)
}

/**
 * Companion object that facilitates the importing of <code>TimeSugar</code> members as 
 * an alternative to mixing it in. One use case is to import <code>TimeSugar</code> members so you can use
 * them in the Scala interpreter:
 *
 * <pre class="stREPL">
 * $scala -classpath scalatest.jar
 * Welcome to Scala version 2.9.1.final (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_29).
 * Type in expressions to have them evaluated.
 * Type :help for more information.
 *
 * scala&gt; import org.scalatest.TimeSugar._
 * import org.scalatest.TimeSugar._
 *
 * scala&gt; Thread.sleep(2 seconds)
 * </pre>
 */
object TimeSugar extends TimeSugar
