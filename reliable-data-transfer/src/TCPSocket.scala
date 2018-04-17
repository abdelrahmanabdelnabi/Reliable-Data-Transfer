import java.net.{DatagramPacket, DatagramSocket, InetAddress}

class TCPSocket(val address: InetAddress, val port: Int) {

  val UDPSocket: DatagramSocket = new DatagramSocket()

  // corresponds to a call from above
  def send(message: String) {

    val data: Array[Byte] = message.getBytes
    val packet = new DatagramPacket(data, data.length, address, 4445)

    UDPSocket.send(packet)

  }

}
