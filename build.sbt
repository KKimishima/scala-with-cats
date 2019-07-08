//lazy val root = (project in file("."))
//  .settings(
//    name := "scala-with-cats",
//    version := "0.1",
//    sbtPlugin := true,
////    scalaVersion := "2.13.0",
//    scalacOptions ++= Seq(
//      "-encoding", "UTF-8",   // source files are in UTF-8
//      "-deprecation",         // warn about use of deprecated APIs
//      "-unchecked",           // warn about unchecked type parameters
//      "-feature",             // warn about misused language features
//      "-language:higherKinds",// allow higher kinded types without `import scala.language.higherKinds`
//      "-Xlint",               // enable handy linter warnings
//      "-Xfatal-warnings",     // turn compiler warnings into errors
//      "-Ypartial-unification" // allow the compiler to unify type constructors of different arities
//    )
//  )

name := "scala-with-cats"
version := "0.1"
//scalaVersion := "2.12.7"
scalaVersion := "2.13.0"
scalacOptions ++= Seq(
  "-encoding", "UTF-8", // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked", // warn about unchecked type parameters
  "-feature", // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-Xlint", // enable handy linter warnings
  "-Xfatal-warnings", // turn compiler warnings into errors
  //  "-Ypartial-unification" // allow the compiler to unify type constructors of different arities
)

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0-M4"
//libraryDependencies += "org.typelevel" %% "cats-core" % "1.4.0"