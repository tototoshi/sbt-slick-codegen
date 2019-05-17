package org.gadget.elastic.codegen

import play.api.libs.json.JsObject
import play.api.libs.json.Json

/**
 *
 * @project sbt-elastic-codegen
 * @author npersad on 2019-05-16
 *
 */
class ElasticSourceCodeGenerator(model: JsObject)
  extends AbstractSourceCodeGenerator(model) with OutputHelpers

object ElasticSourceCodeGenerator {

  def run(
    elasticUrl: String,
    index: String,
    fileName: String,
    pkg: String): String = {

    val result    = scala.io.Source.fromURL(s"$elasticUrl/$index?pretty").mkString
    val json      = Json.parse(result)
    val mappings  = (json \ index \ "mappings").as[JsObject]

    val generator = new ElasticSourceCodeGenerator(mappings)
    generator.generateCode(pkg)
  }

}
