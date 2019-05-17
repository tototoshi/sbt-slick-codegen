package org.gadget.elastic.codegen

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/** Output-related code-generation utilities. */
trait OutputHelpers {

  /**
   * Writes given content to a file.
   * Ensures the file ends with a newline character.
   *
   * @group Output
   */
  def writeStringToFile(
    content:  String,
    folder:   String,
    pkg:      String,
    fileName: String): Unit = {

    val folder2: String = folder + "/" + pkg.replace(".", "/") + "/"
    new File(folder2).mkdirs()

    val fileLocation = s"$folder2$fileName"
    val file         = new File(fileLocation)

    if (!file.exists()) {
      file.createNewFile()
    }
    val fw = new FileWriter(file.getAbsoluteFile)
    val bw = new BufferedWriter(fw)
    bw.write(content)
    if (!content.endsWith("\n")) bw.write("\n")
    bw.close()
  }
}
