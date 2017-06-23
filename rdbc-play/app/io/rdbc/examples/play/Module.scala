package io.rdbc.examples.play

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides, Singleton}
import io.rdbc.examples.play.scheduler.AkkaScheduler
import io.rdbc.pgsql.transport.netty.NettyPgConnectionFactory
import io.rdbc.pool.sapi.ConnectionPool
import io.rdbc.pool.sapi.ConnectionPoolConfig.Default
import io.rdbc.sapi._
import play.api.Configuration
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

  def configure(): Unit = ()
}
