package window
import java.net.DatagramPacket
import java.util

class SRSenderWindow(val windowSize: Int) extends AbstractPipelinedWindow {

  override def acknowledge(seqNo: Int): util.List[DatagramPacket] = {
    if(seqNo-base > packets.size())
      throw new IllegalArgumentException

    // packet is already acknowledged
    if(isAcked.get(seqNo-base)) {
      // do nothing for now. Could throw exception later.
    }

    isAcked.set(seqNo-base, true)

    val delivered: util.ArrayList[DatagramPacket] = new util.ArrayList[DatagramPacket]()

    while(!isAcked.isEmpty && isAcked.element()) {
      isAcked.pop()
      delivered.add(packets.pop())
      base += 1
    }

    delivered
  }

  override def getWindowSize: Int = windowSize

  override def putPacket(packet: DatagramPacket, seqNo: Int): Unit =
    throw new UnsupportedOperationException("window does not support packet buffering")
}
