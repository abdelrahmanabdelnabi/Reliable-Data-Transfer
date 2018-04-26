package sender.SRSender

import java.net.{DatagramPacket, DatagramSocket}

import sender.{ACKNotifier, MyTimer, Sender, State}
import sockets.UnreliableSocket
import window.Window

class SRSender extends Sender {

  override var window: Window = _
  val socket: DatagramSocket = new UnreliableSocket(0,0)
  var currentState: State = _
  val timer: MyTimer = new MyTimer(this, 30)
  val lock: Object = new Object
  val notifier:  ACKNotifier = new ACKNotifier(socket, this)
  notifier.start()

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
