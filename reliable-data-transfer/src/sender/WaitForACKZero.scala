package sender

import java.net.DatagramPacket
class WaitForACKZero(context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    false
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    context.setCurrentState(new WaitForSendZero(context))
  }
}
