package io.rdbc.examples.play

import javax.inject.Inject

import com.google.inject.{AbstractModule, Provides, Singleton}
import io.rdbc.pgsql.transport.netty.{NettyPgConnFactoryConfig, NettyPgConnectionFactory}
import io.rdbc.sapi.ConnectionFactory
import io.rdbc.sapi.Interpolators.SqlInterpolator
import play.api.inject.ApplicationLifecycle
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Await
import scala.concurrent.duration._

class Module extends AbstractModule {

  @Provides
  @Singleton
  @Inject
  def connectionFactory(lifecycle: ApplicationLifecycle): ConnectionFactory = {
    implicit val timeout = 10.seconds
    val factory = NettyPgConnectionFactory(
      NettyPgConnFactoryConfig("localhost", 5432, "povder", "povder")
        .copy(ec = play.api.libs.concurrent.Execution.defaultContext)
    )

    val bootstrap = factory.withConnection { conn =>
      conn.statement(sql"create table if not exists rdbc_demo(i int4, t timestamptz, s varchar)")
        .flatMap(_.executeIgnoringResult())
    }

    Await.result(bootstrap, timeout)

    lifecycle.addStopHook { () =>
      factory.shutdown()
    }

    factory
  }

  def configure(): Unit = ()
}
