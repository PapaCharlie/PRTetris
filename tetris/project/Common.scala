import sbt._
import sbt.Keys._

object Common {

  val akkaV = "2.3.9"

  val dependencies = Seq(
    // "com.typesafe.akka"   %%  "akka-actor"        % akkaV,
    // "com.typesafe.akka"   %%  "akka-testkit"      % akkaV   % "test",
    "org.apache.spark"        %%  "spark-core"      % "1.5.1"   % "provided",
    "commons-io"              %   "commons-io"      % "2.4",
    // "org.scala-lang.modules"  %%  "scala-pickling"  % "0.10.1",
    "org.specs2"              %%  "specs2-core"     % "2.3.11"  % "test",
    "org.scalatest"           %%  "scalatest"       % "2.2.4"   % "test"
    // "org.scalacheck"          %%  "scalacheck"      % "1.12.5"  % "test"
  )

  val names: Seq[Setting[_]] = Seq(
    version := "1.1",
    scalaVersion := "2.11.8",
    organization := "chesnais.paul"
  )

  val settings: Seq[Setting[_]] = names ++ Seq(
    libraryDependencies ++= dependencies,
    resolvers += "softprops-maven" at "https://dl.bintray.com/content/softprops/maven",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xlint",
      "-Ywarn-unused-import"
    ),
    javaOptions ++= Seq("-d64", "-Xms1g", "-Xmx4g")
  )
}
