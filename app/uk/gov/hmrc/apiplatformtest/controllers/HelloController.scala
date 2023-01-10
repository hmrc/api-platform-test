/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.apiplatformtest.models.Header
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisationException, AuthorisedFunctions}
import uk.gov.hmrc.http.controllers.RestFormats.localDateFormats
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful
import uk.gov.hmrc.apiplatformtest.utils.ApplicationLogger

@Singleton
class HelloController @Inject()(override val authConnector: AuthConnector, cc: ControllerComponents)(implicit val ec: ExecutionContext)
  extends BackendController(cc) with AuthorisedFunctions with ApplicationLogger {


  def handle: Action[AnyContent] = Action.async { request =>
    logger.warn(s"Application ID: ${request.headers.get("x-application-id").getOrElse("Not Found")}")
    successful(Ok(Json.toJson("""{ "message": "Hello World" }""")))
  }

  def handleDave: Action[AnyContent] = Action.async { implicit request =>
    authorised().retrieve(allUserDetails and internalId and externalId and applicationId) {
      case credentials ~ name ~ dateOfBirth ~ postCode ~ email ~ affinityGroup ~ agentCode ~ agentInformation ~
        credentialRole ~ description ~ groupIdentifier ~  internalId ~ externalId ~ applicationId =>
        successful(Ok(Json.obj(
          "internalId" -> internalId,
          "externalId" -> externalId,
          "applicationId" -> applicationId,
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
          "headers" -> request.headers.headers.map(h => Header(h._1, h._2))
        )))
    } recover {
      case e: AuthorisationException => Unauthorized(Json.obj("errorMessage" -> e.getMessage))
    }
  }

  def handleBruce: Action[AnyContent] = Action.async { implicit request =>
    authorised().retrieve(authProviderId and credentials and clientId and applicationName and applicationId) {
      case authProviderId ~ credentials ~ clientId ~ applicationName ~ applicationId =>
        successful(Ok(
          Json.obj(
            "authProviderId" -> authProviderId,
            "credentials" -> credentials,
            "clientId" -> clientId,
            "applicationName" -> applicationName,
            "applicationId" -> applicationId)))
    } recover {
      case e: AuthorisationException => Unauthorized(Json.obj("errorMessage" -> e.getMessage))
    }
  }

  def handleWithParam(param: String): Action[AnyContent] = Action.async {
    successful(Ok(s"""{ "message": "$param" }"""))
  }

  def handleWithTwoParams(param1: String, param2: String): Action[AnyContent] = Action.async {
    successful(Ok(s"""{ "message": "$param1 / $param2" }"""))
  }
}
