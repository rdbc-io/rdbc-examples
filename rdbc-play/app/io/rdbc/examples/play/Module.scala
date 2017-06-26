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

package io.rdbc.examples.play

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides, Singleton}
import io.rdbc.examples.play.scheduler.AkkaScheduler
import io.rdbc.pgsql.transport.netty.NettyPgConnectionFactory
import io.rdbc.pool.sapi.ConnectionPool
import io.rdbc.pool.sapi.ConnectionPoolConfig.Default
import io.rdbc.sapi._
import play.api.{Configuration, Logger}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Await
import scala.concurrent.duration._

class Module extends AbstractModule {

  @Provides
  @Singleton
  @Inject
  def connectionFactory(actorSystem: ActorSystem,
                        lifecycle: ApplicationLifecycle,
                        cfg: Configuration): ConnectionFactory = {
    logVersions()
    implicit val timeout = 10.seconds.timeout

    val pool = new ConnectionPool(
      connFact = NettyPgConnectionFactory(
        cfg.getOptional[String]("rdbc.pgsql.host").getOrElse("localhost"),
        cfg.getOptional[Int]("rdbc.pgsql.port").getOrElse(5432),
        cfg.getOptional[String]("rdbc.pgsql.username").getOrElse("postgres"),
        cfg.getOptional[String]("rdbc.pgsql.password").getOrElse("postgres")
      ),
      config = Default.copy(
        taskScheduler = () => new AkkaScheduler(actorSystem)
      )
    )

    val bootstrap = pool.withConnection { conn =>
      conn.statement(
        sql"CREATE TABLE IF NOT EXISTS rdbc_demo(i INT4, t TIMESTAMPTZ, s VARCHAR)"
      ).execute()
    }

    Await.result(bootstrap, 15.seconds)

    lifecycle.addStopHook { () =>
      pool.shutdown()
    }

    pool
  }

  private def logVersions(): Unit = {
    //Logger.info(s"Using rdbc-api-scala ${io.rdbc.sapi.BuildInfo.version} built at ${io.rdbc.sapi.BuildInfo.buildTime}")
    Logger.info(s"Using rdbc-pgsql ${io.rdbc.pgsql.core.BuildInfo.version} built at ${io.rdbc.pgsql.core.BuildInfo.buildTime}")
    Logger.info(s"Using rdbc-pgsql-transport-netty ${io.rdbc.pgsql.transport.netty.BuildInfo.version} built at ${io.rdbc.pgsql.transport.netty.BuildInfo.buildTime}")
    //Logger.info(s"Using rdbc-pool ${io.rdbc.pool.BuildInfo.version} built at ${io.rdbc.pool.BuildInfo.buildTime}")
  }

  def configure(): Unit = ()
}
