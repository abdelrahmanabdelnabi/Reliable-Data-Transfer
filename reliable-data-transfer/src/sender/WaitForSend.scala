package sender

import java.net.{DatagramPacket, InetAddress}

class WaitForSend(seqNo: Int, context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    context.setCurrentState(new WaitForACK(seqNo, context))
    context.UDPSocket.send(context.makePacket(data))
    true
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    throw new IllegalStateException()

  }

}
