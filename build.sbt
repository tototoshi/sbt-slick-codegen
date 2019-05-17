name := """sbt-elastic-codegen"""

organization := "com.github.npersad"

description := "An sbt plugin to generate case classes from elastic schema"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.10"
)

version := "1.0.0"

sbtPlugin := true
