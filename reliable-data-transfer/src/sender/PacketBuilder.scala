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
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(data.length + 4 + 4)
    byteBuffer.putInt(seqNo)
    byteBuffer.putInt(checkSum)
    byteBuffer.put(data)
    val allData = byteBuffer.array()

    new DatagramPacket(allData, allData.length)
  }

  def buildACKPacket(seqNo: Int, checkSum: Int): DatagramPacket = {
    val data = "ACK".getBytes()

    buildDataPacket(data, seqNo, checkSum)
  }

  def buildACKPacket(seqNo: Int, checkSum: Int, address: InetAddress, port: Int): DatagramPacket = {
    val packet = buildACKPacket(seqNo, checkSum)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  def extractData(datagramPacket: DatagramPacket): Array[Byte] = {
    val byteBuffer = ByteBuffer.allocate(datagramPacket.getData.length)
    val dataLength = datagramPacket.getLength - 8 // 4 bytes checksum, 4 bytes seqNo

    byteBuffer.put(datagramPacket.getData)
    byteBuffer.position(8)
    val data: Array[Byte] = new Array[Byte](dataLength)
    byteBuffer.get(data, 0, dataLength)
    data
  }

  def extractSeqNo(datagramPacket: DatagramPacket): Int  = {
    extractIntAtOffset(datagramPacket, 0)
  }

  def extractCheckSum(datagramPacket: DatagramPacket): Int = {
    extractIntAtOffset(datagramPacket, 4)
  }

  private def extractIntAtOffset(datagramPacket: DatagramPacket, offset: Int): Int = {
    val data: Array[Byte] = datagramPacket.getData

    val byteBuffer = ByteBuffer.allocate(data.length)
    byteBuffer.put(data)
    byteBuffer.position(offset)
    byteBuffer.getInt()
  }

}
