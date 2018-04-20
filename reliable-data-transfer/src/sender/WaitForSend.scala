package sender

import java.net.{DatagramPacket, InetAddress}

class WaitForSend(seqNo: Int, context: Sender) extends State {
  override def timeout(seqNo: Int): Unit = {
    throw new IllegalStateException("received timeout signal but was not expecting an acknowledgment")

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    context.setCurrentState(new WaitForACK(seqNo, context))
    context.UDPSocket.send(context.makePacket(data, seqNo))
    context.timer.start(seqNo)
    true
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    // according to rdt 3.0, if the sender receives a packet and he
    // is not expecting one, he should just ignore it
  }

}