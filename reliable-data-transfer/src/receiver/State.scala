package receiver

import java.net.DatagramPacket

trait State {
  def RDTReceive(packet: DatagramPacket): Unit
}
