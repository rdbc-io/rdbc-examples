import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val commonSettings = Seq(
  organization := "io.rdbc",
  organizationName := "rdbc contributors",
  scalaVersion := "2.12.4",
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
  buildInfoKeys := Vector(version, scalaVersion, git.gitHeadCommit, BuildInfoKey.action("buildTime") {
    java.time.Instant.now()
  }),
  scalastyleFailOnError := true
)

lazy val examplesRoot = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(play)

lazy val play = (project in file("rdbc-play"))
  .settings(commonSettings: _*)
  .settings(
    name := "rdbc-play",
    libraryDependencies ++= Vector(
      Library.rdbcScalaApi,
      Library.rdbcUtil,
      Library.rdbcPool,
      Library.rdbcPgsqlNetty,
      Library.webjars,
      Library.jquery,
      Library.webshim,
      guice
    ),
    buildInfoPackage := "io.rdbc.examples.play"
  ).enablePlugins(PlayScala)
