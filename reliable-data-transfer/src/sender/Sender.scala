package sender

import java.net.{DatagramPacket, DatagramSocket}

import window.Window

trait Sender {

  var window: Window


  /**
    * Returns the DatagramSocket for this sender object.
    * @return
    */
  def getSocket: DatagramSocket

  /**
    * Starts the timer for a packet with the given sequence number.
    * @param seqNo
    */
  def startTimer(seqNo: Int): Unit

  /**
    * Stops the timer for a packet with the given sequence number.
    * @param seqNo
    */
  def stopTimer(seqNo: Int): Unit

  /**
    * Sets the current state of the sender to be the next state passed to the method.
    *
    * @param nextState: The next state of the sender object
    */
  def setCurrentState(nextState: State): Unit

  /**
    * The function that should be called by the client to send data. The method blocks until
    * the given data is sent through the Sender's socket.
    *
    * @param data: the data to be sent as a byte array.
    */
  def send(data: Array[Byte]): Unit

  /**
    * This method will be called to notify the sender object whenever a timeout for a packet
    * with a passed sequence number occurs. The sender needs to only handle the timeout and
    * should not be concerned about the caller of the method.
    *
    * @param seqNo: the sequence number of the packet that timed-out.
    */
  def timeout(seqNo: Int)

  /**
    * This method will be called to notify the sender object whenever a packet arrives at at its
    * socket. The sender needs to only handle the timeout and should not be concerned
    * about the caller of the method.
    *
    * @param packet: the packet that was received
    */
  def receive(packet: DatagramPacket): Unit

  /**
    * returns true if the sender has space available in its window to send data.
    * @return true if the sender can send data.
    */
  def isAvailable: Boolean

}
