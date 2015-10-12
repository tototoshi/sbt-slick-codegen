scalariformSettings

sbtPlugin := true

name := """sbt-slick-codegen"""

organization := "com.github.tototoshi"

version := "1.2.0-SNAPSHOT"

scalaVersion := "2.10.5"

val slickVersion = "3.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-codegen" % slickVersion
)

publishMavenStyle := true

publishTo <<= version { (v: String) => _publishTo(v) }

def _publishTo(v: String) = {
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomExtra :=
  <url>http://github.com/tototoshi/sbt-slick-codegen</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:tototoshi/sbt-slick-codegen</url>
    <connection>scm:git:git@github.com:tototoshi/sbt-slick-codegen.git</connection>
  </scm>
  <developers>
    <developer>
      <id>tototoshi</id>
      <name>Toshiyuki Takahashi</name>
      <url>http://tototoshi.github.io</url>
    </developer>
  </developers>
