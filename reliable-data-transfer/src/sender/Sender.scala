package sender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}


class Sender(address: InetAddress, port: Int) {
  val lock: Object = new Object

  var currentState: State = new WaitForSend(0,this)
  val UDPSocket: DatagramSocket = new DatagramSocket()
  var fileName = ""
  val timer: MyTimer = new MyTimer(this, 10)
  var currentData: Array[Byte] = _

  val notifier = new AckNotifier(UDPSocket, this)
  notifier.start()

  def this(address: InetAddress, port: Int, fileName: String) {
    this(address, port)
    this.fileName = fileName
  }

  class AckNotifier(socket: DatagramSocket, listener: Sender) extends Thread {

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

  def send(data: Array[Byte]): Boolean = {
    lock.synchronized{
      currentData = data
      currentState.RDTSend(data)
    }
  }

  def receive(packet: DatagramPacket): Unit = {
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

  def isAvailable(): Boolean = {
    lock.synchronized{
      if(currentState.isInstanceOf[WaitForSend])
        true
      else
        false
    }
  }

}

