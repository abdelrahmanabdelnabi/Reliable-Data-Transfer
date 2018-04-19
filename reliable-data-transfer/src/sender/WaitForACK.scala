package sender

import java.net.DatagramPacket
class WaitForACK(seqNo: Int, context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    throw new IllegalStateException()
    false
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    val receviedSeqNo = PacketBuilder.extractSeqNo(datagramPacket)
    println("sender received ack: " + receviedSeqNo)
    context.setCurrentState(new WaitForSend(1-seqNo, context))
  }
}
