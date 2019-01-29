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

import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier


object HeadersControllerDomain {
  case class IdField(name: String, value: String)
  case class Payload(request: IdField, client: IdField)

  implicit val idFieldJF = Json.format[IdField]
  implicit val payloadJF = Json.format[Payload]
}


trait HeadersController extends BaseController {
  import HeadersControllerDomain._

  implicit val hc: HeaderCarrier

  def handle: Action[AnyContent] = Action.async { implicit request =>

    def headersWeWant(headerName: String): Boolean = {
      val h = headerName.toUpperCase

      (h.contains("REQUEST") || h.contains("CLIENT")) && h.contains("ID")
    }

    def findIdHeader(headerName: String): (String, String) = {
      request.headers.headers
        .find(p => p._1.toUpperCase.contains(headerName.toUpperCase) && p._1.toUpperCase.endsWith("ID"))
        .getOrElse( ("Absent", "-") )
    }

    val requestId = findIdHeader("REQUEST")
    val clientId = findIdHeader("CLIENT")
    val response = Payload(IdField(requestId._1, requestId._2), IdField(clientId._1, clientId._2))

    Future.successful(Ok(Json.toJson(response))
      .withHeaders(request.headers.headers.filter(p => headersWeWant(p._1)): _*))
  }

  def handleWithParam(param: String): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(s"""{ "message": "$param" }""")))
  }

  def handleWithTwoParams(param1: String, param2: String): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(s"""{ "message": "$param1 / $param2" }""")))
  }
}

object HeadersController extends HeadersController {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}
