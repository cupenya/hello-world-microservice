import ReleaseTransformations._
import com.typesafe.sbt.packager.docker.Cmd

name          := """hello-world"""
organization  := "com.github.cupenya"
scalaVersion  := "2.11.8"

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

resolvers += Resolver.jcenterRepo
resolvers += "Cupenya Nexus" at "https://test.cupenya.com/nexus/content/groups/public"

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

scalacOptions := Seq(
  "-encoding", "utf8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-target:jvm-1.7",
  "-Xlog-reflective-calls",
  "-Ypatmat-exhaust-depth", "40",
  "-Xmax-classfile-name", "240", // for docker container
//      "-Xlog-implicits",
//      disable compiler switches for now, some of them make an issue with recompilations
  "-optimise"
//      "-Yclosure-elim",
//      "-Yinline",
//      "-Ybackend:GenBCode"
)

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
    <url>https://github.com/cupenya/hello-world-microservice</url>
    <connection>scm:git:git@github.com:cupenya/hello-world-microservice.git</connection>
  </scm>

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

mainClass in Compile := Some("com.github.cupenya.hello.Boot")

updateOptions := updateOptions.value.withCachedResolution(true)

// docker
enablePlugins(JavaServerAppPackaging)
enablePlugins(DockerPlugin)

publishArtifact in (Compile, packageDoc) := false

val shortCommit = ("git rev-parse --short HEAD" !!).replaceAll("\\n", "").replaceAll("\\r", "")

packageName in Docker := "cpy-docker-test/" + name.value
version in Docker     := shortCommit
dockerBaseImage       := "airdock/oracle-jdk:jdk-1.8"
defaultLinuxInstallLocation in Docker := s"/opt/${name.value}" // to have consistent directory for files
dockerRepository := Some("eu.gcr.io")

