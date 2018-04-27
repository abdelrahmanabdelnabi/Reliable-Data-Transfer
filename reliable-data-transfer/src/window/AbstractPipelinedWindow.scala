package window

import java.net.DatagramPacket
import java.util

abstract class AbstractPipelinedWindow extends Window {
  protected var base: Int = 1
  protected var nextSequenceNumber: Int = 1
  protected val packets: util.LinkedList[DatagramPacket] = new util.LinkedList[DatagramPacket]()
  protected val isAcked: util.LinkedList[Boolean] = new util.LinkedList[Boolean]()

  override def append(packet: DatagramPacket): Unit = {
    if(hasSpace) {
      packets.add(packet)
      isAcked.add(false)
      nextSequenceNumber += 1
    } else {
      throw new Exception("No space available")
    }
  }

  override def getPacket(seqNo: Int): DatagramPacket = {
    packets.get(seqNo-base)
  }

  override def getBase: Int = base

  override def getNextSequenceNumber: Int = nextSequenceNumber

  override def hasSpace: Boolean = packets.size() < getWindowSize

}
