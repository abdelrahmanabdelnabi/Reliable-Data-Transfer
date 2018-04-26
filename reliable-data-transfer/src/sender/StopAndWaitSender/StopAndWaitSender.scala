package sender.StopAndWaitSender

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import sender._
import window.{StopAndWaitWindow, Window}


class StopAndWaitSender(address: InetAddress, port: Int) extends Sender {
  val lock: Object = new Object
  var window: Window = new StopAndWaitWindow()

  var currentState: State = new WaitForSend(0,this)
  val UDPSocket: DatagramSocket = new DatagramSocket()
  var fileName = ""
  val timer: MyTimer = new MyTimer(this, 10)

  val notifier = new ACKNotifier(UDPSocket, this)
  notifier.start()

  def this(address: InetAddress, port: Int, fileName: String) {
    this(address, port)
    this.fileName = fileName
  }


  override  def send(data: Array[Byte]): Unit = {
    lock.synchronized{
      window.append(makePacket(data, window.getBase))
      currentState.RDTSend(data)
    }
  }

  override def receive(packet: DatagramPacket): Unit = {
    lock.synchronized {
      currentState.RDTReceive(packet)
    }
  }

  def timeout(seqNo: Int): Unit = {
    lock.synchronized{
      currentState.timeout(seqNo)
    }
  }

  def setCurrentState(nextState: State): Unit = {
    lock.synchronized{
      currentState = nextState
    }
  }

  def makePacket(data: Array[Byte], seqNo: Int): DatagramPacket = {
    val packet = PacketBuilder.buildDataPacket(data, seqNo)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  def isAvailable: Boolean = {
    lock.synchronized{
      if(currentState.isInstanceOf[WaitForSend])
        true
      else
        false
    }
  }

  override def getSocket: DatagramSocket = {
    UDPSocket
  }

  override def startTimer(seqNo: Int): Unit = {
    timer.start(seqNo)
  }

  override def stopTimer(seqNo: Int): Unit = {
    timer.cancel(seqNo)
  }
}
