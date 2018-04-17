class Client extends Thread {

  import java.net.DatagramPacket
  import java.net.DatagramSocket
  import java.net.InetAddress

  override def run(): Unit = {

    val socket = new DatagramSocket

    val buf = new Array[Byte](256)
    val address = InetAddress.getByName("localhost")
    val packet = new DatagramPacket(buf, buf.length, address, 4445)
    socket.send(packet)

    while (true) {

      val rec_packet = new DatagramPacket(buf, buf.length)
      socket.receive(packet)
      val received = new String(rec_packet.getData, 0, packet.getLength)
      System.out.println("Quote of the Moment: " + received)
    }
  }
}
