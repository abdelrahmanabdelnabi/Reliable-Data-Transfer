import java.net.DatagramPacket

class WaitForSendZero(context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    context.UDPSocket.send(context.makePacket(data))
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {

  }

}
