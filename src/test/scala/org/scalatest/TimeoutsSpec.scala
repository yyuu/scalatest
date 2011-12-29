package org.scalatest

import matchers.ShouldMatchers
import ValueOnOption._
import Timeouts._
import SharedHelpers.thisLineNumber
import java.io.ByteArrayInputStream
import java.net.SocketException
import java.net.ServerSocket
import java.net.Socket

class TimeoutsSpec extends Spec with ShouldMatchers {

  describe("The failAfter construct") {
    
    it("should blow up with TestFailedException when timeout") {
      val caught = evaluating {
        failAfter(3000) {
          Thread.sleep(6000)
        }
      } should produce [TestFailedException]
      caught.message.value should be (Resources("timeoutFailAfter", "3000"))
      caught.failedCodeLineNumber.value should equal (thisLineNumber - 5)
      caught.failedCodeFileName.value should be ("TimeoutsSpec.scala")
    }
    
    it("should pass normally when timeout is not reached") {
      failAfter(6000) {
        Thread.sleep(3000)
      }
    }
    
    it("should blow up with TestFailedException when the task does not response interrupt request and pass after the timeout") {
      val caught = evaluating {
        failAfter(milis = 3000) {
          for (i <- 1 to 10) {
            try {
              Thread.sleep(500)
            }
            catch {
              case _: InterruptedException =>
                Thread.interrupted() // Swallow the interrupt
            }
          }
        }
      } should produce [TestFailedException]
    }
    
    it("should not catch exception thrown from the test") {
      val caught = evaluating {
        throw new InterruptedException
      } should produce [InterruptedException]
    }
    
    it("should close Socket connection when timeout reached") {
      val serverSocket = new ServerSocket(9999)
      @volatile
      var drag = true
      val serverThread = new Thread() {
        override def run() {
          val clientSocket = serverSocket.accept()
          while(drag) {
            try {
              Thread.sleep(1000)
            }
            catch {
              case _: InterruptedException => Thread.interrupted()
            }
          }
          serverSocket.close()
        }
      }
      serverThread.start()
      val clientSocket = new Socket("localhost", 9999)
      val inputStream = clientSocket.getInputStream()
      
      val caught = evaluating {
        failAfter(3000, List(clientSocket)) {
          inputStream.read()
        }
      } should produce [TestFailedException]
      clientSocket.close()
      drag = false
    }
  }
}