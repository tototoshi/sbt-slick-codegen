scalaVersion := "2.11.12"

libraryDependencies += "com.typesafe.slick" %% "slick" % System.getProperty("slick.version")

enablePlugins(CodegenPlugin)

sourceGenerators in Compile += slickCodegen

slickCodegenDatabaseUrl := "jdbc:postgresql://localhost/example"

slickCodegenDatabaseUser := "test"

slickCodegenDatabasePassword := "test"
