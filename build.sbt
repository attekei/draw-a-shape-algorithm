import com.typesafe.sbt._

import org.scalatra.sbt._
import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

val ScalatraVersion = "2.3.0"

name := "drawing-app-algoritm"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies += "org.specs2" %% "specs2" % "2.4.2" % "test"

libraryDependencies += "com.xeiam.xchart" % "xchart" % "2.3.0"

libraryDependencies += "org.apache.commons" %	"commons-math3"	% "3.3"

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.scalatra" %% "scalatra-swagger"  % ScalatraVersion,
  "com.wordnik"  %  "swagger-annotations" % "1.3.2",
  "com.novus" %% "salat-util" % "1.9.9",
  "com.novus" %% "salat-core" % "1.9.9",
  "org.json4s"   %% "json4s-jackson" % "3.2.9",
  "ch.qos.logback" % "logback-classic" % "1.0.13" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.1.5.v20140505" % "container;runtime;provided",
  "org.eclipse.jetty" % "jetty-plus" % "9.1.5.v20140505" % "container;runtime;compile",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "runtime;compile;provided;test" artifacts Artifact("javax.servlet-api", "jar", "jar")
)

ScalatraPlugin.scalatraWithJRebel

proguardSettings

ProguardKeys.options in Proguard ++= Seq(
  "-dontnote",
//  // http://viktorbresan.blogspot.fi/2012/10/conversion-to-dalvik-format-failed-with.html
  "-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable",
  "-dontobfuscate",
  "-keep class studies.algorithms.** { *; }")

instrumentSettings

coverallsSettings

mainClass in Compile := Some("studies.algorithms.api.JettyLauncher")