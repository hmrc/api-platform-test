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

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

trait HelloController extends BaseController {

  implicit val hc: HeaderCarrier

  def handle: Action[AnyContent] = Action.async { request =>
    Logger.warn(s"Application ID: ${request.headers.get("x-application-id").getOrElse("Not Found")}")
    Future.successful(Ok(Json.toJson(s"""{ "message": "Hello Application ${request.headers.get("x-application-id").getOrElse("Not Found")}" }""")))
  }

  def handleWithParam(param: String): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(s"""{ "message": "$param" }""")))
  }

  def handleWithTwoParams(param1: String, param2: String): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(s"""{ "message": "$param1 / $param2" }""")))
  }
}

object HelloController extends HelloController {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}
