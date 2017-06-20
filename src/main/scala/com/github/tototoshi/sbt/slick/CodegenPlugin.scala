package com.github.tototoshi.sbt.slick

import sbt._
import Keys._
import slick.codegen.SourceCodeGenerator
import slick.driver.JdbcProfile
import slick.{ model => m }

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object CodegenPlugin extends sbt.Plugin {

  lazy val slickCodegen: TaskKey[Seq[File]] = taskKey[Seq[File]]("Command to run codegen")

  lazy val slickCodegenDatabaseUrl: SettingKey[String] =
    settingKey[String]("URL of database used by codegen")

  lazy val slickCodegenDatabaseUser: SettingKey[String] =
    settingKey[String]("User of database used by codegen")

  lazy val slickCodegenDatabasePassword: SettingKey[String] =
    settingKey[String]("Password of database used by codegen")

  lazy val slickCodegenDriver: SettingKey[JdbcProfile] =
    settingKey[JdbcProfile]("Slick driver used by codegen")

  lazy val slickCodegenJdbcDriver: SettingKey[String] =
    settingKey[String]("Jdbc driver used by codegen")

  lazy val slickCodegenOutputPackage: SettingKey[String] =
    settingKey[String]("Package of generated code")

  lazy val slickCodegenOutputFile: SettingKey[String] =
    settingKey[String]("Generated file")

  lazy val slickCodegenOutputDir: SettingKey[File] =
    settingKey[File]("Folder where the generated file lives in")

  lazy val slickCodegenOutputContainer: SettingKey[String] =
    settingKey[String]("Container of generated source code")

  lazy val slickCodegenCodeGenerator: SettingKey[m.Model => SourceCodeGenerator] =
    settingKey[m.Model => SourceCodeGenerator]("Function to create CodeGenerator to be used")

  lazy val slickCodegenExcludedTables: SettingKey[Seq[String]] =
    settingKey[Seq[String]]("Tables that should be excluded")

  lazy val slickCodegenIncludedTables: SettingKey[Seq[String]] =
    settingKey[Seq[String]]("Tables that should be included. If this list is not nil, only the included tables minus excluded will be taken.")
  
  lazy val defaultSourceCodeGenerator: m.Model => SourceCodeGenerator = (model: m.Model) =>
    new SourceCodeGenerator(model)

  private def gen(
    generator: m.Model => SourceCodeGenerator,
    driver: JdbcProfile,
    jdbcDriver: String,
    url: String,
    user: String,
    password: String,
    outputDir: String,
    pkg: String,
    fileName: String,
    container: String,
    excluded: Seq[String],
    included: Seq[String],
    s: TaskStreams): File = {

    val database = driver.api.Database.forURL(url = url, driver = jdbcDriver, user = user, password = password)

    try {
      database.source.createConnection().close()
    } catch {
      case e: Throwable =>
        throw new RuntimeException("Failed to run slick-codegen: " + e.getMessage, e)
    }

    s.log.info(s"Generate source code with slick-codegen: url=${url}, user=${user}")

    val tables = driver.defaultTables
      .map(ts => ts.filter(t => included.isEmpty || (included contains t.name.name)))
      .map(ts => ts.filterNot(t => excluded contains t.name.name))
      

    val driverClassName = driver.getClass.getName
    val profile = {
      // if it's a singleton object, then just reference it directly
      if (driverClassName.endsWith("$")) driverClassName.stripSuffix("$")
      // if it's an instance of a regular class, we don't know constructor args; try the no-arguments constructor and hope for the best
      else s"new $driverClassName()"
    }

    val dbio = for {
      m <- driver.createModel(Some(tables))
    } yield generator(m).writeToFile(
      profile = profile,
      folder = outputDir,
      pkg = pkg,
      container = container,
      fileName = fileName
    )

    Await.result(database.run(dbio), Duration.Inf)

    val generatedFile = outputDir + "/" + pkg.replaceAllLiterally(".", "/") + "/" + fileName
    s.log.info(s"Source code has generated in ${generatedFile}")
    file(generatedFile)
  }

  lazy val slickCodegenSettings: Seq[Setting[_]] = Seq(
    slickCodegenDriver := slick.driver.PostgresDriver,
    slickCodegenJdbcDriver := "org.postgresql.Driver",
    slickCodegenDatabaseUrl := "Database url is not set",
    slickCodegenDatabaseUser := "Database user is not set",
    slickCodegenDatabasePassword := "Database password is not set",
    slickCodegenOutputPackage := "com.example",
    slickCodegenOutputFile := "Tables.scala",
    slickCodegenOutputDir := (sourceManaged in Compile).value,
    slickCodegenOutputContainer := "Tables",
    slickCodegenExcludedTables := Seq(),
    slickCodegenIncludedTables := Seq(),
    slickCodegenCodeGenerator := defaultSourceCodeGenerator,
    slickCodegen := {
      val outDir = {
        val folder = slickCodegenOutputDir.value
        if (folder.exists()) {
          require(folder.isDirectory, s"file :[$folder] is not a directory")
        } else {
          folder.mkdir()
        }
        folder.getPath
      }
      val outPkg = (slickCodegenOutputPackage).value
      val outFile = (slickCodegenOutputFile).value
      Seq(gen(
        (slickCodegenCodeGenerator).value,
        (slickCodegenDriver).value,
        (slickCodegenJdbcDriver).value,
        (slickCodegenDatabaseUrl).value,
        (slickCodegenDatabaseUser).value,
        (slickCodegenDatabasePassword).value,
        outDir,
        outPkg,
        outFile,
        slickCodegenOutputContainer.value,
        slickCodegenExcludedTables.value,
        slickCodegenIncludedTables.value,
        streams.value
      ))
    }
  )

}
