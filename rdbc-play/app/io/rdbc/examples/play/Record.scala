package io.rdbc.examples.play

import java.time.{Instant, LocalDateTime}

case class Record(i: Option[Int], t: Option[Instant], s: Option[String])
