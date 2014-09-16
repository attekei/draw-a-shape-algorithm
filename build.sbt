name := "drawing-app-algoritm"
 
version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies += "org.specs2" %% "specs2" % "2.4.2" % "test"

libraryDependencies += "com.xeiam.xchart" % "xchart" % "2.3.0"

libraryDependencies += "org.apache.commons" %	"commons-math3"	% "3.3"

proguardSettings

ProguardKeys.options in Proguard ++= Seq(
  "-dontnote",
  // http://viktorbresan.blogspot.fi/2012/10/conversion-to-dalvik-format-failed-with.html
  "-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable",
  "-dontobfuscate",
  "-keep class studies.algorithms.** { *; }")

instrumentSettings

coverallsSettings