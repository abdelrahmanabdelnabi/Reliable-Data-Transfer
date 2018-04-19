package sender

import java.net.{DatagramPacket, InetAddress}
import java.nio.ByteBuffer

object PacketBuilder {

  def buildDataPacket(data: Array[Byte], seqNo: Int, checkSum: Int, address: InetAddress, port: Int): DatagramPacket = {
    val packet: DatagramPacket = buildDataPacket(data, seqNo, checkSum)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  def buildDataPacket(data: Array[Byte], seqNo: Int, checkSum: Int): DatagramPacket = {
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(1024)
    byteBuffer.putInt(seqNo)
    byteBuffer.putInt(checkSum)
    byteBuffer.put(data)
    val allData = byteBuffer.array()

    new DatagramPacket(allData, allData.length)
  }

  def buildACKPacket(seqNo: Int, checkSum: Int): DatagramPacket = {
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(1024)
    byteBuffer.putInt(seqNo)
    byteBuffer.putInt(checkSum)
    byteBuffer.put("ACK".getBytes)
    val allData = byteBuffer.array()

    new DatagramPacket(allData, allData.length)
  }

  def buildACKPacket(seqNo: Int, checkSum: Int, address: InetAddress, port: Int): DatagramPacket = {
    val packet = buildACKPacket(seqNo, checkSum)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  def extractData(datagramPacket: DatagramPacket): Array[Byte] = {
    val byteBuffer = ByteBuffer.allocate(1024)

    byteBuffer.put(datagramPacket.getData)
    val data: Array[Byte] = new Array[Byte](1024)
    byteBuffer.get(data, 8, 1024-8)
    data
  }

  def extractSeqNo(datagramPacket: DatagramPacket): Int  = {
    val buffer: Array[Byte] = datagramPacket.getData

    val byteBuffer = ByteBuffer.allocate(1024)
    byteBuffer.put(buffer)
    byteBuffer.getInt(0)
  }

  def extractCheckSum(datagramPacket: DatagramPacket): Int = {
    val buffer: Array[Byte] = datagramPacket.getData

    val byteBuffer = ByteBuffer.allocate(1024)
    byteBuffer.put(buffer)
    byteBuffer.getInt(4)
  }

}
