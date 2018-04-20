package sender

import java.net.{DatagramPacket, DatagramSocket}

trait Sender {
  var base: Int = 0
  var nextSequenceNumber: Int = 0
  var windowSize = 1

  def getSocket: DatagramSocket

  def getPacket(seqNo: Int): DatagramPacket

  def makePacket(data: Array[Byte], seqNo: Int): DatagramPacket

  def startTimer(seqNo: Int): Unit

  def stopTimer(seqNo: Int): Unit

  def setCurrentState(nextState: State): Unit

  def send(data: Array[Byte]): Unit

  def timeout(seqNo: Int)

}
