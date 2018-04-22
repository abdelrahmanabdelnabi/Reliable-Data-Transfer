package window

import java.net.DatagramPacket
import java.util

trait Window {

  val packets: java.util.LinkedList[DatagramPacket] = new util.LinkedList[DatagramPacket]()
  val isAcked: java.util.LinkedList[Boolean] = new util.LinkedList[Boolean]()

  def hasSpace: Boolean = {
    getWindowSize - packets.size > 0
  }

  def append(packet: DatagramPacket): Unit

  def acknowledge(seqNo: Int): java.util.List[DatagramPacket]

  def getPacket(seqNo: Int): DatagramPacket

  def getBase: Int

  def getNextSequenceNumber: Int

  def getWindowSize: Int

  def isEmpty: Boolean = getNextSequenceNumber - getBase == 0

}
