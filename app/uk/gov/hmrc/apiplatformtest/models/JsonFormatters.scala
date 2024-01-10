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

package uk.gov.hmrc.apiplatformtest.models

import play.api.libs.json.{Json, OFormat, Writes}
import uk.gov.hmrc.auth.core.retrieve._

object JsonFormatters {

  implicit val formatDummyAnswer: OFormat[DummyAnswer]                       = Json.format[DummyAnswer]
  implicit val formatNoFraudAnswer: OFormat[NoFraudAnswer]                   = Json.format[NoFraudAnswer]
  implicit val formatPrivilegedAccessAnswer: OFormat[PrivilegedAccessAnswer] = Json.format[PrivilegedAccessAnswer]
  implicit val formatName: OFormat[Name]                                     = Json.format[Name]
  implicit val formatCredentials: OFormat[Credentials]                       = Json.format[Credentials]
  implicit val formatAgentInformation: OFormat[AgentInformation]             = Json.format[AgentInformation]
  implicit val formatHeader: OFormat[Header]                                 = Json.format[Header]

  implicit val formatGGCredId: OFormat[GGCredId]                       = Json.format[GGCredId]
  implicit val formatVerifyPid: OFormat[VerifyPid]                     = Json.format[VerifyPid]
  implicit val formatPAClientId: OFormat[PAClientId]                   = Json.format[PAClientId]
  implicit val formatStandardApplication: OFormat[StandardApplication] = Json.format[StandardApplication]

  implicit val writeLegacyCredentials: Writes[LegacyCredentials] = Writes[LegacyCredentials] {
    case value: GGCredId            => Json.toJson(value)
    case value: VerifyPid           => Json.toJson(value)
    case value: PAClientId          => Json.toJson(value)
    case OneTimeLogin               => Json.toJson("")
    case value: StandardApplication => Json.toJson(value)
  }
}
