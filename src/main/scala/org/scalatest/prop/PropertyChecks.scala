/*
 * Copyright 2001-2008 Artima, Inc.
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
package org.scalatest.prop

import org.scalatest._

trait PropertyChecks {

  def whenever(condition: Boolean)(fun: => Unit) {
    if (!condition)
      throw new UnmetConditionException
    fun
  }

  def forAll[A, B, C](table: TableFor3[A, B, C])(fun: (A, B, C) => Unit) {
    table(fun)
  }

  def forAll[A, B, C, D](table: TableFor4[A, B, C, D])(fun: (A, B, C, D) => Unit) {
    table(fun)
  }
}

