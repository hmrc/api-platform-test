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
import uk.gov.hmrc.apiplatformtest.config.AuthClientAuthConnector
import uk.gov.hmrc.apiplatformtest.models.Header
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{allUserDetails, externalId, internalId}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisationException, AuthorisedFunctions}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful

trait HelloController extends BaseController with AuthorisedFunctions {

  override val authConnector: AuthConnector = AuthClientAuthConnector

  def handle: Action[AnyContent] = Action.async { request =>
    Logger.warn(s"Application ID: ${request.headers.get("x-application-id").getOrElse("Not Found")}")
    successful(Ok(Json.toJson("""{ "message": "Hello World" }""")))
  }

  def handleDave: Action[AnyContent] = Action.async { implicit request =>
    authorised().retrieve(allUserDetails and internalId and externalId) {
      case credentials ~ name ~ dateOfBirth ~ postCode ~ email ~ affinityGroup ~ agentCode ~ agentInformation ~
        credentialRole ~ description ~ groupIdentifier ~ unreadMessageCount ~ internalId ~ externalId =>
        successful(Ok(Json.obj(
          "internalId" -> internalId,
          "externalId" -> externalId,
          "credentials" -> credentials,
          "name" -> name,
          "dateOfBirth" -> dateOfBirth,
          "postCode" -> postCode,
          "email" -> email,
          "affinityGroup" -> affinityGroup,
          "agentCode" -> agentCode,
          "agentInformation" -> agentInformation,
          "credentialRole" -> credentialRole,
          "description" -> description,
          "groupIdentifier" -> groupIdentifier,
          "unreadMessageCount" -> unreadMessageCount,
          "headers" -> request.headers.headers.map(h => Header(h._1, h._2))
        )))
    } recover {
      case e: AuthorisationException => Unauthorized(Json.obj("errorMessage" -> e.getMessage))
    }
  }

  def handleWithParam(param: String): Action[AnyContent] = Action.async {
    successful(Ok(Json.toJson(s"""{ "message": "$param" }""")))
  }

  def handleWithTwoParams(param1: String, param2: String): Action[AnyContent] = Action.async {
    successful(Ok(Json.toJson(s"""{ "message": "$param1 / $param2" }""")))
  }
}

object HelloController extends HelloController
