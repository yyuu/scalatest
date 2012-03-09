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
package org.scalatest.time

/**
 * Trait providing two implicit conversions that allow you to specify <code>Long</code> spans of time
 * with units such as <code>millis</code>, <code>seconds</code>, and <code>minutes</code>.
 * 
 * <p>
 * This trait enables you to specify units of time when you need a <code>Long</code> number of milliseconds. This
 * can be used, for example, with the <code>failAfter</code> method of trait <code>Timeouts</code> or the
 * <code>timeLimit</code> field of trait <code>TimeLimitedTests</code>. Here are examples of each unit enabled
 * by this trait: 
 * </p>
 * 
 * <pre>
 * Thread.sleep(1 millisecond) // TODO: Need new examples
 * Thread.sleep(2 milliseconds)
 * Thread.sleep(2 millis)
 * Thread.sleep(1 second)
 * Thread.sleep(2 seconds)
 * Thread.sleep(1 minute)
 * Thread.sleep(2 minutes)
 * Thread.sleep(1 hour)
 * Thread.sleep(2 hours)
 * Thread.sleep(1 day)
 * Thread.sleep(2 days) // A nice nap indeed
 * </pre>
 * 
 * <p>
 * Because the result of these expressions is simply a <code>Long</code> number of milliseconds, you can also 
 * make arithmetic expressions out of them (so long as you use needed parentheses). For example:
 * </p>
 * 
 * <pre>
 * scala&gt; import org.scalatest.SpanSugar._
 * import org.scalatest.SpanSugar._
 *
 * scala&gt; (1 second) + 88 milliseconds
 * res0: Long = 1088
 * </pre>
 */
trait SpanSugar {

  // Not calling this Duration because everyone else does, so avoids name clash
  /**
   * Class containing methods that return a <code>Span</code> time value calculated from the
   * value passed to the <code>GrainOfTime</code> constructor.
   * 
   * @param value the value to be converted
   */
  class GrainOfTime(value: Long) {
    
    /**
     * A units method for one millisecond. 
     * 
     * @return the value passed to the constructor
     */
    def millisecond: Span = Span(value, Millisecond) // TODO: Also enforce that 1 thing here probably
    
    /**
     * A units method for milliseconds. 
     * 
     * @return the value passed to the constructor
     */
    def milliseconds: Span = Span(value, Milliseconds)

    /**
     * A shorter units method for milliseconds. 
     * 
     * @return the value passed to the constructor
     */
    def millis: Span = Span(value, Millis)

    /**
     * A units method for one second. 
     * 
     * @return the value passed to the constructor multiplied by 1000
     */
    def second: Span = Span(value, Second) 
    
    /**
     * A units method for seconds. 
     * 
     * @return the value passed to the constructor multiplied by 1000
     */
    def seconds: Span = Span(value, Seconds)

    /**
     * A units method for one minute. 
     * 
     * @return the value passed to the constructor multiplied by 1000 * 60
     */
    def minute: Span = Span(value, Minute)

    /**
     * A units method for minutes. 
     * 
     * @return the value passed to the constructor multiplied by 1000 * 60
     */
    def minutes: Span = Span(value, Minutes)
    
    /**
     * A units method for one hour. 
     * 
     * @return the value passed to the constructor multiplied by 1000 * 60 * 60
     */
    def hour: Span = Span(value, Hour)

    /**
     * A units method for hours. 
     * 
     * @return the value passed to the constructor multiplied by 1000 * 60 * 60
     */
    def hours: Span = Span(value, Hours)
    
    /**
     * A units method for one day. 
     * 
     * @return the value passed to the constructor multiplied by 1000 * 60 * 60 * 24
     */
    def day: Span = Span(value, Day)

    /**
     * A units method for days. 
     * 
     * @return the value passed to the constructor multiplied by 1000 * 60 * 60 * 24
     */
    def days: Span = Span(value, Days)
  }
  
  /**
   * Implicit conversion that adds time units methods to <code>Int</code>s.
   * 
   * @param i: the <code>Int</code> to which to add time units methods
   * @return a <code>GrainOfTime</code> wrapping the passed <code>Int</code>
   */
  implicit def convertIntToGrainOfTime(i: Int) = new GrainOfTime(i)
  
  /**
   * Implicit conversion that adds time units methods to <code>Long</code>s.
   * 
   * @param i: the <code>Long</code> to which to add time units methods
   * @return a <code>GrainOfTime</code> wrapping the passed <code>Long</code>
   */
  implicit def convertLongToGrainOfTime(i: Long) = new GrainOfTime(i) // TODO: Will need these for Double as well
// TODO: And write some tests with Float literals.
}

/**
 * Companion object that facilitates the importing of <code>SpanSugar</code> members as 
 * an alternative to mixing it in. One use case is to import <code>SpanSugar</code> members so you can use
 * them in the Scala interpreter:
 *
 * <pre class="stREPL">
 * $scala -classpath scalatest.jar
 * Welcome to Scala version 2.9.1.final (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_29).
 * Type in expressions to have them evaluated.
 * Type :help for more information.
 *
 * scala&gt; import org.scalatest.SpanSugar._
 * import org.scalatest.SpanSugar._
 *
 * scala&gt; Thread.sleep(2 seconds) // TODO: Need a new example
 * </pre>
 */
object SpanSugar extends SpanSugar
