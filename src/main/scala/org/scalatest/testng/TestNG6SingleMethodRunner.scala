package org.scalatest.testng

import org.testng.IAnnotationTransformer
import org.testng.annotations.ITestAnnotation
import java.lang.reflect.Method
import java.lang.reflect.Constructor
import org.testng.TestNG

class TestNG6SingleMethodRunner extends SingleMethodRunner {
  
  def run(testName: String, testng: TestNG) {
    class TestNG6Transformer extends IAnnotationTransformer {
      override def transform( annotation: ITestAnnotation, testClass: java.lang.Class[_], testConstructor: Constructor[_], testMethod: Method){
        if (testName.equals(testMethod.getName)) {
          annotation.setGroups(Array("org.scalatest.testng.singlemethodrun.methodname"))  
        }
      }
    }
    testng.setGroups("org.scalatest.testng.singlemethodrun.methodname")
    testng.setAnnotationTransformer(new TestNG6Transformer)
  }

}