import java.net.InetAddress

import receiver.GBNReceiver.GBNReceiver
import receiver.StopAndWaitReceiver.StopAndWaitReceiver


object Main {
  def main(args: Array[String]): Unit = {
    val address = InetAddress.getByName("localhost")
    val receiver = new GBNReceiver(address, 4445)

    receiver.start()
  }

}
