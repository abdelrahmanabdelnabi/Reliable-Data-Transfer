package receiver
import java.net.{DatagramPacket, DatagramSocket}

import sender.PacketBuilder

import scala.util.Random

class WaitForPacket(context: Receiver) extends State {
  var socket = new DatagramSocket()
  var seqNo = 0

  override def RDTReceive(packet: DatagramPacket): Unit = {
    val receivedSeqNo = PacketBuilder.extractSeqNo(packet)
    val data = PacketBuilder.extractData(packet)
    val corrupted: Boolean = false

    if (receivedSeqNo == seqNo && !corrupted) {
      val ackPacket = PacketBuilder.buildACKPacket(seqNo , 0, packet.getAddress, packet.getPort)
      seqNo = 1-seqNo
      println(new String(data))

      if(Random.nextInt(4) == 1)
        Thread.sleep(10)
      socket.send(ackPacket)
    } else {
      val ackPacket = PacketBuilder.buildACKPacket(1-seqNo , 0, packet.getAddress, packet.getPort)
      socket.send(ackPacket)
      println("received duplicate packet")
    }

  }
}
