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

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Action
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

trait HelloController extends BaseController {

  implicit val hc: HeaderCarrier

  def handle = Action.async { implicit request =>
    def headersWeWant(headerName: String): Boolean = {
      val h = headerName.toUpperCase

      (h.contains("REQUEST") || h.contains("CLIENT")) && h.contains("ID")
    }

    Logger.info(s"Headers :${request.headers}")
    Future.successful(Ok(Json.toJson("""{ "message": "Hello World" }""")).withHeaders(request.headers.headers.filter(p => headersWeWant(p._1)): _*))
  }

  def handleWithParam(param: String) = Action.async {
    Future.successful(Ok(Json.toJson(s"""{ "message": "$param" }""")))
  }

  def handleWithTwoParams(param1: String, param2: String) = Action.async {
    Future.successful(Ok(Json.toJson(s"""{ "message": "$param1 / $param2" }""")))
  }
}

object HelloController extends HelloController {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}
