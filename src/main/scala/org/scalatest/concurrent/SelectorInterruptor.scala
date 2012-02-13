/*
 * Copyright 2001-2012 Artima, Inc.
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
package org.scalatest.concurrent
import java.nio.channels.Selector

private[scalatest] class SelectorInterruptor(selector: Selector) extends Interruptor {
  
  def interrupt(testThread: Thread) {
    selector.wakeup()
  }
  
}

object SelectorInterruptor {
  def apply(selector: Selector) = new SelectorInterruptor(selector)
}
