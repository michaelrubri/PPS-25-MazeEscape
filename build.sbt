val scala3Version = "3.6.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "PPS-25-MazeEscape",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

      libraryDependencies ++= Seq(
          "org.junit.jupiter" % "junit-jupiter-api" % "5.10.0" % Test,
          "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.0" % Test,
          "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0"
      )
  )