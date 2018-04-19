import java.net.InetAddress

import receiver.Receiver


object Main {
  def main(args: Array[String]): Unit = {
    val address = InetAddress.getByName("localhost")
    val receiver = new Receiver(address, 4445)

    receiver.start()
  }

}
