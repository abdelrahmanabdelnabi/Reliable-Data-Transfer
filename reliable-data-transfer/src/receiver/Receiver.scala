package receiver

import java.net.DatagramSocket

trait Receiver {

  var expectedSeqNo = 1

  def getSocket: DatagramSocket

  def setCurrentState(nextState: State): Unit

}
