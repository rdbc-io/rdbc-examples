package io.rdbc.examples.play.scheduler

import akka.actor.ActorSystem
import io.rdbc.util.scheduler.{ScheduledTask, TaskScheduler}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class AkkaScheduler(system: ActorSystem,
                    terminateSystemOnShutdown: Boolean = false)
  extends TaskScheduler {

  import system.dispatcher

  def schedule(delay: FiniteDuration)
              (action: () => Unit): ScheduledTask = {
    val cancellable = system.scheduler.scheduleOnce(delay)(action())
    new AkkaScheduledTask(cancellable)
  }

  def shutdown(): Future[Unit] = {
    if (terminateSystemOnShutdown) {
      system.terminate().map(_ => ())
    } else {
      Future.successful(())
    }
  }
}
