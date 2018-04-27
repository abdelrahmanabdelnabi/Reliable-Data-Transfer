package window

import java.net.DatagramPacket
import java.util

class GBNSenderWindow(val windowSize: Int) extends AbstractPipelinedWindow {

  override def acknowledge(seqNo: Int): java.util.List[DatagramPacket] = {
    if(seqNo-base > packets.size())
      throw new IllegalArgumentException

    for(i <- base to seqNo) {
      isAcked.set(i-base, true)
    }

    val delivered: util.ArrayList[DatagramPacket] = new util.ArrayList[DatagramPacket]()

    while(!isAcked.isEmpty && isAcked.element()) {
      isAcked.pop()
      delivered.add(packets.pop())
      base += 1
    }

    delivered
  }

  override def getWindowSize: Int = windowSize
}
