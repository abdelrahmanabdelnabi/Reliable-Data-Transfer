package sender

import java.net.DatagramPacket
class WaitForACK(seqNo: Int, context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {
    println("packet " + seqNo + " timed out. resending")
    context.UDPSocket.send(context.makePacket(context.currentData, seqNo))
    context.setCurrentState(new WaitForACK(seqNo, context))
    context.timer.start(seqNo)
  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    throw new IllegalStateException()
    false
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {

    // if packet is not corrupted and with correct sequence number
    if(PacketBuilder.extractSeqNo(datagramPacket) == seqNo) {
      val receivedSeqNo = PacketBuilder.extractSeqNo(datagramPacket)
      context.timer.cancel(seqNo)
      println("received ACK: " + receivedSeqNo)
      context.setCurrentState(new WaitForSend(1-seqNo, context))
    } else {
      println("received a delayed ACK")
    }
  }
}
