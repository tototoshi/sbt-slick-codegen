# Work in progress - elastic-codegen

elastic-codegen compile hook for sbt

## Motivation
This plugin allows you to create case classes that engender parent child-relationships (joins) that elastic search discourages while at the same time writiing json objects that maintain the denormalized structure that elastic stresses.
This is achievable with a simple naming conventino of the form `<level name>_<field>`.  


Please see the wiki for a detailed explanation of this plugin
https://github.com/npersad/elastic-codegen/wiki

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
