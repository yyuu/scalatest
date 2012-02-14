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

trait TimeSugar {

  // Not calling this Duration because everyone else does, so avoids name clash
  class GrainOfTime(left: Long) {
    def millisecond: Long = left
    def milliseconds: Long = left
    def millis: Long = left
    def second: Long = left * 1000    
    def seconds: Long = left * 1000    
    def minute: Long = left * 1000 * 60   
    def minutes: Long = left * 1000 * 60
    def hour: Long = left * 1000 * 60 * 60  
    def hours: Long = left * 1000 * 60 * 60
    def day: Long = left * 1000 * 60 * 60 * 24 
    def days: Long = left * 1000 * 60 * 60 * 24
  }
  
  implicit def convertIntToGrainOfTime(i: Int) = new GrainOfTime(i)
  implicit def convertLongToGrainOfTime(i: Long) = new GrainOfTime(i)
}

object TimeSugar extends TimeSugar