package receiver.GBNReceiver

import java.net.DatagramPacket

import receiver.{Receiver, State}
import sender.PacketBuilder

class Wait(context: Receiver) extends State {

  override def RDTReceive(packet: DatagramPacket): Unit = {

    val receivedSeqNo = PacketBuilder.extractSeqNo(packet)
    val data = PacketBuilder.extractData(packet)
    val corrupted: Boolean = PacketBuilder.isCorrupted(packet)
    var ackPacket: DatagramPacket = null

    if(!corrupted && receivedSeqNo == context.expectedSeqNo) {
      ackPacket = PacketBuilder.buildACKPacket(context.expectedSeqNo, packet.getAddress, packet.getPort)
      context.expectedSeqNo += 1
      println(new String(data))
    } else {

      if(corrupted)
        println("received a corrupted packet")
      else
        println("received duplicate or out of order packet")

      ackPacket = PacketBuilder.buildACKPacket(context.expectedSeqNo - 1, packet.getAddress, packet.getPort)
    }

    context.getSocket.send(ackPacket)
  }

}
