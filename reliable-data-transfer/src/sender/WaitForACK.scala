package sender

import java.net.DatagramPacket
class WaitForACK(seqNo: Int, context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {
    println("packet " + seqNo + " timed out. resending")
    context.UDPSocket.send(context.makePacket(context.currentData, seqNo))
    context.timer.start(seqNo)
  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    throw new IllegalStateException("Can not send when waiting for ACK")
    false
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {

    // if packet is not corrupted and with correct sequence number
    val corrupted = PacketBuilder.isCorrupted(datagramPacket)
    if(PacketBuilder.extractSeqNo(datagramPacket) == seqNo && !corrupted) {
      val receivedSeqNo = PacketBuilder.extractSeqNo(datagramPacket)
      context.timer.cancel(seqNo)
      println("received ACK: " + receivedSeqNo)
      context.setCurrentState(new WaitForSend(1-seqNo, context))
    } else if(corrupted) {
      println("received a corrupted ACK")
    } else {
      println("received a delayed or duplicate ACK")
    }
  }
}
