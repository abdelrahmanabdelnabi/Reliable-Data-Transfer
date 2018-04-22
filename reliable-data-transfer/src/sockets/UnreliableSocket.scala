package sockets

import java.net.{DatagramPacket, DatagramSocket}

import sender.PacketBuilder

import scala.util.Random

class UnreliableSocket(dropProbability: Float = 0, corruptionProbability: Float = 0) extends DatagramSocket {

  var packetDropProbability: Float = dropProbability
  var packetCorruptionProbability: Float = corruptionProbability
  val rand: Random = new Random()

  def setDropProbability(probability: Float): Unit = packetDropProbability = probability
  def setCorruptionProbability(probability: Float): Unit = packetCorruptionProbability = probability

  override def send(packet: DatagramPacket): Unit = {
    val p = rand.nextFloat()
    if(p > packetDropProbability && p > packetCorruptionProbability)
      super.send(packet)
    else if(p <= packetDropProbability) {
      // drop the packet: i.e don't send it
    } else {
      // corrupt the packet and send it
      val corruptedPacket = PacketBuilder.corruptPacketData(packet)
      super.send(corruptedPacket)
    }
  }
}