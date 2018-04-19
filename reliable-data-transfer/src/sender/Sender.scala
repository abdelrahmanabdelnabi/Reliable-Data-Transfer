package sender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}


class Sender(address: InetAddress, port: Int) {

  var currentState: State = new WaitForSend(0,this)
  val UDPSocket: DatagramSocket = new DatagramSocket()
  var fileName = ""

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
    currentState.RDTSend(data)
  }

  def receive(packet: DatagramPacket): Unit = {
    currentState.RDTReceive(packet)
  }

  def setCurrentState(nextState: State): Unit = {
    currentState = nextState
  }

  def makePacket(data: Array[Byte]): DatagramPacket = {
    new DatagramPacket(data, data.length, address, port)
  }

  def makePacket(data: Array[Byte], seqNo: Int): DatagramPacket = {
    PacketBuilder.buildDataPacket(data, seqNo, 0)
  }

  def isAvailable(): Boolean = {
    if(currentState.isInstanceOf[WaitForSend])
      true
    else
      false
  }



}

