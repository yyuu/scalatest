package org.scalatest.spi.interruptor

import java.net.Socket

class SocketInterruptor(socket: Socket) extends Interruptor {

  def interrupt(testThread: Thread) {
    socket.close()
  }
  
}

object SocketInterruptor {
  def apply(socket: Socket) = new SocketInterruptor(socket)
}