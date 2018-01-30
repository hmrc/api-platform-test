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
import play.api.mvc.{Action, AnyContent, Request}
import uk.gov.hmrc.apiplatformtest.models.CiaoAnswer
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatCiaoAnswer
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future.successful

trait CiaoController extends BaseController {

  implicit val hc: HeaderCarrier

  private def result(status: Status, resource: String, request: Request[AnyContent]) = {
    val ciaoAnswer = CiaoAnswer(uri = request.uri, resourceDetails = resource)
    successful(status(Json.toJson(ciaoAnswer)))
  }

  private def success(resource: String)(implicit request: Request[AnyContent]) = {
    result(Ok, resource, request)
  }

  private def failure(resource: String)(implicit request: Request[AnyContent]) = {
    result(NotImplemented, resource, request)
  }

  // Successful routes.
  // These resources/endpoints are published to WSO2.

  def handleEmpty(): Action[AnyContent] =
    Action.async { implicit request => success("Ciao Empty!") }

  def handleCiao(surname: Option[String], firstName: Option[String], middleName: Option[String]): Action[AnyContent] =
    Action.async { implicit request => success(s"Ciao: $surname, $firstName, $middleName") }

  def handleCiaoSurname(surname: String): Action[AnyContent] =
    Action.async { implicit request => success(s"Ciao Surname: $surname") }

  def handleCiaoFullName(surname: String, firstName: String, middleName: Option[String]): Action[AnyContent] =
    Action.async { implicit request => success(s"Ciao Full Name: $surname, $firstName, $middleName") }

  // Expected failing routes.
  // These resources/endpoints are (deliberately) not published to WSO2.
  // Thus, if requests matching these routes are sent to WSO2, WSO2 should block them and answer with 404 MATCHING_RESOURCE_NOT_FOUND.

  def handleNotImplemented(): Action[AnyContent] =
  Action.async { implicit request => failure("Ciao Not Implemented!") }

}

object CiaoController extends CiaoController {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}
