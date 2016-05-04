lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xlint"
)


lazy val testDeps = libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

lazy val root = (project in file("."))
  .settings(
  libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.7.12",
    "com.google.guava" % "guava" % "19.0",
    "com.google.code.findbugs" % "jsr305" % "3.0.0",
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.3",
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.5",
    "org.apache.logging.log4j" % "log4j-api" % "2.5",
    "org.apache.logging.log4j" % "log4j-core" % "2.5",
    "me.scf37.config2" % "config2_2.11" % "0.2",
    "org.mongodb" % "mongodb-driver-async" % "3.2.2",
    "org.mongodb" % "mongodb-driver" % "3.2.2"
  ),
  testDeps,
  scalacOptions ++= compilerOptions,
  resolvers += "Scf37" at "https://dl.bintray.com/scf37/maven/"
)

name := "dbinstaller"
organization := "me.scf37"

scalaVersion := "2.11.8"
