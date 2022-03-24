sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % x)
  case _       => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
