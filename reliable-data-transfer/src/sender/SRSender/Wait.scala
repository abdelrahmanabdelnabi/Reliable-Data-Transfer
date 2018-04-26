package sender.SRSender

import java.net.DatagramPacket

import sender.State

class Wait extends State {

  override def timeout(seqNo: Int): Unit = {
    // TODO: Implement me
  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    // TODO: Implement me
    true
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    // TODO: Implement me
  }
}
