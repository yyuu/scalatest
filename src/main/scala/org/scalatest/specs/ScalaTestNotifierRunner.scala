
package org.scalatest.specs

import org.specs.specification._
import org.specs._
import org.specs.util.LazyParameter
import org.specs.runner.Notifier
import org.specs.runner.NotifierRunner

trait ScalaTestAbstractNotifier extends Notifier {
  def scopeOpened(name: String)
  def scopeClosed(name: String)
}

class ScalaTestNotifierRunner(val specification: Specification, val notifier: ScalaTestAbstractNotifier) 
  extends NotifierRunner(Array(specification), Array[Notifier](notifier)) {
  
  override def reportExample(example: Examples, planOnly: Boolean): this.type = {
    //println("example: " + example.description + ", children: " + example.examples.length)
    if(example.examples.length == 0) {
      notifiers.foreach { _.exampleStarting(example.description) }
    
      if (!planOnly && example.isOk && example.skipped.isEmpty)
        notifiers.foreach { _.exampleSucceeded(example.description) }
      if (!planOnly && !example.failures.isEmpty)
        notifiers.foreach { notifier =>
          example.failures.foreach { failure =>
            notifier.exampleFailed(example.description, failure) 
          }
        }
      if (!planOnly && !example.errors.isEmpty)
        notifiers.foreach { notifier =>
          example.errors.foreach { error =>
            notifier.exampleError(example.description, error) 
          }
        }
      if (!planOnly && !example.skipped.isEmpty)
        notifiers.foreach { notifier =>
          notifier.exampleSkipped(example.description) 
        }
      notifiers.foreach { _.exampleCompleted(example.description) }
    }
    else {
      notifier.scopeOpened(example.description)
      if (!planOnly) {
        example.examples.foreach(e => reportExample(e, planOnly))
      }
      notifier.scopeClosed(example.description)
    }
    this
  }
}

