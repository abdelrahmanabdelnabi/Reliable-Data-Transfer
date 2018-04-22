package sender.StopAndWaitSender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender._
import window.{GBNSenderWindow, Window}


class StopAndWaitSender(address: InetAddress, port: Int) extends Sender {
  val lock: Object = new Object
  var window: Window = new GBNSenderWindow(1)

  var base = 0
  var nextSequenceNumber = 0

  var currentState: State = new WaitForSend(0,this)
  val UDPSocket: DatagramSocket = new DatagramSocket()
  var fileName = ""
  val timer: MyTimer = new MyTimer(this, 10)
  var currentPacket: DatagramPacket = _

  val notifier = new AckNotifier(UDPSocket, this)
  notifier.start()

  def this(address: InetAddress, port: Int, fileName: String) {
    this(address, port)
    this.fileName = fileName
  }

  class AckNotifier(socket: DatagramSocket, listener: StopAndWaitSender) extends Thread {

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
    lock.synchronized{
      currentPacket = makePacket(data, base)
      currentState.RDTSend(data)
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
      if(currentState.isInstanceOf[WaitForSend])
        true
      else
        false
    }
  }

  override def getSocket: DatagramSocket = {
    UDPSocket
  }

  override def getPacket(seqNo: Int): DatagramPacket = {
    if(seqNo == base)
      currentPacket
    else
      throw new IllegalArgumentException
  }

  override def startTimer(seqNo: Int): Unit = {
    timer.start(seqNo)
  }

  override def stopTimer(seqNo: Int): Unit = {
    timer.cancel(seqNo)
  }
}

