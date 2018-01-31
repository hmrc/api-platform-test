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
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.apiplatformtest.models.DummyAnswer
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatDummyAnswer
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future.successful

trait CommonController extends BaseController {

  implicit val hc: HeaderCarrier

  protected def result(status: Status, resource: String, request: Request[AnyContent]) = {
    val answer = DummyAnswer(uri = request.uri, method = request.method, resourceDetails = resource)
    successful(status(Json.toJson(answer)))
  }

  protected def success(resource: String)(implicit request: Request[AnyContent]) = {
    result(Ok, resource, request)
  }

  protected def failure(resource: String)(implicit request: Request[AnyContent]) = {
    result(NotImplemented, resource, request)
  }

}
