package receiver.SRReceiver

import java.net.DatagramPacket

import receiver.State
import sender.PacketBuilder

class Wait(context: SRReceiver) extends State {

  override def RDTReceive(packet: DatagramPacket): Unit = {
    val receivedSeqNo = PacketBuilder.extractSeqNo(packet)
    val data = PacketBuilder.extractData(packet)
    val corrupted: Boolean = PacketBuilder.isCorrupted(packet)
    var ackPacket: DatagramPacket = null

    // TODO

  }
}