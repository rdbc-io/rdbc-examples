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

package io.rdbc.examples.simplejava;

import io.rdbc.japi.ConnectionFactory;
import io.rdbc.pgsql.core.config.japi.Auth;
import io.rdbc.pgsql.transport.netty.japi.NettyPgConnectionFactory;
import io.rdbc.pgsql.transport.netty.japi.NettyPgConnectionFactory.Config;
import io.rdbc.pool.japi.ConnectionPool;
import io.rdbc.pool.japi.ConnectionPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PersonExampleRunner {

    private static final Logger log = LoggerFactory.getLogger(PersonExampleRunner.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ConnectionFactory connFact = NettyPgConnectionFactory.create(
                Config.builder()
                        .host("localhost")
                        .port(5432)
                        .authenticator(Auth.password("postgres", "postgres"))
                        .build()
        );

        ConnectionFactory pool = ConnectionPool.create(
                connFact,
                ConnectionPoolConfig
                        .builder()
                        .name("example")
                        .size(10)
                        .build()
        );


        PersonDao dao = new PersonDao(pool);

        List<Person> persons = Arrays.asList(
                new Person("Alex", 32),
                new Person("Riley", 23),
                new Person("Alex", 34),
                new Person("Casey", 42)
        );

        dao.insertPersons(persons)
                .thenRun(() -> log.info("{} persons inserted", persons.size()))
                .thenCompose(ignore -> {
                    log.info("Querying for persons named 'Alex'");
                    return dao.listPersons("Alex");
                })
                .thenAccept(alexes -> {
                    log.info("Listing results");
                    alexes.forEach(a -> log.info(a.toString()));
                }).
                thenCompose(ignore -> pool.shutdown()).
                toCompletableFuture().get();
    }
}
