crossScalaVersions := Seq("2.11.12")

crossScalaVersions ++= {
  if (sbtVersion.value startsWith "1.")
    Seq("2.12.8", "2.13.0-RC2")
  else
    Nil
}

libraryDependencies += "com.typesafe.slick" %% "slick" % System.getProperty("slick.version")

enablePlugins(CodegenPlugin)

sourceGenerators in Compile += slickCodegen

slickCodegenDatabaseUrl := "jdbc:postgresql://localhost/example"

slickCodegenDatabaseUser := "test"

slickCodegenDatabasePassword := "test"
