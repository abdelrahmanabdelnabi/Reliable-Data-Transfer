import java.net.DatagramPacket

trait State {
  def timeout(seqNo: Int): Unit

  def RDTSend(data: Array[Byte]): Boolean

  def RDTReceive(datagramPacket: DatagramPacket): Unit

}
