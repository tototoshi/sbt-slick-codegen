scalariformSettings

sbtPlugin := true

name := """sbt-slick-codegen"""

organization := "com.github.tototoshi"

crossSbtVersions := Seq("0.13.17", "1.1.0")

version := "1.2.2-SNAPSHOT"

val slickVersion = SettingKey[String]("slickVersion")

slickVersion := {
  if((sbtVersion in pluginCrossBuild).value.startsWith("1.")) {
    "3.2.1"
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

ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

ScriptedPlugin.scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

ScriptedPlugin.scriptedLaunchOpts ++= Seq(
  "-Dplugin.version=" + version.value,
  "-Dslick.version=" + slickVersion.value
)
