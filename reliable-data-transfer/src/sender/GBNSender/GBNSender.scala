package sender.GBNSender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender.{MyTimer, PacketBuilder, Sender, State}
import window.{GBNSenderWindow, Window}

class GBNSender(address: InetAddress, port: Int, windowSize: Int, socket: DatagramSocket) extends Sender {
  val lock: Object = new Object
  var window: Window = new GBNSenderWindow(windowSize)

  var currentState: State = new Wait(this)
  val UDPSocket: DatagramSocket = socket
  val timer: MyTimer = new MyTimer(this, 100)

  val notifier = new AckNotifier(UDPSocket, this)
  notifier.start()

  class AckNotifier(socket: DatagramSocket, listener: GBNSender) extends Thread {

    override def run(): Unit = {
      println("listening for ACKs")
      while(true) {
        val buf = new Array[Byte](256)
        val rec_packet = new DatagramPacket(buf, buf.length)
        socket.receive(rec_packet)
        listener.receive(rec_packet)
      }
    }
  }

  override  def send(data: Array[Byte]): Unit = {
    lock.synchronized {
      // TODO:
      // check if there is space available in window
      // then put data in window and call RDTSend
      // else refuse the data

      if(window.hasSpace) {
        window.append(makePacket(data, window.getNextSequenceNumber))
        currentState.RDTSend(data)
      } else {
        println("Window has no space")
      }

    }
  }

  private def receive(packet: DatagramPacket): Unit = {
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
