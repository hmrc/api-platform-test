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

package uk.gov.hmrc.apiplatformtest.models

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.auth.core.retrieve._

object JsonFormatters {

  implicit val formatDummyAnswer = Json.format[DummyAnswer]
  implicit val formatNoFraudAnswer = Json.format[NoFraudAnswer]
  implicit val formatPrivilegedAccessAnswer = Json.format[PrivilegedAccessAnswer]
  implicit val formatName = Json.format[Name]
  implicit val formatCredentials = Json.format[Credentials]
  implicit val formatAgentInformation = Json.format[AgentInformation]
  implicit val formatHeader = Json.format[Header]

  implicit val formatGGCredId = Json.format[GGCredId]
  implicit val formatVerifyPid = Json.format[VerifyPid]
  implicit val formatPAClientId = Json.format[PAClientId]
  implicit val formatStandardApplication = Json.format[StandardApplication]
  implicit val writeLegacyCredentials = Writes[LegacyCredentials] {
    case value: GGCredId => Json.toJson(value)
    case value: VerifyPid => Json.toJson(value)
    case value: PAClientId => Json.toJson(value)
    case OneTimeLogin => Json.toJson("")
    case value: StandardApplication => Json.toJson(value)
  }
}
