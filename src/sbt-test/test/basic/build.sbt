crossScalaVersions := Seq("2.12.15", "2.13.8")

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies += "com.typesafe.slick" %% "slick" % System.getProperty("slick.version")

enablePlugins(CodegenPlugin)

Compile / sourceGenerators += slickCodegen

slickCodegenDatabaseUrl := "jdbc:postgresql://postgres/example"

slickCodegenDatabaseUser := "test"

slickCodegenDatabasePassword := "test"

slickCodegenOutputToMultipleFiles := true
