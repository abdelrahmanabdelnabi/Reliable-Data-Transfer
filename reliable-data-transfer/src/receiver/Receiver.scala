package receiver

import java.net.{DatagramPacket, DatagramSocket}

class Receiver(val port: Int) extends Thread{

  var socket = new DatagramSocket(port)
  var currentState: State = new WaitForZero(this)

  override def run(): Unit = {
    receive()
  }

  def receive(): Unit = {

    while(true) {
      val buf = new Array[Byte](256)
      val rec_packet = new DatagramPacket(buf, buf.length)
      socket.receive(rec_packet)
      var data: String = new String(rec_packet.getData)
      data = data.substring(0, data.indexOf(0))
      currentState.RDTReceive(rec_packet)
      println("received " + data + " from port " + rec_packet.getPort)
    }
  }
}
