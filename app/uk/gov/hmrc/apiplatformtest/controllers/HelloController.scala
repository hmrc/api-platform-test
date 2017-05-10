/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

trait HelloController extends BaseController with HeaderValidator {
  implicit val hc: HeaderCarrier

  val response = Ok(Json.toJson("""{ "message": "Hello World" }"""))

  def get = validateAccept(acceptHeaderValidationRules).async {
    Future.successful(response)
  }

  def getWithOneParam(param: String) = validateAccept(acceptHeaderValidationRules).async {
    Future.successful(response)
  }

  def getWithTwoParams(param1: String, param2: String) = validateAccept(acceptHeaderValidationRules).async {
    Future.successful(response)
  }

  def post = validateAccept(acceptHeaderValidationRules).async {
    Future.successful(response)
  }

  def postWithOneParam(param: String) = validateAccept(acceptHeaderValidationRules).async {
    Future.successful(response)
  }

  def postWithTwoParams(param1: String, param2: String) = validateAccept(acceptHeaderValidationRules).async {
    Future.successful(response)
  }

}

object HelloController extends HelloController {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}