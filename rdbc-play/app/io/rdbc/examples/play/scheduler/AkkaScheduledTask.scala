package io.rdbc.examples.play.scheduler

import akka.actor.Cancellable
import io.rdbc.util.scheduler.ScheduledTask

class AkkaScheduledTask(task: Cancellable) extends ScheduledTask {
  def cancel(): Unit = task.cancel()
}
