import java.net.{DatagramPacket, DatagramSocket}

object Main {

  def main(args: Array[String]): Unit = {
    val connectionAcceptPort = 4445

    val socket = new DatagramSocket(connectionAcceptPort)

    println("Server started, accepting connection on port " + connectionAcceptPort)

    while(true) {

      val buf = new Array[Byte](256)
      val rec_packet = new DatagramPacket(buf, buf.length)
      socket.receive(rec_packet)

      val address = rec_packet.getAddress
      val port = rec_packet.getPort

      val fileName = new String(rec_packet.getData).replaceAll("\u0000", "")
      println("client " + address + " : " + port + " requested file: " + fileName)
      new SingleRequestServer(address, port, fileName).start()
    }

  }
}
