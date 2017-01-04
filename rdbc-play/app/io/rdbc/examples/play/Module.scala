package io.rdbc.examples.play

import javax.inject.Inject

import com.google.inject.{AbstractModule, Provides, Singleton}
import io.rdbc.pgsql.transport.netty.{NettyPgConnFactoryConfig, NettyPgConnectionFactory}
import io.rdbc.sapi.ConnectionFactory
import play.api.inject.ApplicationLifecycle

class Module extends AbstractModule {

  @Provides
  @Singleton
  @Inject
  def connectionFactory(lifecycle: ApplicationLifecycle): ConnectionFactory = {
    val factory = NettyPgConnectionFactory(
      NettyPgConnFactoryConfig("localhost", 5432, "povder", "povder")
        .copy(ec = play.api.libs.concurrent.Execution.defaultContext)
    )

    lifecycle.addStopHook { () =>
      factory.shutdown()
    }

    factory
  }

  def configure(): Unit = ()
}
