package sender.SRSender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender._
import sockets.UnreliableSocket
import window.{SRSenderWindow, Window}

class SRSender(address: InetAddress, port: Int, windowSize: Int, socket: DatagramSocket) extends Sender {

  var window: Window = new SRSenderWindow(windowSize)
  var currentState: State = _
  val timer: MyTimer = new MyTimer(this, 30)
  val lock: Object = new Object
  val notifier:  ACKNotifier = new ACKNotifier(socket, this)
  notifier.start()

  def this(address: InetAddress, port: Int, windowSize: Int) = {
    this(address, port, windowSize, new UnreliableSocket(0, 0))
  }

  override def send(data: Array[Byte]): Unit = {
    lock.synchronized {
      if(window.hasSpace){
        window.append(makePacket(data, window.getNextSequenceNumber))
        currentState.RDTSend(data)
      } else{
        println("Window has no free space")
      }
    }
  }

  override def timeout(seqNo: Int): Unit = {
    lock.synchronized {
      currentState.timeout(seqNo)
    }
  }

  override def receive(packet: DatagramPacket): Unit = {
    lock.synchronized {
      currentState.RDTReceive(packet)
    }
  }

  def makePacket(data: Array[Byte], seqNo: Int): DatagramPacket = {
    val packet = PacketBuilder.buildDataPacket(data, seqNo)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  override def getSocket: DatagramSocket = socket

  override def startTimer(seqNo: Int): Unit = timer.start(seqNo)

  override def stopTimer(seqNo: Int): Unit = timer.cancel(seqNo)

  override def setCurrentState(nextState: State): Unit = currentState = nextState
}
