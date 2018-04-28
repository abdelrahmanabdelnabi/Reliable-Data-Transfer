package sender

import java.util
import java.util.TimerTask
import java.util.Timer


class MyTimer(listener: Sender, timeOut: Int) {
  private val timers: util.HashMap[Int, Timer] = new util.HashMap()

  def start(seqNo: Int): Unit = {
    timers.put(seqNo, new Timer())
    timers.get(seqNo).schedule(new MyTask(seqNo), timeOut)
  }

  class MyTask(seqNo: Int) extends TimerTask {
    override def run(): Unit = {
      listener.timeout(seqNo)
    }
  }

  def cancel(seqNo: Int): Unit = {
     timers.get(seqNo).cancel()
  }
}
