import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {
  lazy val libraryDependencies = compile ++ test

  private lazy val compile = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-backend-play-28"        % "5.15.0",
    "com.typesafe.play"             %% "play-json"                        % "2.9.2",
    "com.typesafe.play"             %% "play-json-joda"                   % "2.9.2",
    "uk.gov.hmrc"                   %% "domain"                           % "6.2.0-play-28",
    "ch.qos.logback"                %  "logback-classic"                  % "1.2.3",
    "uk.gov.hmrc"                   %% "logback-json-logger"              % "4.6.0"
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-test-play-28"           % "5.15.0",
    "org.mockito"                   %% "mockito-scala-scalatest"          % "1.16.42",
    "com.github.tomakehurst"        %  "wiremock-jre8-standalone"         % "2.27.1"
  ).map (_ % "test")
}