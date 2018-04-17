package sender

import java.net.DatagramPacket
class WaitForACKZero(context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    throw new IllegalStateException()
    false
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    val data = datagramPacket.getData
    println("sender received ack: " + data.toString)
    context.setCurrentState(new WaitForSendZero(context))
  }
}
