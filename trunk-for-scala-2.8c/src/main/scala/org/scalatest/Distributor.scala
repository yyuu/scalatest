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
package org.scalatest

/**
 * Trait whose instances facilitate parallel execution of <code>Suite</code>s.
 * An optional <code>Distributor</code> is passed to the <code>run</code> method of <code>Suite</code>. If a
 * <code>Distributor</code> is indeed passed, trait <code>Suite</code>'s implementation of <code>run</code> will
 * populate that <code>Distributor</code> with its nested <code>Suite</code>s (by passing them to the <code>Distributor</code>'s
 * <code>apply</code> method) rather than executing the nested <code>Suite</code>s directly. It is then up to another thread or process
 * to execute those <code>Suite</code>s.
 *
 * <p>
 * If you have a set of nested <code>Suite</code>s that must be executed sequentially, you can mix in trait
 * <code>SequentialNestedSuiteExecution</code>, which overrides <code>runNestedSuites</code> and
 * calls <code>super</code>'s <code>runNestedSuites</code> implementation, passing in <code>None</code> for the
 * <code>Distributor</code>.
 * </p>
 * 
 * <p>
 * Implementations of this trait must be thread safe.
 * </p>
 *
 * @author Bill Venners
 */
trait Distributor extends ((Suite, Tracker) => Unit) {

  /**
   * Puts a <code>Suite</code> into the <code>Distributor</code>.
   *
   * @param suite the <code>Suite</code> to put into the <code>Distributor</code>.
   * @param tracker a <code>Tracker</code> to pass to the <code>Suite</code>'s <code>run</code> method.
   *
   * @throws NullPointerException if either <code>suite</code> or <code>tracker</code> is <code>null</code>.
   */
  override def apply(suite: Suite, tracker: Tracker)
}