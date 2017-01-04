import sbt._

object Version {
  val rdbc = "0.0.37"
  val rdbcPgsql = "0.4.0-SNAPSHOT"
}

object Library {
  val rdbcScalaApi = "io.rdbc" %% "rdbc-api-scala" % Version.rdbc
  val rdbcPgsqlNetty = "io.rdbc.pgsql" %% "pgsql-transport-netty40" % Version.rdbcPgsql
}
