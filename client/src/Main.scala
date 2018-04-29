import java.net.InetAddress

import receiver.GBNReceiver.GBNReceiver
import receiver.SRReceiver.SRReceiver
import receiver.StopAndWaitReceiver.StopAndWaitReceiver
import sender.SRSender.SRSender


object Main {
  def main(args: Array[String]): Unit = {
    val address = InetAddress.getByName("localhost")
    val receiver = new SRReceiver(address, 4445, 4)

    receiver.start()
  }

}
