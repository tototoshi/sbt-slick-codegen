// addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

libraryDependencies += {
  "org.scala-sbt" % "scripted-plugin_2.12" % sbtVersion.value
}
