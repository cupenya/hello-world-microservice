import ReleaseTransformations._
import com.typesafe.sbt.packager.docker.Cmd

name          := """hello-world-service"""
organization  := "com.github.cupenya"
scalaVersion  := "2.11.8"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  val akkaV            = "2.4.10"
  val scalaTestV       = "3.0.0-M15"
  val slf4sV           = "1.7.10"
  val logbackV         = "1.1.3"
  val commonsLang3V    = "3.4"
  val commonsCodecV    = "1.10"
  val jwtV             = "0.8.1"
  val guavaV           = "15.0"

  Seq(
    "com.typesafe.akka" %% "akka-http-core"                    % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental"            % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j"                        % akkaV,
    "org.slf4s"         %% "slf4s-api"                         % slf4sV,
    "com.pauldijou"     %% "jwt-core"                          % jwtV,
    "org.apache.commons"% "commons-lang3"                      % commonsLang3V,
    "commons-codec"     % "commons-codec"                      % commonsCodecV,
    "ch.qos.logback"    % "logback-classic"                    % logbackV,
    "org.scalatest"     %% "scalatest"                         % scalaTestV       % Test,
    "com.typesafe.akka" %% "akka-http-testkit"                 % akkaV            % Test
  )
}

val branch = "git rev-parse --abbrev-ref HEAD" !!
val cleanBranch = branch.toLowerCase.replaceAll(".*(cpy-[0-9]+).*", "$1").replaceAll("\\n", "").replaceAll("\\r", "")

lazy val dockerImageFromJava = Seq(
  packageName in Docker := "cpy-docker-test/" + name.value,
  version in Docker     := "latest",
  dockerBaseImage       := "airdock/oracle-jdk:jdk-1.8",
  dockerRepository      := Some("eu.gcr.io"),
  defaultLinuxInstallLocation in Docker := s"/opt/${name.value}", // to have consistent directory for files
  dockerCommands ++= Seq(
    Cmd("EXPOSE", "8080"),
    Cmd("LABEL", "resource=hello"),
    Cmd("LABEL", "name=hello-world-service")
  )
)

lazy val root = project.in(file(".")).settings(dockerImageFromJava)

Revolver.settings
enablePlugins(DockerPlugin, JavaAppPackaging)

initialCommands := """|import akka.actor._
                      |import akka.pattern._
                      |import akka.util._
                      |import scala.concurrent._
                      |import scala.concurrent.duration._""".stripMargin

publishMavenStyle := true
publishArtifact in Test := false
releasePublishArtifactsAction := PgpKeys.publishSigned.value
pomIncludeRepository := { _ => false }
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
pomExtra :=
  <url>https://github.com/cupenya/hello-world-service</url>
  <licenses>
    <license>
      <name>Apache-2.0</name>
      <url>http://opensource.org/licenses/Apache-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/cupenya/hello-world-service</url>
    <connection>scm:git:git@github.com:cupenya/hello-world-service.git</connection>
  </scm>
  <developers>
    <developer>
      <id>jeroenr</id>
      <name>Jeroen Rosenberg</name>
      <url>https://github.com/jeroenr/</url>
    </developer>
  </developers>

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)
