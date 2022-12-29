import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {
  lazy val libraryDependencies = compile ++ test

  val bootstrapVersion = "7.12.0"

  private lazy val compile = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-backend-play-28"        % bootstrapVersion,
    "com.typesafe.play"             %% "play-json"                        % "2.9.2",
    "com.typesafe.play"             %% "play-json-joda"                   % "2.9.2",
    "uk.gov.hmrc"                   %% "domain"                           % "8.1.0-play-28"
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-test-play-28"           % bootstrapVersion,
    "org.mockito"                   %% "mockito-scala-scalatest"          % "1.16.42",
    "com.github.tomakehurst"        %  "wiremock-jre8-standalone"         % "2.27.1"
  ).map (_ % "test")
}

