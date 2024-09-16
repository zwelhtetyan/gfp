scalaVersion := "3.4.2"
libraryDependencies ++= Seq(
  "org.typelevel"     %% "cats-effect"      % "3.5.4",
  "co.fs2"            %% "fs2-core"         % "3.10.2",
  "org.scalatest"     %% "scalatest"        % "3.2.19"   % Test,
  "org.scalatestplus" %% "scalacheck-1-16"  % "3.2.14.0" % Test,
  // imperative libraries:
  "com.typesafe.akka"  % "akka-actor_2.13"  % "2.6.20",
  "org.apache.jena"    % "apache-jena-libs" % "5.1.0",
  "org.apache.jena"    % "jena-fuseki-main" % "5.1.0",
  "org.slf4j"          % "slf4j-nop"        % "2.0.16"
)
initialCommands := s"""
  import fs2._, cats.effect._, cats.implicits._, cats.effect.unsafe.implicits.global
  import scala.concurrent.duration._, java.util.concurrent._
  import scala.jdk.javaapi.CollectionConverters.asScala
  import org.apache.jena.query._, org.apache.jena.rdfconnection._
"""