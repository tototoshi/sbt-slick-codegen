import scala.collection.JavaConverters._
import java.lang.management.ManagementFactory

lazy val scala212 = "2.12.21"
lazy val scala3 = "3.8.3"

ThisBuild / scalaVersion := scala212
ThisBuild / crossScalaVersions := Seq(scala212, scala3)

enablePlugins(SbtPlugin)

sbtPlugin := true

name := """sbt-slick-codegen"""

organization := "com.github.tototoshi"

version := "2.2.0"

crossSbtVersions := Seq("1.12.9", "2.0.0-RC11")

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.12.9"
    case _      => "2.0.0-RC11"
  }
}

scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => Seq("-Xsource:3")
    case _            => Seq.empty
  }
}

val slickVersion = SettingKey[String]("slickVersion")

slickVersion := "3.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % slickVersion.value,
  "com.typesafe.slick" %% "slick-codegen" % slickVersion.value
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

Test / publishArtifact := false

pomExtra :=
  <url>https://github.com/tototoshi/sbt-slick-codegen</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.html</url>
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
      <url>https://tototoshi.github.io</url>
    </developer>
  </developers>

scriptedBufferLog := false
scriptedLaunchOpts ++= ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList.filter(a =>
  Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)
scriptedLaunchOpts ++= Seq(
  "-Dplugin.version=" + version.value,
  "-Dslick.version=" + slickVersion.value
)
