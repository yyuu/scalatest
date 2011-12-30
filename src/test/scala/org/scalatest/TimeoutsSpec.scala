package org.scalatest

import matchers.ShouldMatchers
import ValueOnOption._
import Timeouts._
import SharedHelpers.thisLineNumber
import java.io.ByteArrayInputStream
import java.net.SocketException
import java.net.ServerSocket
import java.net.Socket
import org.scalatest.spi.interruptor.SocketInterruptor
import org.scalatest.spi.interruptor.Interruptor
import org.scalatest.spi.interruptor.DoNotInterrupt
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel
import org.scalatest.spi.interruptor.SelectorInterruptor

class TimeoutsSpec extends Spec with ShouldMatchers {

  describe("The failAfter construct") {
    
    it("should blow up with TestFailedException when timeout") {
      val caught = evaluating {
        failAfter(1000) {
          Thread.sleep(2000)
        }
      } should produce [TestFailedException]
      caught.message.value should be (Resources("timeoutFailAfter", "1000"))
      caught.failedCodeLineNumber.value should equal (thisLineNumber - 5)
      caught.failedCodeFileName.value should be ("TimeoutsSpec.scala")
    }
    
    it("should pass normally when timeout is not reached") {
      failAfter(2000) {
        Thread.sleep(1000)
      }
    }
    
    it("should blow up with TestFailedException when the task does not response interrupt request and pass after the timeout") {
      val caught = evaluating {
        failAfter(millis = 1000) {
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
        failAfter(1000) {
          throw new InterruptedException
        }
      } should produce [InterruptedException]
    }
    
    it("should set exception thrown from the test after timeout as cause of TestFailedException") {
      val caught = evaluating {
        failAfter(1000) {
          for (i <- 1 to 10) {
            try {
              Thread.sleep(500)
            }
            catch {
              case _: InterruptedException =>
                Thread.interrupted() // Swallow the interrupt
            }
          }
          throw new IllegalArgumentException("Something goes wrong!")
        }
      } should produce [TestFailedException]
      caught.getCause().getClass === classOf[IllegalArgumentException]
    }
    
    it("should close Socket connection via SocketInterruptor when timeout reached") {
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
        failAfter(1000) {
          inputStream.read()
        } (SocketInterruptor(clientSocket))
      } should produce [TestFailedException]
      clientSocket.close()
      drag = false
    }
    
    it("should close Socket connection via FunInterruptor when timeout reached") {
      val serverSocket = new ServerSocket(19999)
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
      val clientSocket = new Socket("localhost", 19999)
      val inputStream = clientSocket.getInputStream()
      
      val caught = evaluating {
        failAfter(1000) {
          inputStream.read()
        } ( Interruptor { clientSocket.close() } )
      } should produce [TestFailedException]
      clientSocket.close()
      drag = false
    }
  }
  
  it("should wait for the test to finish when DoNotInterrupt is used.") {
    var x = 0
    val caught = evaluating {
      failAfter(1000) {
        Thread.sleep(2000)
        x = 1
      } ( DoNotInterrupt() )
    } should produce [TestFailedException]
    x should be (1)
  }
  
  it("should close Selector connection via SelectorInterruptor when timeout reached") {
    val selector = Selector.open()
    val ssChannel = ServerSocketChannel.open()
    ssChannel.configureBlocking(false)
    ssChannel.socket().bind(new InetSocketAddress(29999))
    ssChannel.register(selector, SelectionKey.OP_ACCEPT)
    @volatile
    var drag = true
    val serverThread = new Thread() {
      override def run() {
        selector.select()
        val it = selector.selectedKeys.iterator
        while (it.hasNext) {
          val selKey = it.next().asInstanceOf[SelectionKey]
          it.remove()
          if (selKey.isAcceptable()) {
            val ssChannel = selKey.channel().asInstanceOf[ServerSocketChannel]
            while(drag) {
              try {
                Thread.sleep(1000)
              }
              catch {
                case _: InterruptedException => Thread.interrupted()
              }
            }
          }
        }
        ssChannel.close()
      }
    }
    
    val clientSelector = Selector.open();
    val sChannel = SocketChannel.open()
    sChannel.configureBlocking(false);
    sChannel.connect(new InetSocketAddress("localhost", 29999));
    sChannel.register(selector, sChannel.validOps());
    
    val caught = evaluating {
      failAfter(1000) {
        clientSelector.select()
      } (SelectorInterruptor(clientSelector))
    } should produce [TestFailedException]
    clientSelector.close()
    drag = false
  }
}