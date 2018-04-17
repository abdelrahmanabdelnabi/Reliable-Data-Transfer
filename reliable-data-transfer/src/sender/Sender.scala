package sender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

class Sender(val address: InetAddress, val port: Int) {

  var currentState: State = new WaitForSendZero(this)
  val myAdd = InetAddress.getByName("localhost")
  val myPort = 4440
  val UDPSocket: DatagramSocket = new DatagramSocket()

  val notifier = new AckNotifier(UDPSocket, this)
  notifier.start()

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

}
