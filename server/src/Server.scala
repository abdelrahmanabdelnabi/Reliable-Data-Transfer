class Server extends Thread{

  import java.net.DatagramSocket
  import java.net.DatagramPacket
  import java.net.InetAddress

  var socket = new DatagramSocket(4445)

  def server(name: String) {
    socket = new DatagramSocket(4445)
  }

  override def run(): Unit = {
    while(true) {
      val buf = new Array[Byte](256)
      val rec_packet = new DatagramPacket(buf, buf.length)
      socket.receive(rec_packet)

      val message = "hi there, you just received a message from the server."
      val send_buf = message.getBytes()

      val address = rec_packet.getAddress
      val port = rec_packet.getPort
      val send_packet = new DatagramPacket(send_buf, send_buf.length, address, port)
      socket.send(send_packet)
    }
  }
}
