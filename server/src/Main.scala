import receiver.Receiver

object Main {

  def main(args: Array[String]): Unit = {
    val receiver: Receiver = new Receiver(4445)

    receiver.start()


  }
}
