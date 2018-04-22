package sender

import java.net.{DatagramPacket, InetAddress}
import java.nio.ByteBuffer

object PacketBuilder {

  def buildDataPacket(data: Array[Byte], seqNo: Int, checkSum: Int, address: InetAddress, port: Int): DatagramPacket = {
    val packet: DatagramPacket = buildDataPacket(data, seqNo)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  def buildDataPacket(data: Array[Byte], seqNo: Int): DatagramPacket = {
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(data.length + 4 + 8)
    byteBuffer.putInt(seqNo)
    val checkSum = getCheckSum(data)
    byteBuffer.putLong(checkSum)
    byteBuffer.put(data)
    val allData = byteBuffer.array()

    new DatagramPacket(allData, allData.length)
  }

  def buildACKPacket(seqNo: Int): DatagramPacket = {
    val data = "ACK".getBytes()

    buildDataPacket(data, seqNo)
  }

  def buildACKPacket(seqNo: Int, address: InetAddress, port: Int): DatagramPacket = {
    val packet = buildACKPacket(seqNo)
    packet.setAddress(address)
    packet.setPort(port)
    packet
  }

  def extractData(datagramPacket: DatagramPacket): Array[Byte] = {
    val byteBuffer = ByteBuffer.allocate(datagramPacket.getData.length)
    val dataLength = datagramPacket.getLength - 12 // 8 bytes checksum, 4 bytes seqNo

    byteBuffer.put(datagramPacket.getData)
    byteBuffer.position(12)
    val data: Array[Byte] = new Array[Byte](dataLength)
    byteBuffer.get(data, 0, dataLength)
    data
  }

  def extractSeqNo(datagramPacket: DatagramPacket): Int  = {
    extractIntAtOffset(datagramPacket, 0)
  }

  def extractCheckSum(datagramPacket: DatagramPacket): Long = {
    val data: Array[Byte] = datagramPacket.getData

    val byteBuffer = ByteBuffer.allocate(data.length)
    byteBuffer.put(data)
    byteBuffer.position(4)
    byteBuffer.getLong()
  }

  private def extractIntAtOffset(datagramPacket: DatagramPacket, offset: Int): Int = {
    val data: Array[Byte] = datagramPacket.getData

    val byteBuffer = ByteBuffer.allocate(data.length)
    byteBuffer.put(data)
    byteBuffer.position(offset)
    byteBuffer.getInt()
  }

  private def getCheckSum(data: Array[Byte]): Long = {
    import java.util.zip.CRC32
    val checksum = new CRC32
    checksum.update(data, 0, data.length)
    checksum.getValue
  }

  def corruptPacketData(packet: DatagramPacket): DatagramPacket = {
    val byteBuffer = ByteBuffer.allocate(packet.getData.length)
    val dataLength = packet.getLength - 12 // 8 bytes checksum, 4 bytes seqNo
    val seqNo = extractSeqNo(packet)
    val checkSum = extractCheckSum(packet)
    val shuffledData: Array[Byte] = util.Random.shuffle(extractData(packet).toList).toArray

    byteBuffer.putInt(seqNo)
    byteBuffer.putLong(checkSum)
    byteBuffer.put(shuffledData)

    val newPacketData = byteBuffer.array()
    new DatagramPacket(newPacketData, newPacketData.length, packet.getAddress, packet.getPort)
  }

  def isCorrupted(datagramPacket: DatagramPacket): Boolean = {
    val rawData = extractData(datagramPacket)
    val checkSum = extractCheckSum(datagramPacket)

    val expectedCheckSum = getCheckSum(rawData)

    if(checkSum == expectedCheckSum)
      return false
    true
  }


}
