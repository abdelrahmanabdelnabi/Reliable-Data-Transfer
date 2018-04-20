package sender.GBNSender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender.{MyTimer, PacketBuilder, Sender, State}

class GBNSender(address: InetAddress, port: Int, windowSize: Int) extends Sender{
  val lock: Object = new Object
  super.windowSize = windowSize

  var currentState: State = new Wait(this)
  val UDPSocket: DatagramSocket = new DatagramSocket()
  val timer: MyTimer = new MyTimer(this, 10)
  var currentPacket: DatagramPacket = _

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
    lock.synchronized{
      // TODO:
      // check if there is space available in window
      // then put data in window and call RDTSend
      // else refuse the data

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
      if(currentState.isInstanceOf[Wait])
        true
      else
        false
    }
  }

  override def getSocket: DatagramSocket = {
    UDPSocket
  }

  override def getPacket(seqNo: Int): DatagramPacket = {
    if(seqNo >= base && seqNo < nextSequenceNumber)
      // TODO: return correct packet
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
