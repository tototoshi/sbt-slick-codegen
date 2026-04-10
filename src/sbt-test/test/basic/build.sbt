ThisBuild / scalaVersion := "2.12.21"

crossScalaVersions := Seq("2.12.21", "2.13.18")

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies += "com.typesafe.slick" %% "slick" % System.getProperty("slick.version")

enablePlugins(CodegenPlugin)

val prepareDb = taskKey[Unit]("Prepare the scripted test database")

prepareDb := {
  Class.forName("org.h2.Driver")
  val connection = java.sql.DriverManager.getConnection(
    slickCodegenDatabaseUrl.value,
    slickCodegenDatabaseUser.value,
    slickCodegenDatabasePassword.value
  )
  try {
    val statement = connection.createStatement()
    try {
      statement.executeUpdate("create table if not exists users (id bigint primary key, name varchar(256));")
    } finally {
      statement.close()
    }
  } finally {
    connection.close()
  }
}

Compile / sourceGenerators += Def.task {
  prepareDb.value
  slickCodegen.value
}.taskValue

slickCodegenDatabaseUrl := s"jdbc:h2:file:${(target.value / "scripted-h2").getAbsolutePath};MODE=PostgreSQL;DB_CLOSE_DELAY=-1"

slickCodegenDatabaseUser := "test"

slickCodegenDatabasePassword := "test"

slickCodegenDriver := slick.jdbc.H2Profile

slickCodegenJdbcDriver := "org.h2.Driver"

slickCodegenOutputToMultipleFiles := true
