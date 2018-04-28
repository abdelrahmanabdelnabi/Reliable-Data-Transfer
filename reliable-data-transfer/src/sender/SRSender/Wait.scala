package sender.SRSender

import java.net.DatagramPacket

import sender.{PacketBuilder, Sender, State}

class Wait(context: Sender) extends State {

  override def timeout(seqNo: Int): Unit = {
    // Do nothing if the window is empty
    if(context.window.isEmpty)
      return
    println("Packet timeout, resending packet " + seqNo)
    context.startTimer(seqNo)
    context.getSocket.send(context.window.getPacket(seqNo))
  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    val sendPacket = context.window.getPacket(context.window.getNextSequenceNumber - 1)
    context.getSocket.send(sendPacket)

    context.startTimer(context.window.getNextSequenceNumber - 1)

    true
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    // Do nothing if the window is empty
    if(context.window.getBase == context.window.getNextSequenceNumber)
      return

    val seqNo = PacketBuilder.extractSeqNo(datagramPacket)
    val base = seqNo + 1
    val corrupted = PacketBuilder.isCorrupted(datagramPacket)

    if(!corrupted){
      print("received ACK " + seqNo + " base: " + context.window.getBase
        + " nextSeqNo: " + context.window.getNextSequenceNumber)

      context.window.acknowledge(seqNo)
      context.stopTimer(seqNo)
    } else{
      // If packet is corrupted, then do nothing.
    }

  }
}
