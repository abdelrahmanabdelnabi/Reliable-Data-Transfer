package window

import java.net.DatagramPacket
import java.util

class GBNSenderWindow(windowLength: Int) extends Window {
  private var base: Int = 1
  private var nextSequenceNumber: Int = 1
  private val windowSize: Int = windowLength


  override def append(packet: DatagramPacket): Unit = {
    if(hasSpace) {
      packets.add(packet)
      isAcked.add(false)
      nextSequenceNumber += 1
    } else {
      throw new Exception("No space available")
    }
  }

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

  override def getPacket(seqNo: Int): DatagramPacket = {
    packets.get(seqNo-base)
  }

  override def getBase: Int = base

  override def getNextSequenceNumber: Int = nextSequenceNumber

  override def getWindowSize: Int = windowSize
}
