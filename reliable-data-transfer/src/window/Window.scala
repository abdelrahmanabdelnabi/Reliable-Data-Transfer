package window

import java.net.DatagramPacket
import java.util

trait Window {

  def hasSpace: Boolean

  def append(packet: DatagramPacket): Unit

  def acknowledge(seqNo: Int): java.util.List[DatagramPacket]

  def getPacket(seqNo: Int): DatagramPacket

  def getBase: Int

  def getNextSequenceNumber: Int

  def getWindowSize: Int

  def isEmpty: Boolean = getNextSequenceNumber - getBase == 0

}
