package sender.GBNSender

import java.net.DatagramPacket

import sender.{PacketBuilder, Sender, State}

private class Wait(context: Sender) extends State {

  override def timeout(seqNo: Int): Unit = {
    // ignore if window is empty
    if(context.window.isEmpty)
      return
    context.startTimer(0)
    val from = context.window.getBase
    val to = context.window.getNextSequenceNumber - 1
    println("packet timed-out, resending packets " + from + " to " + to)

    for(seqNo <- context.window.getBase until context.window.getNextSequenceNumber) {
      val packet = context.getPacket(seqNo)
      context.getSocket.send(packet)
    }

  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    val sendPacket = context.getPacket(context.window.getNextSequenceNumber - 1)
    context.getSocket.send(sendPacket)

    if(context.window.getBase == context.window.getNextSequenceNumber - 1)
      context.startTimer(0)

    true
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    // ignore packets when window is empty
    if(context.window.getBase == context.window.getNextSequenceNumber)
      return
    val seqNo = PacketBuilder.extractSeqNo(datagramPacket)
    val base = seqNo + 1
    val corrupted = PacketBuilder.isCorrupted(datagramPacket)

    if(!corrupted) {
      print("received ACK " + seqNo + " base: " + context.window.getBase
        + " nextSeqNo: " + context.window.getNextSequenceNumber)
      context.window.acknowledge(seqNo)
      if(base == context.window.getNextSequenceNumber) {
        context.stopTimer(0)
        println(" stopping timer")
      } else {
        println(" starting timer")
        context.stopTimer(0)
        context.startTimer(0)
      }
    } else {
      // according to GBNSender FSM, if packet is corrupted then do nothing
    }

  }
}
