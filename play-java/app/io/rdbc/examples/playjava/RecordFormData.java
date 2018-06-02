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
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class RecordFormData {
    private Optional<Integer> integer;
    private Optional<Instant> timestamp;
    private Optional<String> varchar;

    public RecordFormData() {
        integer = Optional.empty();
        timestamp = Optional.empty();
        varchar = Optional.empty();
    }

    public Optional<Integer> getInteger() {
        return integer;
    }

    public void setInteger(String integer) {
        this.integer = inputField(integer).map(Integer::valueOf);
    }

    public Optional<Instant> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        inputField(timestamp).map(str ->
                LocalDateTime.parse(str, ISO_DATE_TIME).toInstant(UTC)
        );
    }

    public Optional<String> getVarchar() {
        return varchar;
    }

    public void setVarchar(String varchar) {
        this.varchar = inputField(varchar);
    }

    private static Optional<String> inputField(String value) {
        return Optional.ofNullable(value).flatMap(str -> {
            if (str.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(str);
            }
        });
    }
}
