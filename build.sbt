import sbt.DefaultMavenRepository

name := "playmoneytransfers"

version := "1.0"

lazy val `playmoneytransfers` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  guice,
  evolutions,
  "com.h2database" % "h2" % "1.4.192",
  "org.mockito" % "mockito-core" % "2.7.19" % Test,
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

routesImport ++= Seq(
  "java.util.UUID",
  "controllers.parser.UuidParser"
)

resolvers ++= Seq(
  DefaultMavenRepository
)

