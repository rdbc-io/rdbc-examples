/*
 * Copyright 2017 rdbc contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.examples.playscala.scheduler

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
