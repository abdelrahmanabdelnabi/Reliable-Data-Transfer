package sender

import java.net.{DatagramPacket, DatagramSocket}

import window.Window

trait Sender {

  var window: Window

  def getSocket: DatagramSocket

  def makePacket(data: Array[Byte], seqNo: Int): DatagramPacket

  def startTimer(seqNo: Int): Unit

  def stopTimer(seqNo: Int): Unit

  def setCurrentState(nextState: State): Unit

  def send(data: Array[Byte]): Unit

  def timeout(seqNo: Int)

}
