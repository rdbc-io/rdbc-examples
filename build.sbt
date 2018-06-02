import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

lazy val commonSettings = Seq(
  organization := "io.rdbc",
  organizationName := "rdbc contributors",
  scalaVersion := "2.12.6",
  scalacOptions ++= Vector(
    "-unchecked",
    "-deprecation",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding", "UTF-8"
  ),
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  startYear := Some(2017),
  resolvers += Resolver.bintrayRepo("rdbc", "maven"),
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion,
    pushChanges
  ),
  scalastyleFailOnError := true
)

lazy val examplesRoot = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(`play-scala`, `play-java`, `simple-java`)

lazy val `play-scala` = (project in file("play-scala"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-play-scala",
    libraryDependencies ++= Vector(
      Library.rdbcScalaApi,
      Library.rdbcUtil,
      Library.rdbcPoolScala,
      Library.rdbcPgsqlNetty,
      Library.webjars,
      Library.jquery,
      Library.webshim,
      guice
    )
  ).enablePlugins(PlayScala)


lazy val `play-java` = (project in file("play-java"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-play-java",
    libraryDependencies ++= Vector(
      Library.rdbcJavaApi,
      Library.rdbcUtil,
      Library.rdbcPoolJava,
      Library.rdbcPgsqlNetty,
      Library.webjars,
      Library.jquery,
      Library.webshim,
      guice
    )
  ).enablePlugins(PlayJava)


lazy val `simple-java` = (project in file("simple-java"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-simple-java",
    libraryDependencies ++= Vector(
      Library.rdbcJavaApi,
      Library.rdbcPoolJava,
      Library.rdbcPgsqlNetty,
      Library.logback
    )
  )
