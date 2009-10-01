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

class TableFor3[A, B, C](heading: (String, String, String), rows: (A, B, C)*) {
  def apply(fun: (A, B, C) => Unit) {
    for ((a, b, c) <- rows) {
      try {
        fun(a, b, c)
      }
      catch {
        case _: UnmetConditionException =>
      }
    }
  }
}

class TableFor4[A, B, C, D](heading: (String, String, String, String), rows: (A, B, C, D)*) {
  def apply(fun: (A, B, C, D) => Unit) {
    for ((a, b, c, d) <- rows) {
      try {
        fun(a, b, c, d)
      }
      catch {
        case _: UnmetConditionException =>
      }
    }
  }
}

object Table {
  def apply[A, B, C](heading: (String, String, String), rows: (A, B, C)*) =
    new TableFor3(heading, rows: _*)
  def apply[A, B, C, D](heading: (String, String, String, String), rows: (A, B, C, D)*) =
    new TableFor4(heading, rows: _*)
}
