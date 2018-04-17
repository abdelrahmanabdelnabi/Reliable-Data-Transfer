package receiver
import java.net.{DatagramPacket, DatagramSocket}

class WaitForZero(context: Receiver) extends State {

  override def RDTReceive(packet: DatagramPacket): Unit = {
    var socket = new DatagramSocket()

    // make ACK packet
    val ack = "ACK".getBytes
    val ackPacket = new DatagramPacket(ack, ack.length, packet.getAddress, packet.getPort)
    socket.send(ackPacket)

  }
}
