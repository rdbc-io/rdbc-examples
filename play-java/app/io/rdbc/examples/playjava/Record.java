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

import java.time.Instant;
import java.util.Optional;

public final class Record {
    private final Optional<Integer> integer;
    private final Optional<Instant> timestamp;
    private final Optional<String> varchar;

    public Record(Optional<Integer> integer,
                  Optional<Instant> timestamp,
                  Optional<String> varchar) {
        this.integer = integer;
        this.timestamp = timestamp;
        this.varchar = varchar;
    }

    public Optional<Instant> getTimestamp() {
        return timestamp;
    }

    public Optional<Integer> getInteger() {
        return integer;
    }

    public Optional<String> getVarchar() {
        return varchar;
    }
}
