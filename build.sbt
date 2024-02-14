import play.sbt.PlayImport._
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import bloop.integrations.sbt.BloopDefaults

val appName = "api-platform-test"

lazy val playSettings: Seq[Setting[_]] = Seq.empty

scalaVersion := "2.13.12"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(ScoverageSettings())
  .settings(defaultSettings(): _*)
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")
  .settings(
    name := appName,
    majorVersion := 0,
    PlayKeys.playDefaultPort := 6704,
    libraryDependencies ++= AppDependencies.libraryDependencies,
    routesGenerator := InjectedRoutesGenerator,
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

commands ++= Seq(
  Command.command("run-all-tests") { state => "test" :: state },

  Command.command("clean-and-test") { state => "clean" :: "compile" :: "run-all-tests" :: state },

  // Coverage does not need compile !
  Command.command("pre-commit") { state => "clean" :: "scalafmtAll" :: "scalafixAll" :: "coverage" :: "run-all-tests" :: "coverageOff" :: "coverageAggregate" :: state }
)
