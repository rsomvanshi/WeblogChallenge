name := "solution"

version := "1.0"
scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.3.3",
  "org.apache.spark" %% "spark-sql" % "2.3.3",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
