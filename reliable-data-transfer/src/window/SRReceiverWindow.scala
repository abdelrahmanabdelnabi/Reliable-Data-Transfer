package window
import java.net.DatagramPacket
import java.util

class SRReceiverWindow(val windowSize: Int) extends AbstractPipelinedWindow {
  // because window supports buffering, we have to initialize the linked list with
  // empty locations
  for(i <- 0 until windowSize){
    packets.add(null)
    isAcked.add(null)
  }

  override def acknowledge(seqNo: Int): util.List[DatagramPacket] = {
    // check valid sequence number
    // check if packet exists in window
    if(seqNo-base > getWindowSize)
      throw new IllegalArgumentException

    if(packets.get(seqNo-base) == null)
      throw new IllegalArgumentException("No packet with sequence number " + seqNo +
        "exists in the window")

    // TODO: Ignore re-acknowledgments?!

    isAcked.set(seqNo-base, true)

    val delivered: util.ArrayList[DatagramPacket] = new util.ArrayList[DatagramPacket]()

    while(isAcked.element()) {
      isAcked.pop()
      delivered.add(packets.pop())
      base += 1

      // maintain the size of the window
      packets.add(null)
      isAcked.add(null)
    }
  }


  override def putPacket(packet: DatagramPacket, seqNo: Int): Unit = {
    if(seqNo-base > getWindowSize || seqNo < base)
      throw new IllegalArgumentException

    packets.set(seqNo-base, packet)
  }
  override def getWindowSize: Int = windowSize

  override def hasSpace: Boolean = true
}
