package receiver
import java.net.{DatagramPacket, DatagramSocket}

import sender.PacketBuilder

class WaitForPacket(context: Receiver) extends State {
  var socket = new DatagramSocket()
  var seqNo = 0

  override def RDTReceive(packet: DatagramPacket): Unit = {
    val ackPacket = PacketBuilder.buildACKPacket(seqNo , 0, packet.getAddress, packet.getPort)
    seqNo = 1-seqNo
    socket.send(ackPacket)
  }
}
