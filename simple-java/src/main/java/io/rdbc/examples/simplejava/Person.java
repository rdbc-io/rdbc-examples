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

import java.util.Optional;

final class Person {
    private final Optional<Long> id;
    private final String name;
    private final int age;

    Person(String name, int age) {
        this.id = Optional.empty();
        this.name = name;
        this.age = age;
    }

    Person(Long id, String name, int age) {
        this.id = Optional.of(id);
        this.name = name;
        this.age = age;
    }

    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Person(" +
                "id=" + id.map(Object::toString).orElse("none") +
                ", name='" + name + '\'' +
                ", age=" + age +
                ')';
    }
}
