package window

import java.net.DatagramPacket

trait Window {

  def hasSpace: Boolean

  /**
    * puts a packet at the first empty location.
    * @param packet
    */
  def append(packet: DatagramPacket): Unit = {
    putPacket(packet, getNextSequenceNumber)
  }

  /**
    * Marks the packet with the specified sequence number as acknowledge.
    * Automatically updates the value of Base.
    * @param seqNo the sequence number of the packet to be acknowledged
    * @return The packets that are now outside the window if any.
    */
  def acknowledge(seqNo: Int): java.util.List[DatagramPacket]

  /**
    * Returns the packet with the specified sequence number.
    * @param seqNo sequence number of the desired packet. Must be within the window
    *              and exist in it.
    * @return the packet.
    */
  def getPacket(seqNo: Int): DatagramPacket

  /**
    * puts a packet at the specified location.
    * @param packet the packet to be put in the window
    * @param seqNo the sequence number of the location of the packet.
    *              Must be a valid sequence number.
    */
  def putPacket(packet: DatagramPacket, seqNo: Int): Unit

  def getBase: Int

  def getNextSequenceNumber: Int

  def getWindowSize: Int

  def isEmpty: Boolean = getNextSequenceNumber - getBase == 0

}
