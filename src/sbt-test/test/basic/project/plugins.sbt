sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % x)
  case _       => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

libraryDependencies += "com.h2database" % "h2" % "2.2.224"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
