import java.net.InetAddress
import java.nio.file.{Files, Path, Paths}

import sender.Sender

class SingleRequestServer(address: InetAddress, port: Int, fileName: String) extends Thread {

  import java.net.DatagramSocket
  import java.net.DatagramPacket
  import java.net.InetAddress

  var socket = new DatagramSocket()


  override def run(): Unit = {
    // send empty connection setup packet
    val setupPacket = new DatagramPacket(new Array[Byte](0), 0, address, port)
    socket.send(setupPacket)

    // wait for ACK
    val buf = new Array[Byte](256)
    val ack = new DatagramPacket(buf, buf.length)
    socket.receive(ack)

    //TODO: assert is ACK
    println("received connection setup ACK")

    // open requested file and convert it to byte array
    val path: Path = Paths.get(System.getProperty("user.dir"), fileName)
    println(path)
    val data = Files.readAllBytes(path)

    // create a sender object
    val sender = new Sender(address, port)

    println("created sender object and will now send")
    // Send 100 bytes at a time
    for(group <- data.grouped(100).toArray) {
      // wait until sender is available
      synchronized {

        while(!sender.isAvailable()){
          print(".")
        }
        println()
      }
      println("sending 100 bytes")

      sender.send(group)
    }

    println("finished sending file")
  }


}
