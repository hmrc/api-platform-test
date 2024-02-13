import play.sbt.PlayImport._
import sbt._

object AppDependencies {
  lazy val libraryDependencies = compile ++ test

  val bootstrapVersion = "8.4.0"

  private lazy val compile = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-backend-play-30"        % bootstrapVersion,
    "org.playframework"             %% "play-json"                        % "3.0.2",
    "uk.gov.hmrc"                   %% "domain-play-30"                   % "9.0.0"
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-test-play-30"           % bootstrapVersion,
    "org.mockito"                   %% "mockito-scala-scalatest"          % "1.17.29"
  ).map (_ % "test")
}
