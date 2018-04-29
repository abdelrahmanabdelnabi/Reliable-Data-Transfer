package receiver.SRReceiver

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import receiver.{Receiver, State}
import sender.PacketBuilder
import window.{SRReceiverWindow, Window}

class SRReceiver(var address: InetAddress, var port: Int, val windowSize: Int)
  extends Thread with Receiver {
  var socket = new DatagramSocket()
  var currentState: State = new Wait(this)
  val window: Window = new SRReceiverWindow(windowSize)

  override def run(): Unit = {
    // TODO: handle timeout and corruption in connection setup

    // setup connection
    var buf = "file.txt".getBytes()
    val sendPacket = new DatagramPacket(buf, buf.length, address, port)
    socket.send(sendPacket)

    buf = new Array[Byte](256)
    val recPacket = new DatagramPacket(buf, buf.length)
    socket.receive(recPacket)

    address = recPacket.getAddress
    port = recPacket.getPort

    val ackPacket = PacketBuilder.buildACKPacket(0, address, port)
    socket.send(ackPacket)

    // now connection is setup

    receive()
  }

  def receive(): Unit = {

    while(true) {
      val buf = new Array[Byte](1040)
      val rec_packet = new DatagramPacket(buf, buf.length)
      socket.receive(rec_packet)
      currentState.RDTReceive(rec_packet)
    }
  }

  override def getSocket: DatagramSocket = socket

  override def setCurrentState(nextState: State): Unit = currentState = nextState


}
