import java.net.{DatagramPacket, DatagramSocket, InetAddress}

class Sender(val address: InetAddress, val port: Int) {

  var currentState: State = new WaitForSendZero(this)
  val UDPSocket: DatagramSocket = new DatagramSocket()

  def send(data: Array[Byte]): Boolean = {
    currentState.RDTSend(data)
  }

  def setCurrentState(nextState: State): Unit = {
    currentState = nextState
  }

  def makePacket(data: Array[Byte]): DatagramPacket = {
    new DatagramPacket(data, data.length, address, port)
  }

}
