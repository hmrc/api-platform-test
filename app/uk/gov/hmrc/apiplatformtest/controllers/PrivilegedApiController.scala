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

package uk.gov.hmrc.apiplatformtest.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatPrivilegedAccessAnswer
import uk.gov.hmrc.apiplatformtest.models.PrivilegedAccessAnswer
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.Future.successful

trait PrivilegedApiController extends BaseController with AuthorisedFunctions {


  private def fromPrivilegedApplication: Result = {
    Ok(Json.toJson(PrivilegedAccessAnswer("Request coming from a privileged application")))
  }

  private def fromNonPrivilegedApplication: Result = {
    Forbidden(Json.toJson(PrivilegedAccessAnswer("Only privileged applications can access this endpoint")))
  }

  def handlePrivilegedAccess(): Action[AnyContent] = Action.async { implicit request =>
    authorised(AuthProviders(PrivilegedApplication)) {
      successful(fromPrivilegedApplication)
    } recover {
      case _: UnsupportedAuthProvider => fromNonPrivilegedApplication
    }
  }

}

