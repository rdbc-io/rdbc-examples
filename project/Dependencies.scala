import sbt._

object Library {
  val rdbcScalaApi = "io.rdbc" %% "rdbc-api-scala" % "0.0.60"
  val rdbcPgsqlNetty = "io.rdbc.pgsql" %% "pgsql-transport-netty" % "0.3.0.12"

  val webjars = "org.webjars" %% "webjars-play" % "2.5.0"
  val jquery = "org.webjars" % "jquery" % "3.2.0"
  val webshim = "org.webjars.bower" % "webshim" % "1.15.10"
}
