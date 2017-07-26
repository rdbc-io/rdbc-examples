import sbt._

object Library {
  private val rdbcVersion = "0.0.67"

  val rdbcScalaApi = "io.rdbc" %% "rdbc-api-scala" % rdbcVersion
  val rdbcUtil = "io.rdbc" %% "rdbc-util" % rdbcVersion
  val rdbcPool = "io.rdbc.pool" %% "rdbc-pool-scala" % "0.0.4"
  val rdbcPgsqlNetty = "io.rdbc.pgsql" %% "pgsql-transport-netty" % "0.3.1.18"

  val webjars = "org.webjars" %% "webjars-play" % "2.6.1"
  val jquery = "org.webjars" % "jquery" % "3.2.1"
  val webshim = "org.webjars.bower" % "webshim" % "1.15.10"
}
