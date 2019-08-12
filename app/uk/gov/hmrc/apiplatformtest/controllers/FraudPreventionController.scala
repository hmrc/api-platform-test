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

import javax.inject.Singleton
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Request, Result}
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatNoFraudAnswer
import uk.gov.hmrc.apiplatformtest.models.NoFraudAnswer
import uk.gov.hmrc.fraudprevention.headervalidators.impl._
import uk.gov.hmrc.fraudprevention.model.{ErrorConversion, ErrorResponse}
import uk.gov.hmrc.fraudprevention.{AntiFraudHeadersValidator, AntiFraudHeadersValidatorActionFilter}

import scala.concurrent.Future
import scala.concurrent.Future.successful

@Singleton
class FraudPreventionController extends ErrorConversion with CommonController {

  lazy val requiredHeaderValidators = List(GovClientColourDepthHeaderValidator, GovClientPublicPortHeaderValidator)
  lazy private val fraudPreventionFilter = AntiFraudHeadersValidatorActionFilter.actionFilterFromHeaderValidators(requiredHeaderValidators)
  lazy private val filterWithDefaultHeaders = AntiFraudHeadersValidatorActionFilter.actionFilterWithDefaultHeaders

  private def success: Future[Result] = {
    successful(
      Ok(Json.toJson(NoFraudAnswer("All required headers have been sent correctly in the request.")))
    )
  }

  def handleFraudWithDefaultHeadersFilter(): Action[AnyContent] = filterWithDefaultHeaders.async { implicit request: Request[AnyContent] =>
    success
  }

  def handleFraudWithFilter(): Action[AnyContent] = fraudPreventionFilter.async { implicit request: Request[AnyContent] =>
    success
  }

  def handleFraud(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    AntiFraudHeadersValidator.validate(requiredHeaderValidators)(request) match {
      case Right(_) => success
      case Left(errors: List[String]) => successful(ErrorResponse(errors))
    }

  }

}

