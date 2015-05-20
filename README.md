# sbt-slick-codegen

slick-codegen compile hook for sbt

## Install

```scala
// plugins.sbt

addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "1.0.0")

// Database driver
// For example, when you are using PostgreSQL
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
```

## Configuration

```scala
// build.sbt
import slick.codegen.SourceCodeGenerator
import slick.{ model => m }

// required
slickCodegenSettings

// required
// Register codegen hook
sourceGenerators in Compile <+= slickCodegen

// required
slickCodegenDatabaseUrl := "jdbc:postgresql://localhost/example"

// required
slickCodegenDatabaseUser := "dbuser"

// required
slickCodegenDatabasePassword := "dbpassword"

// required (If not set, postgresql driver is choosen)
slickCodegenDriver := scala.slick.driver.PostgresDriver

// required (If not set, postgresql driver is choosen)
slickCodegenJdbcDriver := "org.postgresql.Driver"

// optional but maybe you want
slickCodegenOutputPackage := "com.example.models"

// optional, pass your own custom source code generator
slickCodegenCodeGenerator := { (model: m.Model) => new SourceCodeGenerator(model) }

// optional
// For example, to exclude flyway's schema_version table from the target of codegen
slickCodegenExcludedTables in Compile := Seq("schema_version")
```

## Example

https://github.com/tototoshi/sbt-slick-codegen-example


## License

Apache 2.0
