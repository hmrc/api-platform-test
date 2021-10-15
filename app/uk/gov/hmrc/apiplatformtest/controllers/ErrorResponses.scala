/*
 * Copyright 2021 HM Revenue & Customs
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

sealed abstract class ErrorResponse(val httpStatusCode: Int,
                                    val errorCode: String,
                                    val message: String)

case object ErrorSaUtrInvalid extends ErrorResponse(400,"SA_UTR_INVALID", "The provided SA UTR is invalid")
case object ErrorTaxYearInvalid extends ErrorResponse(400,"TAX_YEAR_INVALID", "The provided Tax Year is invalid")
case object ErrorUnauthorized extends ErrorResponse(401,"UNAUTHORIZED", "Bearer token is missing or not authorized")
case object ErrorNotFound extends ErrorResponse(404,"NOT_FOUND", "Resource was not found")
case object ErrorGenericBadRequest extends ErrorResponse(400,"BAD_REQUEST", "Bad Request")
case object ErrorAcceptHeaderInvalid extends ErrorResponse(406,"ACCEPT_HEADER_INVALID", "The accept header is missing or invalid")
case object ErrorInternalServerError extends ErrorResponse(500,"INTERNAL_SERVER_ERROR", "Internal server error")
case object ErrorGatewayTimeout extends ErrorResponse(504,"GATEWAY_TIMEOUT", "The request has timed out")

object ErrorResponse {
  import play.api.libs.json.Json

  implicit val errorAcceptHeaderInvalid = Json.format[ErrorAcceptHeaderInvalid.type]
  implicit val errorInternalServerErrorFormat = Json.format[ErrorInternalServerError.type]
}
