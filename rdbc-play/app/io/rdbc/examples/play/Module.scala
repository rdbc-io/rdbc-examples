package io.rdbc.examples.play

import javax.inject.Inject

import com.google.inject.{AbstractModule, Provides, Singleton}
import io.rdbc.pgsql.transport.netty.NettyPgConnectionFactory
import io.rdbc.sapi._
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Await
import scala.concurrent.duration._

class Module extends AbstractModule {

  @Provides
  @Singleton
  @Inject
  def connectionFactory(lifecycle: ApplicationLifecycle,
                        cfg: Configuration): ConnectionFactory = {
    implicit val timeout = 10.seconds.timeout

    val factory = NettyPgConnectionFactory(
      cfg.getString("rdbc.pgsql.host").getOrElse("localhost"),
      cfg.getInt("rdbc.pgsql.port").getOrElse(5432),
      cfg.getString("rdbc.pgsql.username").getOrElse("postgres"),
      cfg.getString("rdbc.pgsql.password").getOrElse("postgres")
    )

    val bootstrap = factory.withConnectionF { conn =>
      conn.statement(
        sql"CREATE TABLE IF NOT EXISTS rdbc_demo(i INT4, t TIMESTAMPTZ, s VARCHAR)"
      ).execute()
    }

    Await.result(bootstrap, timeout.value)

    lifecycle.addStopHook { () =>
      factory.shutdown()
    }

    factory
  }

  def configure(): Unit = ()
}
