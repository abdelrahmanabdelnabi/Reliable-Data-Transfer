package sender

import java.util.TimerTask
import java.util.Timer

class MyTimer(listener: Sender, timeOut: Int) {
  private var timerZero: Timer = _
  private var timerOne: Timer = _

  def start(seqNo: Int): Unit = {
    seqNo match {
      case 0 => timerZero = new Timer()
        timerZero.schedule(new TimerTask {
          override def run(): Unit = {
            listener.timeout(0)
          }
        }, timeOut)
      case 1 => timerOne = new Timer()
        timerOne.schedule(new TimerTask {
          override def run(): Unit = {
            listener.timeout(1)
          }
        }, timeOut)
    }
  }

  def cancel(seqNo: Int): Unit = {
    seqNo match {
      case 0 => timerZero.cancel()
      case 1 => timerOne.cancel()
    }
  }
}
