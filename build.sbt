// See README.md for license details.

scalaVersion     := "2.12.13"
version          := "0.1.0"
organization     := "com.github.Zerokei"
name             := "zjv2"

lazy val root = (project in file("."))
  .settings(
    Compile / unmanagedSources / excludeFilter := "*Rocket*",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "3.5.0",
     "edu.berkeley.cs" %% "chiseltest" % "0.5.0" % "test",
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-P:chiselplugin:useBundlePlugin"
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.4.3" cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )
