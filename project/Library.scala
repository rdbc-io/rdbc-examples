import sbt._

object Library {
  private val rdbcVersion = "0.0.80"
  private val rdbcPoolVersion = "0.0.10"

  val rdbcScalaApi = "io.rdbc" %% "rdbc-api-scala" % rdbcVersion
  val rdbcJavaApi = "io.rdbc" % "rdbc-api-java" % rdbcVersion
  val rdbcUtil = "io.rdbc" %% "rdbc-util" % rdbcVersion
  val rdbcPoolScala = "io.rdbc.pool" %% "rdbc-pool-scala" % rdbcPoolVersion
  val rdbcPoolJava = "io.rdbc.pool" %% "rdbc-pool-java" % rdbcPoolVersion
  val rdbcPgsqlNetty = "io.rdbc.pgsql" %% "pgsql-transport-netty" % "0.3.1.22"

  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  val webjars = "org.webjars" %% "webjars-play" % "2.6.3"
  val jquery = "org.webjars" % "jquery" % "3.3.1"
  val webshim = "org.webjars.bower" % "webshim" % "1.15.10"
}
