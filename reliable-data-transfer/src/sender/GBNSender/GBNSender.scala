package sender.GBNSender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender._
import sockets.UnreliableSocket
import window.{GBNSenderWindow, Window}

class GBNSender(address: InetAddress, port: Int, windowSize: Int, socket: DatagramSocket) extends Sender {

  val lock: Object = new Object
  var window: Window = new GBNSenderWindow(windowSize)
  var currentState: State = new Wait(this)
  var UDPSocket: DatagramSocket = socket
  val timer: MyTimer = new MyTimer(this, 30)

  val notifier = new ACKNotifier(UDPSocket, this)
  notifier.start()

  def this(address: InetAddress, port: Int, windowSize: Int) = {
    this(address, port, windowSize, new UnreliableSocket(0, 0))
  }

  override  def send(data: Array[Byte]): Unit = {
    lock.synchronized {
      if(window.hasSpace) {
        window.append(makePacket(data, window.getNextSequenceNumber))
        currentState.RDTSend(data)
      } else {
        println("Window has no space")
      }

    }
  }

  override def receive(packet: DatagramPacket): Unit = {
    lock.synchronized {
      currentState.RDTReceive(packet)
    }
  }

  def timeout(seqNo: Int): Unit = {
    lock.synchronized{
      currentState.timeout(seqNo)
    }
  }

  def setCurrentState(nextState: State): Unit = {
    lock.synchronized{
      currentState = nextState
    }
  }

  def makePacket(data: Array[Byte], seqNo: Int): DatagramPacket = {
    val packet = PacketBuilder.buildDataPacket(data, seqNo)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  def isAvailable: Boolean = {
    lock.synchronized{
      if(window.hasSpace)
        true
      else
        false
    }
  }

  override def getSocket: DatagramSocket = {
    UDPSocket
  }

  override def startTimer(seqNo: Int): Unit = {
    timer.start(seqNo)
  }

  override def stopTimer(seqNo: Int): Unit = {
    timer.cancel(seqNo)
  }
}
