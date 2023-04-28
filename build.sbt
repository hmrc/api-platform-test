
import play.core.PlayVersion
import play.sbt.PlayImport._
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import bloop.integrations.sbt.BloopDefaults

val appName = "api-platform-test"

lazy val playSettings: Seq[Setting[_]] = Seq.empty

scalaVersion := "2.13.8"

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(ScoverageSettings())
  .settings(defaultSettings(): _*)
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")
  .settings(
    name := appName,
    majorVersion := 0,
    PlayKeys.playDefaultPort := 6704,
    libraryDependencies ++= AppDependencies.libraryDependencies,
    routesGenerator := InjectedRoutesGenerator,
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    Global / bloopAggregateSourceDependencies := true
  )
  .settings(
    inConfig(Test)(BloopDefaults.configSettings),
    Test / testOptions := Seq(Tests.Filter(_ => true)),// this removes duplicated lines in HTML reports
    Test / unmanagedSourceDirectories += baseDirectory.value / "test" / "unit", // TODO remove unit from tests
    Test / unmanagedSourceDirectories += baseDirectory.value / "test-common",
    addTestReportOption(Test, "test-reports")
  )
  .settings(
    scalacOptions ++= Seq(
      "-Wconf:cat=unused&src=views/.*\\.scala:s",
      "-Wconf:cat=unused&src=.*RoutesPrefix\\.scala:s",
      "-Wconf:cat=unused&src=.*Routes\\.scala:s",
      "-Wconf:cat=unused&src=.*ReverseRoutes\\.scala:s"
    )
  )