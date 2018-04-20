package receiver.StopAndWaitReceiver

import java.net.{DatagramPacket, DatagramSocket}

import receiver.State
import sender.PacketBuilder

private class WaitForPacket(context: StopAndWaitReceiver) extends State {
  var socket = new DatagramSocket()
  var seqNo = 0

  override def RDTReceive(packet: DatagramPacket): Unit = {
    val receivedSeqNo = PacketBuilder.extractSeqNo(packet)
    val data = PacketBuilder.extractData(packet)

    var corrupted: Boolean = PacketBuilder.isCorrupted(packet)

    // simulate corruption
//    if(Random.nextInt(2) == 1) {
//      corrupted = true
//    }

    if (receivedSeqNo == seqNo && !corrupted) {
      val ackPacket = PacketBuilder.buildACKPacket(seqNo, packet.getAddress, packet.getPort)
      seqNo = 1-seqNo
      println(new String(data))

//      if(Random.nextInt(4) == 1)
//        Thread.sleep(10)
      socket.send(ackPacket)
    } else {
      val ackPacket = PacketBuilder.buildACKPacket(1-seqNo, packet.getAddress, packet.getPort)
      socket.send(ackPacket)
      if(corrupted)
        println("received corrupted packet")
      else
        println("received duplicate packet")
    }

  }
}
