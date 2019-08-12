/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apiplatformtest.config

import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import play.api.Mode.Mode
import uk.gov.hmrc.play.config.ServicesConfig

@Singleton
class AppContext @Inject()(override val runModeConfiguration: Configuration, environment: Environment) extends ServicesConfig {
  lazy val appName = runModeConfiguration.getString("appName").getOrElse(throw new RuntimeException("appName is not configured"))
  lazy val appUrl = runModeConfiguration.getString("appUrl").getOrElse(throw new RuntimeException("appUrl is not configured"))
  lazy val serviceLocatorUrl: String = baseUrl("service-locator")
  lazy val registrationEnabled: Boolean = runModeConfiguration.getBoolean(s"$env.microservice.services.service-locator.enabled").getOrElse(true)
  lazy val access = runModeConfiguration.getConfig("api.access")

  override protected def mode: Mode = environment.mode

}

