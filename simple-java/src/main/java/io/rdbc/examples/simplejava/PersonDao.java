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

import io.rdbc.japi.Connection;
import io.rdbc.japi.ConnectionFactory;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

class PersonDao {

    private final ConnectionFactory db;

    PersonDao(ConnectionFactory db) {
        this.db = db;
    }

    CompletionStage<Void> insertPersons(List<Person> persons) {
        return db.withTransaction(conn ->
                createTableIfNotExists(conn).thenCompose(ignoreVoid ->
                        AsyncUtil.reduceAsync(
                                persons.stream(),
                                person -> insertPerson(conn, person)
                        )
                )
        );
    }

    CompletionStage<List<Person>> listPersons(String name) {
        return db.withConnection(conn ->
                conn.statement("select id, name, age from people where name = ?")
                        .bindByIdx(name)
                        .executeForSet()
                        .thenApply(rs -> rs.getRows().stream()
                                .map(row ->
                                        new Person(
                                                row.getLong("id"),
                                                row.getStr("name"),
                                                row.getInt("age")
                                        )
                                ).collect(Collectors.toList())
                        )
        );
    }

    private CompletionStage<Void> createTableIfNotExists(Connection conn) {
        return conn.statement("create table if not exists people (id serial, name varchar, age int)")
                .noArgs()
                .execute();
    }

    private CompletionStage<Void> insertPerson(Connection conn, Person person) {
        return conn.statement("insert into people (name, age) values (:name, :age)")
                .arg("name", person.getName())
                .arg("age", person.getAge())
                .bind()
                .execute();
    }

}
