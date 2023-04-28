import scoverage.ScoverageKeys._
  
object ScoverageSettings {
  def apply() = Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    coverageExcludedPackages :=
        """.*definition.*;""" +
        """uk\.gov\.hmrc\.BuildInfo;""" +
        """.*\.Routes;""" +
        """.*\.RoutesPrefix;""" +
        """.*Filters?;""" +
        """MicroserviceAuditConnector;""" +
        """Module;""" +
        """GraphiteStartUp;""" +
        """.*\.Reverse[^.]*""",
    coverageMinimumStmtTotal := 80.00,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )
}
