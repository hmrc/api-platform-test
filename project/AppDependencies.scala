import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {
  lazy val libraryDependencies = compile ++ test

  private lazy val compile = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-play-26"        % "4.0.0",
    "uk.gov.hmrc"                   %% "domain"                   % "5.6.0-play-26",
    "ch.qos.logback"                %  "logback-classic"          % "1.2.3",
    "uk.gov.hmrc"                   %% "logback-json-logger"      % "4.6.0"
  )

  private lazy val test = Seq(
    "org.pegdown"                   %  "pegdown"                  % "1.6.0",
    "com.typesafe.play"             %% "play-test"                % PlayVersion.current,
    "org.scalatestplus.play"        %% "scalatestplus-play"       % "3.1.3",
    "org.mockito"                   %% "mockito-scala-scalatest"  % "1.7.1",
    "com.github.tomakehurst"        %  "wiremock-jre8-standalone" % "2.27.1"
  ).map (m => m % "test")
}