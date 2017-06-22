addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % System.getProperty("plugin.version"))

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
