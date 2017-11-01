# sbt-slick-codegen

slick-codegen compile hook for sbt

## Install

|Slick version|slick-codegen version|
|-------------|--------------|
|3.2.x|1.3.1|
|3.1.x|1.2.1|
|3.0.x|1.1.1|


```scala
// plugins.sbt

addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % slickCodegenVersion)

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
slickCodegenDriver := slick.driver.PostgresDriver

// required (If not set, postgresql driver is choosen)
slickCodegenJdbcDriver := "org.postgresql.Driver"

// optional but maybe you want
slickCodegenOutputPackage := "com.example.models"

// optional, pass your own custom source code generator
slickCodegenCodeGenerator := { (model: m.Model) => new SourceCodeGenerator(model) }

// optional
// For example of all the tables in a database we only would like to take table named "users"
slickCodegenIncludedTables in Compile := Seq("users")

// optional
// For example, to exclude flyway's schema_version table from the target of codegen. This still applies after slickCodegenIncludedTables.
slickCodegenExcludedTables in Compile := Seq("schema_version")

//optional
slickCodegenOutputDir := (sourceManaged in Compile).value
```

## Example

https://github.com/tototoshi/sbt-slick-codegen-example


## License

Apache 2.0
