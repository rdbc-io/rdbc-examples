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

package io.rdbc.examples.playscala

import java.time.Instant

import play.api.libs.json.{Json, Writes}

object Record {
  implicit val writes: Writes[Record] = Json.writes[Record]
}

final case class Record(integer: Option[Int],
                        timestamp: Option[Instant],
                        varchar: Option[String])
