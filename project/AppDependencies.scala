import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {
  lazy val libraryDependencies = compile ++ test

  private lazy val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % "1.16.0",
    "uk.gov.hmrc" %% "domain" % "5.6.0-play-26",
    "uk.gov.hmrc" %% "auth-client" % "2.35.0-play-26",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "uk.gov.hmrc" %% "logback-json-logger" % "4.6.0"
  )

  private lazy val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-play-26" % "1.3.0",
    "uk.gov.hmrc" %% "hmrctest" % "3.9.0-play-26",
    "org.scalatest" %% "scalatest" % "3.0.5",
    "org.pegdown" % "pegdown" % "1.6.0",
    "com.typesafe.play" %% "play-test" % PlayVersion.current,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2",
    "org.mockito" % "mockito-core" % "2.13.0",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.26.3"
  ).map (m => m % "test")
}