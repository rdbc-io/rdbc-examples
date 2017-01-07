package io.rdbc.examples.play

import java.time.Instant

import play.api.libs.json.{Json, Writes}

object Record {
  implicit val writes: Writes[Record] = Json.writes[Record]
}

case class Record(i: Option[Int], t: Option[Instant], s: Option[String])
