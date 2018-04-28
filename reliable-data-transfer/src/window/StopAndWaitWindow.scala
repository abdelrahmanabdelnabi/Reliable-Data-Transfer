package window
import java.net.DatagramPacket
import java.util

class StopAndWaitWindow extends Window {
  var base: Int = 0
  var nextSeqNum: Int = 0
  val window: Int = 1
  var currentPacket: DatagramPacket = _

  override def append(packet: DatagramPacket): Unit = {
    if(hasSpace)
      currentPacket = packet
    else
      throw new IllegalStateException("Window has no space available")
  }

  override def acknowledge(seqNo: Int): util.List[DatagramPacket] = {
    if(seqNo == base) {
      val returnPacket = currentPacket
      currentPacket = null
      base = 1 - base
      nextSeqNum = 1 - nextSeqNum
      val list = new util.ArrayList[DatagramPacket]()
      list.add(returnPacket)
      list
    } else
      throw new IllegalArgumentException("unexpected sequence number")
  }

  override def getPacket(seqNo: Int): DatagramPacket = {
    if(seqNo == base)
      currentPacket
    else
      throw new IllegalArgumentException
  }

  override def getBase: Int = base

  override def getNextSequenceNumber: Int = nextSeqNum

  override def getWindowSize: Int = window

  override def hasSpace: Boolean = currentPacket == null

  /**
    * puts a packet at the specified location.
    *
    * @param packet the packet to be put in the window
    * @param seqNo  the sequence number of the location of the packet.
    *               Must be a valid sequence number.
    */
  override def putPacket(packet: DatagramPacket, seqNo: Int): Unit =
    throw new UnsupportedOperationException("window does not support packet buffering")
}
