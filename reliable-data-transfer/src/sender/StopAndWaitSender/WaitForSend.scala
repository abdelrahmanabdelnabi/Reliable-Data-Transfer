package sender.StopAndWaitSender

import java.net.DatagramPacket

import sender.{Sender, State}

private class WaitForSend(seqNo: Int, context: Sender) extends State {

  override def timeout(seqNo: Int): Unit = {
    // we should do nothing here. If the sender it waiting for a call from above, this means
    // that any packets sent before, if any, are already acknowledged
  }

  override def RDTSend(data: Array[Byte]): Boolean = {
    context.setCurrentState(new WaitForACK(seqNo, context))
    context.getSocket.send(context.window.getPacket(seqNo))
    context.startTimer(seqNo)
    true
  }

  override def RDTReceive(datagramPacket: DatagramPacket): Unit = {
    // according to rdt 3.0, if the sender receives a packet and he
    // is not expecting one, he should just ignore it
  }

}
