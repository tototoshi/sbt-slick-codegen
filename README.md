# sbt-slick-codegen

slick-codegen compile hook for sbt

## Install

```scala
// plugins.sbt

addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "0.1.0-SNAPSHOT")
```

## Configuration

```scala
import com.github.tototoshi.sbt.slick.CodegenPlugin._

// required
// Register codegen hook
sourceGenerators in Compile <+= slickCodegen

// required
slickCodegenDatabaseUrl in Compile := "jdbc:postgresql://localhost/example"

// required
slickCodegenDatabaseUser in Compile := "dbuser"

// required
slickCodegenDatabasePassword in Compile := "dbpassword"

// required (If not set, postgresql driver is choosen)
slickCodegenDriver in Compile := scala.slick.driver.PostgresDriver

// required (If not set, postgresql driver is choosen)
slickCodegenJdbcDriver in Compile := "org.postgresql.Driver"

// optional but maybe you want
slickCodegenOutputPackage in Compile := "com.example.models"

// optional, pass your own custom source code generator
slickCodegenCodeGenerator in Compile := { (model:  m.Model) => new SourceCodeGenerator(model) }

// optional
slickCodegenExcludedTables in Compile := Seq("play_evolutions")
```

## License

Apache 2.0
