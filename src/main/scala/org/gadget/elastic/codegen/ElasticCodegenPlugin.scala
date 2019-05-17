package org.gadget.elastic.codegen

import sbt.{ File, TaskKey, _ }
import Keys._

/**
 *
 * @project sbt-elastic-codegen
 * @author npersad on 2019-05-16
 *
 */
object ElasticCodegenPlugin extends sbt.AutoPlugin with OutputHelpers {

  object autoImport {
    lazy val elasticCodeGen: TaskKey[Seq[File]] = taskKey[Seq[File]]("Command to run codegen")

    lazy val elasticUrl: SettingKey[String] =
      settingKey[String]("Elastic Search endpoint")

    lazy val elasticIndex: SettingKey[String] =
      settingKey[String]("Elastic Search Index")

    lazy val elasticCodegenOutputDir: SettingKey[File] =
      settingKey[File]("Folder where the generated file lives in")

    lazy val elasticCodegenOutputFile: SettingKey[String] =
      settingKey[String]("Generated Elastic File")

    lazy val elasticCodegenOutputPackage: SettingKey[String] =
      settingKey[String]("Package of generated code")
  }

  import autoImport._

  private def gen(
    url:       String,
    outputDir: String,
    index:     String,
    pkg:       String,
    fileName:  String,
    s:         TaskStreams): File = {

    s.log.info(s"Generate source code with elastic-codegen: url = ${url}")
    s.log.info(s"writing file ${fileName} to ${outputDir} ")

    val content = ElasticSourceCodeGenerator.run(
      url,
      index,
      fileName,
      pkg)

    val generatedFile = outputDir + "/" + pkg.replaceAllLiterally(".", "/") + "/" + fileName
    writeStringToFile(content, outputDir, pkg, fileName)
    file(generatedFile)

  }

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    elasticCodegenOutputDir     :=  (sourceManaged in Compile).value,
    elasticUrl                  :=  "elastic url is not set",
    elasticIndex                :=  "elastic index is not set",
    elasticCodegenOutputFile    :=  "Elastic.scala",
    elasticCodegenOutputPackage :=  "com.example",
    elasticCodeGen              :=  {

      val outDir = {

        val folder =            elasticCodegenOutputDir.value

        if (folder.exists())    require(folder.isDirectory, s"file :[$folder] is not a directory")
        else                    folder.mkdir()

        folder.getPath
      }

      val outPkg  = elasticCodegenOutputPackage.value
      val outFile = elasticCodegenOutputFile.value
      Seq(gen(
        url       = elasticUrl.value,
        outputDir = outDir,
        index     = elasticIndex.value,
        pkg       = outPkg,
        fileName  = outFile,
        s         = streams.value)
      )
    })

}
