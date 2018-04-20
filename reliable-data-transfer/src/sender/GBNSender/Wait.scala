package sender.GBNSender

import java.net.DatagramPacket

import sender.{PacketBuilder, Sender, State}

private class Wait(context: Sender) extends State {

  override def timeout(seqNo: Int): Unit = {
    context.startTimer(0)

    for(seqNo <- context.base until context.nextSequenceNumber) {
      val packet = context.getPacket(seqNo)
      context.getSocket.send(packet)
    }

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    val sendPacket = context.getPacket(context.nextSequenceNumber)
    context.getSocket.send(sendPacket)

    if(context.base == context.nextSequenceNumber)
      context.startTimer(0)

    context.nextSequenceNumber += 1
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    val base = PacketBuilder.extractSeqNo(datagramPacket) + 1
    val corrupted = PacketBuilder.isCorrupted(datagramPacket)

    if(!corrupted) {
      if(base == context.nextSequenceNumber)
        context.stopTimer(0)
      else
        context.startTimer(0)
    } else {
      // according to GBNSender FSM, if packet is corrupted then do nothing
    }

  }
}
