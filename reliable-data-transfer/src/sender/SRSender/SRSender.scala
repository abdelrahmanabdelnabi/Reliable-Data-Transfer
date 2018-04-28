package sender.SRSender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender.{ACKNotifier, MyTimer, Sender, State}
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
      // TODO: Implement me
    }
  }

  override def timeout(seqNo: Int): Unit = {
    lock.synchronized {
      // TODO: Implement me
    }
  }

  override def receive(packet: DatagramPacket): Unit = {
    lock.synchronized {
      // TODO: Implement me
    }
  }

  override def getSocket: DatagramSocket = socket

  override def startTimer(seqNo: Int): Unit = timer.start(seqNo)

  override def stopTimer(seqNo: Int): Unit = timer.cancel(seqNo)

  override def setCurrentState(nextState: State): Unit = currentState = nextState
}
