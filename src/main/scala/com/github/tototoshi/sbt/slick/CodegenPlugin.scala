package com.github.tototoshi.sbt.slick

import java.io.File

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
    s: TaskStreams): File = {

    val database = driver.api.Database.forURL(url = url, driver = jdbcDriver, user = user, password = password)

    try {
      database.source.createConnection().close()
    } catch {
      case e: Throwable =>
        throw new RuntimeException("Failed to run slick-codegen: " + e.getMessage, e)
    }

    s.log.info(s"Generate source code with slick-codegen: url=${url}, user=${user}")

    val tables = driver.defaultTables.map(ts => ts.filterNot(t => excluded contains t.name.name))

    val dbio = for {
      m <- driver.createModel(Some(tables))
    } yield generator(m).writeToFile(
      profile = "slick.driver." + driver.toString,
      folder = outputDir,
      pkg = pkg,
      container = container,
      fileName = fileName
    )

    Await.ready(
      {
        val f = database.run(dbio)
        f onFailure {
          case e: Throwable =>
            s.log.info("CodegenPlugin Error")
            e.printStackTrace()
        }
        f
      }
    , Duration.Inf)

    val generatedFile = outputDir + File.separator + pkg.replaceAllLiterally(".", File.separator) + File.separator + fileName
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
        streams.value
      ))
    }
  )

}
