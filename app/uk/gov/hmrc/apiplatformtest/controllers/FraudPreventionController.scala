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
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatNoFraudAnswer
import uk.gov.hmrc.apiplatformtest.models.NoFraudAnswer
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidatorActionFilter
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future.successful

trait FraudPreventionController extends CommonController {

  override implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy private val requiredHeaders = List("Gov-Client-Public-Port")
  lazy private val fraudPreventionFilter = AntiFraudHeadersValidatorActionFilter.actionFilterFromHeaderNames(requiredHeaders)


  def handleFraud(): Action[AnyContent] = fraudPreventionFilter.async { implicit request =>
    successful(
      Ok(Json.toJson(NoFraudAnswer("All required headers have been sent correctly in the request.")))
    )
  }

}

object FraudPreventionController extends FraudPreventionController
