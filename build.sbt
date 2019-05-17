name := """sbt-elastic-codegen"""
organization := "org.npersad"
version := "0.1-SNAPSHOT"

organization := "com.github.npersad"

description := "An sbt plugin to generate case classes from elastic schema"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.10"
)

sbtPlugin := true

bintrayPackageLabels := Seq("sbt","plugin")
bintrayVcsUrl := Some("""git@github.com:org.npersad/sbt-elastic-code-gen.git""")

initialCommands in console := """import org.gadget._"""

enablePlugins(ScriptedPlugin)
// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
