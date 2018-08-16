/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.apiplatformtest.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.apiplatformtest.config.AuthClientAuthConnector
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.{AuthConnector, AuthProviders, AuthorisedFunctions}
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future.successful

trait PrivilegedApiController extends BaseController with AuthorisedFunctions {
  override val authConnector: AuthConnector = AuthClientAuthConnector

  def handlePrivilegedAccess(): Action[AnyContent] = Action.async { implicit request =>
    authorised(AuthProviders(PrivilegedApplication)) {
      successful(
        Ok(Json.toJson("Request coming from a privileged application"))
      )
    }
  }
    // TODO: add recover ( 401) for non priv apps
    // TODO: add unit tests
    // TODO: add acceptance tests
}

object PrivilegedApiController extends PrivilegedApiController