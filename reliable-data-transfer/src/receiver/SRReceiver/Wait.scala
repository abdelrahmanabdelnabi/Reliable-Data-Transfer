package receiver.SRReceiver

import java.net.DatagramPacket

import receiver.State
import sender.PacketBuilder

class Wait(context: SRReceiver) extends State {

  override def RDTReceive(packet: DatagramPacket): Unit = {
    val receivedSeqNo = PacketBuilder.extractSeqNo(packet)
    val corrupted: Boolean = PacketBuilder.isCorrupted(packet)
    var ackPacket: DatagramPacket = null
    val window = context.window

    if(corrupted) {
      println("received a corrupted packet")
      return // ignore packet
    }

    if(receivedSeqNo >= window.getBase &&
      receivedSeqNo <= window.getBase + window.getWindowSize - 1) {
      ackPacket = PacketBuilder.buildACKPacket(receivedSeqNo, packet.getAddress, packet.getPort)
      context.getSocket.send(ackPacket)

      window.putPacket(packet, receivedSeqNo)
      val delivered = window.acknowledge(receivedSeqNo)

      delivered.forEach(packet => println(new String(PacketBuilder.extractData(packet))))
      return
    }

    if((receivedSeqNo >= (window.getBase - window.getWindowSize)) &&
      (receivedSeqNo <= (window.getBase - 1)) ) {
      ackPacket = PacketBuilder.buildACKPacket(receivedSeqNo, packet.getAddress, packet.getPort)
      context.getSocket.send(ackPacket)
      return
    }

    println("ignoring packet " + receivedSeqNo + " Base: " + window.getBase)

    // otherwise, ignore packet

  }
}