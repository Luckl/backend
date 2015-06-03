name := "api"

organization := "com.leagueprojecto"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Mvn repository" at "http://mvnrepository.com/artifact/"

libraryDependencies ++= {
  val akkaVersion = "2.3.10"
  val akkaStreamVersion = "1.0-M5"
  val scalaTestVersion = "2.2.1"
  val logbackVersion = "1.1.2"
  val jsonPathVersion = "0.6.4"
  val jacksonCore = "2.5.3"
  val mockitoVersion = "1.10.19"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamVersion,
    "ch.qos.logback"    % "logback-classic" % logbackVersion,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamVersion % "test",
    "org.scalatest"     %% "scalatest" % scalaTestVersion % "test",
    "io.gatling"        % "jsonpath_2.11" % jsonPathVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonCore,
    "org.mockito"       % "mockito-all" % mockitoVersion

  )
}

mainClass in (Compile,run) := Some("com.leagueprojecto.api.Startup")

fork in run := true
