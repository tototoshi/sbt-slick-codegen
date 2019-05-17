# Work in progress - elastic-codegen

elastic-codegen compile hook for sbt


## Motivation
As Elastic discourages the use of parent join relationships and stresses data denormalization
https://www.elastic.co/guide/en/elasticsearch/reference/current/parent-join.html#_parent_join_and_performance
we may find ourselves with cumbersome case classes that have manny fields.

I created a simple json traverser that traverses
an elastic schema with field names of the convention <br/> `<parent type>_<field name>` and collects them into a class by `<parent type>` name.  For example, in the schema below three case classes would be generated, 2 for "parent level" objects and 1 for 2nd level (child) objects.  In this way, in our scala application, we can somehwat "have our cake" and eat it too. It gives us a nice seperation of "parent-child" relationships (albeit due to a name convention) among case classes and still keep the elastic data denormalized when converting back to json.  Thus, can easily update parts of a record (i.e Rate below) or create entire records with JsObject ++ concatenation. 
  
  ```scala
case class Book(name: String, length: Long, isbn: String, rate: Rating)
  
object Book {
  implicit val BookWrites: Writes[Category] = (
    (JsPath \ "book_name").write[String] and
    (JsPath \ "book_length").write[Long] and
    (JsPath \ "book_isbn").write[String] and
    (JsPath \ "book_rating").write[Rating]
  )(unlift(Book.unapply))
}

case class Author(name: String, rating: Rating)
  
object Author {
  implicit val AuthorWrites: Writes[Author] = (
    (JsPath \ "author_name").write[String] and
    (JsPath \ "author_rating").write[Rating]
  )(unlift(Author.unapply))
}

case class Rating(amount: Long, lastModified: Long, reputation: String, stars: Long)

object Rating {
  implicit val RatingWrites: Writes[Rating] = (
    (JsPath \ "amount").write[Long] and
    (JsPath \ "lastModified").write[Long] and
    (JsPath \ "reputation").write[String] 
  )(unlift(Rating.unapply))
}

  
  ```
 ```json
  {  
   "bookstore":{  
      "aliases":{  

      },
      "mappings":{  
         "properties":{  
         
            "book_name":{  
               "type":"text",
               "fields":{  
                  "keyword":{  
                     "type":"keyword",
                     "ignore_above":256
                  }
               }
            },
             "book_rating":{  
               "properties":{  
                  "amount":{  
                     "type":"long"
                  },
                  "lastModifiedUTC":{  
                     "type":"long"
                  },
                  "reputation":{  
                     "type":"text",
                     "fields":{  
                        "keyword":{  
                           "type":"keyword",
                           "ignore_above":256
                        }
                     }
                  },
                  "stars":{  
                     "type":"long"
                  }
               }
            },
            "book_length":{  
               "type":"long"
            },
            "book_isbn":{  
               "type":"text",
               "fields":{  
                  "keyword":{  
                     "type":"keyword",
                     "ignore_above":256
                  }
               }
            },
            "author_rating":{  
               "properties":{  
                  "amount":{  
                     "type":"long"
                  },
                  "lastModifiedUTC":{  
                     "type":"long"
                  },
                  "reputation":{  
                     "type":"text",
                     "fields":{  
                        "keyword":{  
                           "type":"keyword",
                           "ignore_above":256
                        }
                     }
                  },
                  "stars":{  
                     "type":"long"
                  }
               }
            },
            "author_name":{  
               "type":"text",
               "fields":{  
                  "keyword":{  
                     "type":"keyword",
                     "ignore_above":256
                  }
               }
            }
         }
      },
      "settings":{  
         "index":{  
            "creation_date":"...",
            "number_of_shards":"1",
            "number_of_replicas":"1",
            ...
            "version":{  
               "created":"..."
            },
            "provided_name":"bookstore"
         }
      }
   }
}
 ```
  

## Install

|slick-codegen version|sbt version|
|---------------------|-----------|
|                1.0.0|      1.2.x|


```scala
// plugins.sbt

addSbtPlugin("com.github.npersad" % "sbt-elastic-codegen" % "1.0.8")

```

## Configuration

```scala
  .enablePlugins(CodegenPlugin)
  .settings(
    // required
    elasticUrl := "http://127.0.0.1:9200",
    // required
    elasticIndex := "records",
    // optional
    elasticCodegenOutputDir := (sourceManaged in Compile).value ,
    // optinal
    elasticCodegenOutputPackage := "com.elastique",
    // optional 
    elasticCodeGen := elasticCodeGen.value, // register manual sbt command)
    
    // optional
    sourceGenerators in Compile += elasticCodeGen.taskValue // register automatic code generation on every compile, remove for only manual use)
  )
```

## Example



## License

Apache 2.0
