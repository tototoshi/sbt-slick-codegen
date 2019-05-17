package org.gadget.elastic.codegen

import play.api.libs.json.JsObject
import play.api.libs.json.JsValue

/**
 *
 * @project sbt-elastic-codegen
 * @author npersad on 2019-05-16
 *
 */
abstract class AbstractSourceCodeGenerator(jsValue: JsObject)
  extends StringGeneratorHelpers {

  /**
   * Map[ClassName -> (fieldName, fieldType, prefix)*]
   * prefix is used to prefix a classes field such as "Sailing"
   * with "sailing_<fieldName>" in this way we can compromise
   * NOT having parent-child relationships as elastic strongly discourages
   * this and providing a clear separation
   * of "types" (sailing, meta, category, cabin, res)
   *
   * @param obj         The raw elastic schema form ..:9200/<schema>
   * @param jsonPrefix  The prefix of the json path
   * @return            An "class map"
   */
  def parseSchema(obj: JsValue, jsonPrefix: Boolean = true): Schema =
    (obj \ "properties").asOpt[JsObject] match {

      case Some(propObj) =>
        propObj.as[JsObject].fields.map {

          case (key, jsValue) =>

            val className       = getClassName(key)
            val fieldName       = key.split("_").lastOption.mkString
            val fieldType       = getFieldType(key, jsValue)
            val jsonParserName  = prefixedField(key, jsonPrefix)

            val classProperty =
              Map(className -> List((fieldName, fieldType, jsonParserName)))

            merge(classProperty, getSubClass(fieldName, jsValue))

        }.reduce(merge)

      case _ => Map.empty[String, List[(String, String, Option[String])]]
    }

  // scalastyle:off line.size.limit
  /**
   * Merges two Schemas
   * This is especially essential for properties that are shared among "classes"
   * I.e stopSell
   *
   * Be aware that I DO NOT DEAL WITH repeated keys in this function
   * but I do get only distinct keys in the "code" function
   *
   * For example, since stopSell appears three times
   * (one per sailing, meta, category)
   * The result of this merge would be:
   * Map(stopSell -> List((FIT, group, "<jspath>"), (FIT, group, "<jspath>"), (FIT, group, "<jspath>")))
   *
   * @param m1  Schema Input
   * @param m2  Schema Input
   * @return    A merged map
   */
  // scalastyle:on
  private def merge(m1: Schema, m2: Schema): Schema = {
    (m1.toList ++ m2.toList)
      .groupBy { case (clas, _) => clas } // by "class"
      .map { case (clas, fields) => Map(clas -> fields.flatMap { case (name, elements) => elements }) }
      .reduceLeftOption(_ ++ _)
      .getOrElse(Map.empty[String, List[(String, String, Option[String])]])
  }

  private def getSubClass(className: String, jsValue: JsValue): Schema =
    parseSchema(jsValue, false)
      .values
      .reduceLeftOption(_ ++ _)
      .map { fields => Map(className.capitalize -> fields) }
      .getOrElse(Map.empty[String, List[(String, String, Option[String])]])

  def generateCode(pkg: String): String = {

    // scalastyle:off indentation
    val disclaimer =
      """
        |// AUTO-GENERATED Elastic data model
        |/** Stand-alone Elastic data model for immediate use */
      """.stripMargin
    val packages =
      s"""package $pkg
         |$disclaimer
         |import play.api.libs.json._
         |import play.api.libs.functional.syntax._
         |
      """.stripMargin
    // scalastyle: on

    packages + code(parseSchema(jsValue))
  }

}

trait StringGeneratorHelpers {
  type Schema = Map[String, List[(String, String, Option[String])]]

  /**
   *
   * @param  key  the name of the field from the (raw) elastic json
   * @return      the scala class name that will be written
   */
  def getClassName(key: String): String =
    key.split("_")
      .headOption
      .map(_.capitalize)
      .mkString

  /**
   *
   * @param key       Used to get the json path of this
   *                  field in the elastic schema
   * @param prefixed  whether to use the provided prefix or not
   * @return
   */
  def prefixedField(key: String, prefixed: Boolean): Option[String] =
    if (prefixed) {
      key.split("_").headOption.map(_ + "_")
    } else {
      None
    }

  /**
   *
   * @param fieldName Get the field type in the elastic schema
   *                  Note that the type may not be present but
   *                 will have another "properties" object instead
   *                 so return the name of the field itself as it
   *                 is a type (in some class) itself
   * @param jsValue
   * @return
   */
  def getFieldType(fieldName: String, jsValue: JsValue): String =
    (jsValue \ "type").asOpt[String] match {

      case Some(tp) =>
        tp

      case None =>
        fieldName
          .split("_")
          .lastOption
          .map(_.capitalize)
          .mkString
    }

  /**
   *
   * @param elasticType The elastic type (i.e text or long)
   * @return            The scala primitive type
   */
  def getPrimitiveType(elasticType: String): String = elasticType match {
    case "text" => "String"
    case "long" => "Long"
    case "double" => "Double"
    case _ => elasticType
  }

  /**
   *
   * @param schema The mapped elastic schema
   * @return       The generated source code for this elastic "class"
   */
  def code(schema: Schema): String =
    schema map {
      case (className, classFieldsList) =>

        val classFields = classFieldsList.distinct

        s"\r\ncase class $className (\n" +
          s"${
            classFields.map {
              case (fieldName, fieldType, _) =>
                s"  $fieldName: ${getPrimitiveType(fieldType)}"
            }.mkString(",\n")
          }" +
          "\n)".stripMargin +
          "\n\n" +
          (s"object $className {" +
            s"\n  implicit val ${className}Writes: Writes[$className] = (" +
            s"\n${
              classFields.map {
                case (fieldName, fieldType, jsonPrefix) =>
                  s"    (JsPath \\ ${
                    "\"" +
                      jsonPrefix.getOrElse("") + fieldName +
                      "\""
                  }).write[${getPrimitiveType(fieldType)}]"
              }.mkString(" and\n")
            }" +
            s"\n  )(unlift($className.unapply))\n}\n".stripMargin)

    } reduce (_ ++ _)
}
