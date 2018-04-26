package sender

import java.net.{DatagramPacket, DatagramSocket}

class ACKNotifier(socket: DatagramSocket, listener: Sender) extends Thread {

  override def run(): Unit = {
    println("listening for ACKs")
    while(true) {
      val buf = new Array[Byte](256)
      val rec_packet = new DatagramPacket(buf, buf.length)
      socket.receive(rec_packet)
      listener.receive(rec_packet)
    }
  }

}
