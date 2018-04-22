package sender.StopAndWaitSender

import java.net.DatagramPacket

import sender.{PacketBuilder, Sender, State}

private class WaitForACK(seqNo: Int, context: Sender) extends State {

  override def timeout(seqNo: Int): Unit = {
    println("packet " + seqNo + " timed out. resending")
    context.getSocket.send(context.window.getPacket(seqNo))
    context.startTimer(seqNo)
  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    throw new IllegalStateException("Can not send when waiting for ACK")
    false
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {

    // if packet is not corrupted and with correct sequence number
    val corrupted = PacketBuilder.isCorrupted(datagramPacket)
    if(PacketBuilder.extractSeqNo(datagramPacket) == seqNo && !corrupted) {
      val receivedSeqNo = PacketBuilder.extractSeqNo(datagramPacket)
      context.stopTimer(seqNo)
      println("received ACK: " + receivedSeqNo)
      context.setCurrentState(new WaitForSend(1-seqNo, context))
      context.window.acknowledge(seqNo)
    } else if(corrupted) {
      println("received a corrupted ACK")
    } else {
      println("received a delayed or duplicate ACK")
    }
  }
}
