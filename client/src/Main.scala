import java.net.InetAddress

import sender.Sender

object Main {
  def main(args: Array[String]): Unit = {
    val address = InetAddress.getByName("localhost")
    val sender = new Sender(address, 4445)
    while(true) {
      val text = scala.io.StdIn.readLine()
      sender.send(text.getBytes)

    }
  }

}
