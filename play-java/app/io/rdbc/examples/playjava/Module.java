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

package io.rdbc.examples.playjava;

import akka.actor.ActorSystem;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.rdbc.japi.ConnectionFactory;
import io.rdbc.pgsql.core.config.japi.Auth;
import io.rdbc.pgsql.transport.netty.japi.NettyPgConnectionFactory;
import io.rdbc.pool.japi.ConnectionPool;
import io.rdbc.pool.japi.ConnectionPoolConfig;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class Module extends AbstractModule {

    @Provides
    @Singleton
    @Inject
    public ConnectionFactory connectionFactory(ActorSystem actorSystem,
                                               ApplicationLifecycle lifecycle,
                                               com.typesafe.config.Config cfg)
            throws ExecutionException, InterruptedException {

        ConnectionFactory pool = ConnectionPool.create(
                NettyPgConnectionFactory.create(NettyPgConnectionFactory.Config.builder()
                        .host(cfg.getString("rdbc.pgsql.host"))
                        .port(cfg.getInt("rdbc.pgsql.port"))
                        .authenticator(
                                Auth.password(
                                        cfg.getString("rdbc.pgsql.username"),
                                        cfg.getString("rdbc.pgsql.password")
                                )
                        ).build()),
                ConnectionPoolConfig.builder()
                        .size(5)
                        .build()
        );


        CompletionStage<Void> bootstrap = pool.withConnection(conn ->
                conn.statement(
                        "CREATE TABLE IF NOT EXISTS rdbc_demo(i INT4, t TIMESTAMP, v VARCHAR)"
                ).noArgs().execute()
        );

        bootstrap.toCompletableFuture().get();

        lifecycle.addStopHook(pool::shutdown);

        return pool;
    }

    public void configure() {
    }
}
