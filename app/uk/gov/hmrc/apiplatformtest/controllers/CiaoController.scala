/*
 * Copyright 2022 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, ControllerComponents}

@Singleton
class CiaoController @Inject()(cc: ControllerComponents) extends CommonController(cc) {

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

