/*
 * Copyright 2023 HM Revenue & Customs
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
import scala.concurrent.duration.{Duration, FiniteDuration}

import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppContext @Inject() (runModeConfiguration: Configuration, environment: Environment, servicesConfig: ServicesConfig) {
  def mode: Mode                       = environment.mode
  lazy val appName                     = runModeConfiguration.getOptional[String]("appName").getOrElse(throw new RuntimeException("appName is not configured"))
  lazy val appUrl                      = runModeConfiguration.getOptional[String]("appUrl").getOrElse(throw new RuntimeException("appUrl is not configured"))
  lazy val access                      = runModeConfiguration.getOptional[Configuration]("api.access")
  lazy val assetsDelay: FiniteDuration = Duration(runModeConfiguration.getOptional[String]("assets.delay").getOrElse("0 sec")).asInstanceOf[FiniteDuration]
}
