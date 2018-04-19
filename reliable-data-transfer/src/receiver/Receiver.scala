package receiver

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender.PacketBuilder

class Receiver(var address: InetAddress, var port: Int) extends Thread{

  var socket = new DatagramSocket()
  var currentState: State = new WaitForPacket(this)
  var expectedSeqNo = 0

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

    val ack = "ACK".getBytes()

    val ackPacket = PacketBuilder.buildACKPacket(0, 0, address, port)
    socket.send(ackPacket)

    // now connection is setup

    receive()
  }

  def receive(): Unit = {

    while(true) {
      val buf = new Array[Byte](256)
      val rec_packet = new DatagramPacket(buf, buf.length)
      socket.receive(rec_packet)
      var data: String = new String(rec_packet.getData)
      data = data.substring(0, data.indexOf(0))
      println("received " + data + " from port " + rec_packet.getPort)
      currentState.RDTReceive(rec_packet)
    }
  }
}
