name := "cui"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.scalactic" % "scalactic_2.11" % "2.2.1"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

TaskKey[String]("packageAll") <<=
  (sbt.Keys.`package` in Compile, sbt.Keys.packageDoc in Compile, sbt.Keys.packageSrc in Compile).map {
  (f1:sbt.File,f2:sbt.File, f3:sbt.File) => s"Package: $f1\nDoc: $f2\nSrc: $f3"
}