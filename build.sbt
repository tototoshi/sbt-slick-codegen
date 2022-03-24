import scalariform.formatter.preferences._
import scala.collection.JavaConverters._
import java.lang.management.ManagementFactory

enablePlugins(ScriptedPlugin)

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)

sbtPlugin := true

name := """sbt-slick-codegen"""

organization := "com.github.tototoshi"

crossSbtVersions := Seq("0.13.18", "1.6.2")

version := "1.4.1-SNAPSHOT"

val slickVersion = SettingKey[String]("slickVersion")

slickVersion := {
  if ((pluginCrossBuild / sbtVersion).value.startsWith("1.")) {
    "3.3.3"
  } else {
    "3.1.0"
  }
}

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
